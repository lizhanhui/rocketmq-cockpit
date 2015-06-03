package com.ndpmedia.rocketmq.cockpit.scheduler.command;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.common.protocol.body.ClusterInfo;
import com.alibaba.rocketmq.common.protocol.body.TopicList;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.common.protocol.route.QueueData;
import com.alibaba.rocketmq.common.protocol.route.TopicRouteData;
import com.alibaba.rocketmq.remoting.RPCHook;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by robert on 2015/5/29.
 */
public class DownTopicCommand implements SubCommand {

    @Autowired
    private TopicMapper topicMapper;

    private Map<String, String> brokerToCluster = new HashMap<>();

    @Override
    public String commandName() {
        return "downTopic";
    }

    @Override
    public String commandDesc() {
        return " try to download topic information ";
    }

    @Override
    public Options buildCommandlineOptions(Options options) {
        Option opt = new Option("b", "brokerAddr", true, "from which broker");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("c", "clusterName", true, "from which cluster");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("t", "topic", true, "subscription topic ");
        opt.setRequired(false);
        options.addOption(opt);

        return options;
    }

    @Override
    public void execute(CommandLine commandLine, Options options, RPCHook rpcHook) {
        DefaultMQAdminExt adminExt = new DefaultMQAdminExt();
        adminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        try {
            adminExt.start();
            doMap(adminExt);

            TopicList topics = adminExt.fetchAllTopicList();
            for (String topicName : topics.getTopicList()) {
                if (topicName.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX) || topicName.startsWith(MixAll.DLQ_GROUP_TOPIC_PREFIX))
                    continue;
                System.out.println("now we check :" + topicName);
                TopicConfig topicConfig = getTopicConfig(adminExt, topicName);
                if (null != topicConfig)
                    downloadTopicConfig(adminExt, topicConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            adminExt.shutdown();
        }
    }

    private void doMap(DefaultMQAdminExt defaultMQAdminExt) {
        System.out.println("[sync topic] try to get broker list");
        boolean flag = true;
        ClusterInfo clusterInfoSerializeWrapper = new ClusterInfo();
        while(flag) {
            try {
                clusterInfoSerializeWrapper = defaultMQAdminExt.examineBrokerClusterInfo();
                flag = false;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Set<Map.Entry<String, Set<String>>> clusterSet =
                clusterInfoSerializeWrapper.getClusterAddrTable().entrySet();

        for (Map.Entry<String, Set<String>> next : clusterSet) {
            Set<String> brokerNameSet = new HashSet<String>();
            brokerNameSet.addAll(next.getValue());
            String cluster = next.getKey();


            for (String brokerName : brokerNameSet) {
                BrokerData brokerData = clusterInfoSerializeWrapper.getBrokerAddrTable().get(brokerName);
                if (brokerData != null) {
                    Set<Map.Entry<Long, String>> brokerAddrSet = brokerData.getBrokerAddrs().entrySet();
                    Iterator<Map.Entry<Long, String>> itAddr = brokerAddrSet.iterator();

                    while (itAddr.hasNext()) {
                        Map.Entry<Long, String> next1 = itAddr.next();
                        brokerToCluster.put(next1.getValue(), cluster);
                    }
                }
            }
        }

        System.out.println("[sync topic] now we get broker list , size : " + brokerToCluster.size() + " [] " + brokerToCluster);
    }

    private TopicConfig getTopicConfig(DefaultMQAdminExt defaultMQAdminExt, String topic) {
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setTopicName(topic);

        TopicRouteData topicRouteData = new TopicRouteData();
        boolean flag = true;
        while (flag){
            try {
                topicRouteData = defaultMQAdminExt.examineTopicRouteInfo(topic);
                flag = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<QueueData> lists = topicRouteData.getQueueDatas();

        int readQ = 0;
        int writeQ = 0;
        int perm = 0;
        for (QueueData queueData:lists){
            readQ = Math.max(readQ, queueData.getReadQueueNums());
            writeQ = Math.max(writeQ, queueData.getWriteQueueNums());
            perm = Math.max(perm, queueData.getPerm());
            topicConfig.setTopicSysFlag(queueData.getTopicSynFlag());
        }
        topicConfig.setWriteQueueNums(writeQ);
        topicConfig.setReadQueueNums(readQ);
        topicConfig.setPerm(perm);

        if (null != topicConfig && null != topicConfig.getTopicName() && !topicConfig.getTopicName().isEmpty())
            return topicConfig;

        System.err.println("[sync topic] big error! find topic but no topic config !");
        return null;
    }

    private void downloadTopicConfig(DefaultMQAdminExt defaultMQAdminExt, TopicConfig topicConfig) {
        TopicRouteData topicRouteData = null;
        boolean flag = true;
        while (flag) {
            try {
                topicRouteData = defaultMQAdminExt.examineTopicRouteInfo(topicConfig.getTopicName());
                flag = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<BrokerData> brokerDatas = topicRouteData.getBrokerDatas();
        Set<String> brokers = new HashSet<>();

        for (BrokerData brokerData:brokerDatas){
            for (Map.Entry<Long, String> entry:brokerData.getBrokerAddrs().entrySet()){
                brokers.add(entry.getValue());
            }
        }

        for (String broker : brokers) {
            flag =true;
            while (flag) {
                try {
                    Topic topic = getTopic(topicConfig, broker);
                    topicMapper.insert(topic);
                    flag = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println("[sync topic]add topic config:" + topicConfig + " to broker :" + broker);
        }
    }

    private Topic getTopic(TopicConfig topicConfig, String broker) {
        Topic topic = new Topic();
        topic.setBrokerAddress(broker);
        topic.setClusterName(brokerToCluster.get(broker));
        topic.setCreateTime(new Date());
        topic.setOrder(topicConfig.isOrder());
        topic.setPermission(topicConfig.getPerm());
        topic.setReadQueueNum(topicConfig.getReadQueueNums());
        topic.setWriteQueueNum(topicConfig.getWriteQueueNums());
        topic.setStatus(Status.ACTIVE);
        topic.setTopic(topicConfig.getTopicName());
        topic.setUpdateTime(new Date());
        return topic;
    }
}
