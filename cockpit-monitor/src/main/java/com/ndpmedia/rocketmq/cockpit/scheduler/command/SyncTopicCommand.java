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
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
import com.ndpmedia.rocketmq.cockpit.service.impl.CockpitBrokerServiceImpl;
import com.ndpmedia.rocketmq.cockpit.service.impl.CockpitTopicServiceImpl;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.*;

/**
 * Created by robert on 2015/5/29.
 */
public class SyncTopicCommand implements SubCommand {

    private Set<String> brokerList = new HashSet<>();

    private CockpitTopicService cockpitTopicService = new CockpitTopicServiceImpl();

    private CockpitBrokerService cockpitBrokerService = new CockpitBrokerServiceImpl();

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
            doList(adminExt);

            Set<String> topics = cockpitTopicService.getTopics(adminExt);

            for (String topicName : topics) {
                System.out.println("now we check :" + topicName);
                TopicConfig topicConfig = getTopicConfig(adminExt, topicName);
                if (null != topicConfig)
                    rebuildTopicConfig(adminExt, topicConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            adminExt.shutdown();
        }
    }

    private void doList(DefaultMQAdminExt defaultMQAdminExt) {
        brokerList.addAll(cockpitBrokerService.getALLBrokers(defaultMQAdminExt));
    }

    private TopicConfig getTopicConfig(DefaultMQAdminExt defaultMQAdminExt, String topic) {
        return cockpitTopicService.getTopicConfigByTopicName(defaultMQAdminExt, topic);
    }

    private void rebuildTopicConfig(DefaultMQAdminExt defaultMQAdminExt, TopicConfig topicConfig) {
        Set<String> localBroker = cockpitTopicService.getTopicBrokers(defaultMQAdminExt, topicConfig.getTopicName());

        for (String broker : brokerList) {
            if (localBroker.contains(broker))
                continue;

            boolean flag =true;
            while (flag) {
                try {
                    defaultMQAdminExt.createAndUpdateTopicConfig(broker, topicConfig);
                    flag = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println("[sync topic]add topic config:" + topicConfig + " to broker :" + broker);
        }
    }
}
