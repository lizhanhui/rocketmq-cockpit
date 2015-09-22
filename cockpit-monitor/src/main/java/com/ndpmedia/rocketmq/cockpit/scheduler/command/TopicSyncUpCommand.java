package com.ndpmedia.rocketmq.cockpit.scheduler.command;

import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.remoting.RPCHook;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;
import com.ndpmedia.rocketmq.cockpit.exception.CockpitException;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicRocketMQService;
import com.ndpmedia.rocketmq.cockpit.service.impl.CockpitBrokerServiceImpl;
import com.ndpmedia.rocketmq.cockpit.service.impl.CockpitTopicRocketMQServiceImpl;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.HashSet;
import java.util.Set;

public class TopicSyncUpCommand implements SubCommand {

    private Set<String> brokerList = new HashSet<>();

    private Set<String> nameList = new HashSet<>();

    private CockpitTopicRocketMQService cockpitTopicRocketMQService = new CockpitTopicRocketMQServiceImpl();

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
            doBaseServer(adminExt);

            Set<String> topics = cockpitTopicRocketMQService.fetchAllTopics(adminExt, true);

            for (String topicName : topics) {
                System.out.println("now we check :" + topicName);
                if (nameList.contains(topicName)){
                    System.out.println("[notice] this topic name is " + topicName);
                    continue;
                }
                TopicConfig topicConfig = getTopicConfig(adminExt, topicName);
                if (null != topicConfig)
                    rebuildTopicConfig(adminExt, topicConfig, commandLine);
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

    private void doBaseServer(DefaultMQAdminExt defaultMQAdminExt){
        nameList.addAll(cockpitBrokerService.getAllNames(defaultMQAdminExt));
    }

    private TopicConfig getTopicConfig(DefaultMQAdminExt defaultMQAdminExt, String topic) throws CockpitException {
        return cockpitTopicRocketMQService.getTopicConfigByTopicName(defaultMQAdminExt, topic);
    }

    private void rebuildTopicConfig(DefaultMQAdminExt defaultMQAdminExt, TopicConfig topicConfig, CommandLine commandLine) throws CockpitException {
        if (null != commandLine.getOptionValue('b') && commandLine.getOptionValue('b').trim().length() > 0) {
            cockpitTopicRocketMQService.rebuildTopicConfig(defaultMQAdminExt, topicConfig, commandLine.getOptionValue('b').trim());
        } else {
            for (String broker : brokerList) {
                cockpitTopicRocketMQService.rebuildTopicConfig(defaultMQAdminExt, topicConfig, broker);
            }
        }
    }
}
