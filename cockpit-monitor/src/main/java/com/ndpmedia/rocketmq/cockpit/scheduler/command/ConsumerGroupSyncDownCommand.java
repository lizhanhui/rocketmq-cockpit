package com.ndpmedia.rocketmq.cockpit.scheduler.command;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.remoting.RPCHook;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;
import com.ndpmedia.rocketmq.cockpit.exception.CockpitException;
import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.BrokerMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerMQService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupMQService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ConsumerGroupSyncDownCommand implements SubCommand {

    private Logger logger = LoggerFactory.getLogger(ConsumerGroupSyncDownCommand.class);

    @Autowired
    private CockpitConsumerGroupDBService cockpitConsumerGroupDBService;

    @Autowired
    private CockpitConsumerGroupMQService cockpitConsumerGroupMQService;

    @Autowired
    private CockpitBrokerDBService cockpitBrokerDBService;

    @Autowired
    private CockpitBrokerMQService cockpitBrokerMQService;

    @Autowired
    private CockpitTopicMQService cockpitTopicMQService;

    @Autowired
    private BrokerMapper brokerMapper;

    private Map<String, String> brokerToCluster = new HashMap<>();

    @Override
    public String commandName() {
        return "downConsumerGroup";
    }

    @Override
    public String commandDesc() {
        return " try to download consumer group information ";
    }

    @Override
    public Options buildCommandlineOptions(Options options) {
        Option opt = new Option("b", "brokerAddr", true, "from which broker");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("c", "clusterName", true, "from which cluster");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("t", "consumer group", true, "subscription consumer group ");
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
            Set<String> consumerGroups = cockpitConsumerGroupMQService.getGroups(adminExt);
            for (String consumerGroup : consumerGroups) {
                logger.info("now we check consumer group:" + consumerGroup);
                downloadConsumerGroupConfig(adminExt, consumerGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            adminExt.shutdown();
        }
    }

    private void doMap(DefaultMQAdminExt defaultMQAdminExt) {
        brokerToCluster.putAll(cockpitBrokerMQService.getBrokerCluster(defaultMQAdminExt));
    }

    private void downloadConsumerGroupConfig(DefaultMQAdminExt defaultMQAdminExt, String consumerGroup) throws CockpitException {
        Set<String> brokers = cockpitTopicMQService.getTopicBrokers(defaultMQAdminExt, MixAll.RETRY_GROUP_TOPIC_PREFIX + consumerGroup, true);
        TopicConfig topicConfig = cockpitTopicMQService.getTopicConfigByTopicName(defaultMQAdminExt, MixAll.RETRY_GROUP_TOPIC_PREFIX + consumerGroup);

        try {
            ConsumerGroup cg = new ConsumerGroup();
            cg.setGroupName(consumerGroup);
            cg.setConsumeEnable(true);
            cg.setRetryQueueNum(topicConfig.getWriteQueueNums());

            ConsumerGroup existingConsumerGroup = cockpitConsumerGroupDBService.get(0L, consumerGroup);
            //若未获取到相同group Name，相同Broker地址的数据，则将该条信息作为新数据直接插入
            if (null == existingConsumerGroup) {
                cockpitConsumerGroupDBService.insert(cg, 1);
                existingConsumerGroup = cg;
            } else if (existingConsumerGroup.getStatus() != Status.ACTIVE) {
                //若获取到相同group Name，相同Broker地址的数据，但是该条数据状态不为ACTIVE，刷新该条数据状态
                cockpitConsumerGroupDBService.activate(existingConsumerGroup.getId());
            }

            for (String brokerAddress : brokers) {
                Broker broker = brokerMapper.get(0, brokerAddress);
                if (!brokerMapper.hasConsumerGroup(broker.getId(), existingConsumerGroup.getId())) {
                    brokerMapper.createConsumerGroup(broker.getId(), existingConsumerGroup.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
