package com.ndpmedia.rocketmq.cockpit.scheduler.command;

import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.remoting.RPCHook;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitBrokerService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
import com.ndpmedia.rocketmq.cockpit.util.TopicTranslate;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by robert on 2015/5/29.
 */
@Component
public class DownTopicCommand implements SubCommand {

    private Logger logger = LoggerFactory.getLogger(DownTopicCommand.class);

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private CockpitBrokerService cockpitBrokerService;

    @Autowired
    private CockpitTopicService cockpitTopicService;

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

            Set<String> topics = cockpitTopicService.getTopics(adminExt);
            for (String topicName : topics) {
                logger.info("now we check topic :" + topicName);
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
        brokerToCluster.putAll(cockpitBrokerService.getBrokerCluster(defaultMQAdminExt));
    }

    private TopicConfig getTopicConfig(DefaultMQAdminExt defaultMQAdminExt, String topic) {
        return cockpitTopicService.getTopicConfigByTopicName(defaultMQAdminExt, topic);
    }

    private void downloadTopicConfig(DefaultMQAdminExt defaultMQAdminExt, TopicConfig topicConfig) {
        Set<String> brokers = cockpitTopicService.getTopicBrokers(defaultMQAdminExt, topicConfig.getTopicName());

        for (String broker : brokers) {
            int flag = 0;
            while (flag++ < 5) {
                try {
                    Topic topic = TopicTranslate.translateFrom(topicConfig, brokerToCluster.get(broker), broker);
                    Topic oldT = topicMapper.get(0L, topic.getTopic(), topic.getBrokerAddress(), null);
                    //若未获取到相同Topic Name，相同Broker地址的数据，则将该条信息作为新数据直接插入
                    if (null == oldT)
                        topicMapper.insert(topic);
                    //若获取到相同Topic Name，相同Broker地址的数据，但是该条数据状态不为ACTIVE，刷新该条数据状态
                    else if (oldT.getStatus() != Status.ACTIVE)
                        topicMapper.register(oldT.getId());
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            logger.info("[sync topic]add topic config:" + topicConfig + " to broker :" + broker);
        }
    }
}
