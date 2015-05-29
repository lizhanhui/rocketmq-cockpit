package com.ndpmedia.rocketmq.cockpit.scheduler.command;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.common.protocol.body.ClusterInfo;
import com.alibaba.rocketmq.common.protocol.body.TopicList;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.remoting.RPCHook;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by robert on 2015/5/29.
 */
public class SyncTopicCommand implements SubCommand {

    private Set<String> brokerList = new HashSet<>();

    @Override
    public String commandName() {
        return "syncTopic";
    }

    @Override
    public String commandDesc() {
        return " try to sync topic information ";
    }

    @Override
    public Options buildCommandlineOptions(Options options) {
        return null;
    }

    @Override
    public void execute(CommandLine commandLine, Options options, RPCHook rpcHook) {
        DefaultMQAdminExt adminExt = new DefaultMQAdminExt();
        adminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try{
            adminExt.start();
            doList(adminExt);

            TopicList topics = adminExt.fetchAllTopicList();
            for (String topicName:topics.getTopicList()){
                if (topicName.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX) || topicName.startsWith(MixAll.DLQ_GROUP_TOPIC_PREFIX))
                    continue;

                TopicConfig topicConfig = getTopicConfig(adminExt, topicName);
                if (null != topicConfig)
                    rebuildTopicConfig(adminExt, topicConfig);
            }
        }catch (Exception e){

        }
    }

    private void doList(DefaultMQAdminExt defaultMQAdminExt) throws Exception {
        System.out.println("[sync topic] try to get broker list");
        ClusterInfo clusterInfoSerializeWrapper = defaultMQAdminExt.examineBrokerClusterInfo();

        Set<Map.Entry<String, Set<String>>> clusterSet =
                clusterInfoSerializeWrapper.getClusterAddrTable().entrySet();

        for (Map.Entry<String, Set<String>> next : clusterSet) {
            String clusterName = next.getKey();
            Set<String> brokerNameSet = new HashSet<String>();
            brokerNameSet.addAll(next.getValue());


            for (String brokerName : brokerNameSet) {
                BrokerData brokerData = clusterInfoSerializeWrapper.getBrokerAddrTable().get(brokerName);
                if (brokerData != null) {
                    Set<Map.Entry<Long, String>> brokerAddrSet = brokerData.getBrokerAddrs().entrySet();
                    Iterator<Map.Entry<Long, String>> itAddr = brokerAddrSet.iterator();

                    while (itAddr.hasNext()) {
                        Map.Entry<Long, String> next1 = itAddr.next();
                        brokerList.add(next1.getValue());
                    }
                }
            }
        }

        System.out.println("[sync topic] now we get broker list , size : " + brokerList.size() + " [] " + brokerList);
    }

    private TopicConfig getTopicConfig(DefaultMQAdminExt defaultMQAdminExt, String topic){
        for (String broker:brokerList) {
            TopicConfig topicConfig = defaultMQAdminExt.examineTopicConfig(broker, topic);
            if (null != topicConfig && null != topicConfig.getTopicName() && !topicConfig.getTopicName().isEmpty())
                return topicConfig;
        }
        System.err.println("[sync topic] big error! find topic but no topic config !");
        return null;
    }

    private void rebuildTopicConfig(DefaultMQAdminExt defaultMQAdminExt, TopicConfig topicConfig) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        for (String broker:brokerList){
            defaultMQAdminExt.createAndUpdateTopicConfig(broker, topicConfig);
        }
    }
}
