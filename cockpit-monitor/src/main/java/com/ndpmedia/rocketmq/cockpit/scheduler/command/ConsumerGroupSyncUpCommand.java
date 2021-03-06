package com.ndpmedia.rocketmq.cockpit.scheduler.command;

import com.alibaba.rocketmq.remoting.RPCHook;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerMQService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupMQService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class ConsumerGroupSyncUpCommand implements SubCommand {

    private Set<String> brokerList = new HashSet<>();

    @Autowired
    private CockpitBrokerDBService cockpitBrokerDBService;

    @Autowired
    private CockpitBrokerMQService cockpitBrokerMQService;

    @Autowired
    private CockpitConsumerGroupDBService cockpitConsumerGroupDBService;

    @Autowired
    private CockpitConsumerGroupMQService cockpitConsumerGroupMQService;

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
            Set<String> consumerGroups = cockpitConsumerGroupMQService.getGroups(adminExt);

            doList(adminExt);

            for (String comGroup: consumerGroups) {
                System.out.println("now we check consumer group : " + comGroup);
                System.out.println(cockpitConsumerGroupMQService.getGroupConfig(adminExt, comGroup));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            adminExt.shutdown();
        }
    }

    private void doList(DefaultMQAdminExt defaultMQAdminExt) {
        brokerList.addAll(cockpitBrokerMQService.getALLBrokers(defaultMQAdminExt));
    }
}
