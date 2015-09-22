package com.ndpmedia.rocketmq.cockpit.scheduler.command;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.remoting.RPCHook;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumerGroupMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupNSService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicNSService;
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

/**
 * Created by robert on 2015/5/29.
 */
@Component
public class DownConsumerCommand implements SubCommand {

    private Logger logger = LoggerFactory.getLogger(DownConsumerCommand.class);

    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    @Autowired
    private CockpitBrokerService cockpitBrokerService;

    @Autowired
    private CockpitTopicNSService cockpitTopicNSService;

    @Autowired
    private CockpitConsumerGroupNSService cockpitConsumerGroupNSService;

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
            Set<String> consumerGroups = cockpitConsumerGroupNSService.getGroups(adminExt);
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
        brokerToCluster.putAll(cockpitBrokerService.getBrokerCluster(defaultMQAdminExt));
    }

    private void downloadConsumerGroupConfig(DefaultMQAdminExt defaultMQAdminExt, String consumerGroup) {
        Set<String> brokers = cockpitTopicNSService.getTopicBrokers(defaultMQAdminExt, MixAll.RETRY_GROUP_TOPIC_PREFIX + consumerGroup);
        TopicConfig topicConfig = cockpitTopicNSService.getTopicConfigByTopicName(defaultMQAdminExt, MixAll.RETRY_GROUP_TOPIC_PREFIX + consumerGroup);

        outer:
        for (String broker : brokers) {
            int flag = 0;
            while (flag++ < 5) {
                try {
                    ConsumerGroup cg = new ConsumerGroup();
                    cg.setGroupName(consumerGroup);
                    cg.setBrokerAddress(broker);
                    cg.setClusterName(brokerToCluster.get(broker));
                    cg.setConsumeEnable(true);
                    cg.setRetryQueueNum(topicConfig.getWriteQueueNums());

                    ConsumerGroup oldC = consumerGroupMapper.get(0L, consumerGroup, null, null);
                    //若未获取到相同group Name，相同Broker地址的数据，则将该条信息作为新数据直接插入
                    if (null == oldC)
                        consumerGroupMapper.insert(cg);
                    //若获取到相同group Name，相同Broker地址的数据，但是该条数据状态不为ACTIVE，刷新该条数据状态
                    else if (oldC.getStatus() != Status.ACTIVE)
                        consumerGroupMapper.register(oldC.getId());
                    //现阶段Consumer Group不与Broker信息做强关联，因此无论brokers中哪一个Broker可完成Consumer Group信息，
                    // 后续步骤均省略，当Broker信息为必须信息，则不再省略。
                    break  outer;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            logger.info("[sync topic]save consumer group : " + consumerGroup + " from broker :" + broker);
        }
    }
}
