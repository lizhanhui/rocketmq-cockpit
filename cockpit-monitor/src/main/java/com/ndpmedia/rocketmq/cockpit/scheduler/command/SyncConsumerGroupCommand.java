package com.ndpmedia.rocketmq.cockpit.scheduler.command;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.protocol.body.Connection;
import com.alibaba.rocketmq.common.protocol.body.ConsumerRunningInfo;
import com.alibaba.rocketmq.remoting.RPCHook;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by robert on 2015/5/29.
 */
public class SyncConsumerGroupCommand implements SubCommand {

    private Set<String> consumerGroups = new HashSet<>();

    @Override
    public String commandName() {
        return "syncConsumerGroup";
    }

    @Override
    public String commandDesc() {
        return " tyr to sync consumer group ";
    }

    @Override
    public Options buildCommandlineOptions(Options options) {
        Option opt = new Option("b", "brokerAddr", true, "subscription group from which broker");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("c", "clusterName", true, "subscription group from which cluster");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("g", "consumerGroup", true, "subscription group ");
        opt.setRequired(false);
        options.addOption(opt);

        return options;
    }

    @Override
    public void execute(CommandLine commandLine, Options options, RPCHook rpcHook) {
        DefaultMQAdminExt adminExt = new DefaultMQAdminExt();
        try{
            adminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
            adminExt.start();
            getBaseGroups(adminExt);

            for (String comGroup:consumerGroups) {
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            adminExt.shutdown();
        }
    }

    private void getBaseGroups(DefaultMQAdminExt defaultMQAdminExt){
        boolean flag = true;
        while (flag) {
            try {
                Set<String> topics = defaultMQAdminExt.fetchAllTopicList().getTopicList();
                for (String topic : topics) {
                    if (topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX) || topic.startsWith(MixAll.DLQ_GROUP_TOPIC_PREFIX))
                        consumerGroups.add(topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX) ? topic.replace(MixAll.RETRY_GROUP_TOPIC_PREFIX, "") :
                                topic.replace(MixAll.DLQ_GROUP_TOPIC_PREFIX, ""));
                }

                flag = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
