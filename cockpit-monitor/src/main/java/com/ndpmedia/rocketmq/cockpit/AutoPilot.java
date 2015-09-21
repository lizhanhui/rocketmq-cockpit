package com.ndpmedia.rocketmq.cockpit;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.admin.MQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.model.BrokerLoad;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.DataCenter;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.model.TopicAvailability;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.BrokerMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumerGroupMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.impl.CockpitConsumerGroupServiceImpl;
import com.ndpmedia.rocketmq.cockpit.service.impl.CockpitTopicRocketMQServiceImpl;
import com.ndpmedia.rocketmq.cockpit.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AutoPilot {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoPilot.class);

    private static final int TOPIC_AVAILABILITY_THRESHOLD = 2;

    @Autowired
    private BrokerMapper brokerMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    private MQAdminExt adminExt;

    public AutoPilot() {
        adminExt = new DefaultMQAdminExt(Helper.getInstanceName());
    }

    @Scheduled(fixedDelay = 30000)
    public void autoPilot() {

        try {
            adminExt.start();
        } catch (MQClientException e) {
            LOGGER.error("Fatal Error: Failed to start admin tool", e);
            return;
        }

        List<TopicAvailability> topicAvailabilityList = topicMapper.queryTopicsAvailability();

        if (null != topicAvailabilityList && !topicAvailabilityList.isEmpty()) {
            for (TopicAvailability topicAvailability : topicAvailabilityList) {
                if (topicAvailability.getAvailability() < TOPIC_AVAILABILITY_THRESHOLD) {
                    List<DataCenter> allowedDataCenters = topicMapper.queryAllowedDC(topicAvailability.getTopicId());
                    if (null == allowedDataCenters || allowedDataCenters.isEmpty()) {
                        continue;
                    } else {
                        boolean legal = false;
                        for (DataCenter dataCenter : allowedDataCenters) {
                            if (dataCenter.getId() == topicAvailability.getDcId()) {
                                legal = true;
                                break;
                            }
                        }
                        if (!legal) {
                            continue;
                        }
                    }

                    List<Broker> brokers = brokerMapper.list(null, null, 0, topicAvailability.getDcId(), null);
                    if (topicAvailability.getAvailability() >= brokers.size()) {
                        // We do not have more brokers.
                        // TODO warning and notify to add more broker in this data center.
                        LOGGER.warn("Availability of topic: {} in DC: {} is {}, but we have no more brokers in this DC",
                                topicAvailability.getTopicId(), topicAvailability.getDcId(), topicAvailability.getAvailability());
                        continue;
                    }

                    // Find candidate brokers to create topic on.
                    List<Long> currentHostingBrokers = topicMapper.queryTopicHostingBrokerIds(topicAvailability.getTopicId(), topicAvailability.getDcId());
                    List<BrokerLoad> brokerLoadList = brokerMapper.queryBrokerLoad(topicAvailability.getDcId(), 0);
                    List<Long> candidateBrokers = new ArrayList<>();
                    for (BrokerLoad brokerLoad : brokerLoadList) {
                        if (!currentHostingBrokers.contains(brokerLoad.getBrokerId())) {
                            candidateBrokers.add(brokerLoad.getBrokerId());
                            if (candidateBrokers.size() + currentHostingBrokers.size() > TOPIC_AVAILABILITY_THRESHOLD) {
                                break;
                            }
                        }
                    }

                    List<Long> consumerGroupIds = topicMapper.queryAssociatedConsumerGroup(topicAvailability.getTopicId());
                    Topic topic = topicMapper.get(topicAvailability.getTopicId(), null);

                    for (Long brokerId : candidateBrokers) {
                        for (Long consumerGroupId : consumerGroupIds) {
                            if (!brokerMapper.hasConsumerGroup(brokerId, consumerGroupId)) {
                                Broker broker = brokerMapper.get(brokerId);
                                ConsumerGroup consumerGroup = consumerGroupMapper.get(consumerGroupId, null);
                                try {
                                    // For each topic, create associated consumer group on the target, matched brokers.
                                    adminExt.createAndUpdateSubscriptionGroupConfig(broker.getAddress(), CockpitConsumerGroupServiceImpl.wrap(consumerGroup));
                                    brokerMapper.createConsumerGroup(brokerId, consumerGroupId);

                                    // Create topic on matched brokers or update topic read/write queue number.
                                    adminExt.createAndUpdateTopicConfig(broker.getAddress(), CockpitTopicRocketMQServiceImpl.wrapTopicToTopicConfig(topic));
                                    brokerMapper.createTopic(brokerId, topicAvailability.getTopicId());
                                } catch (RemotingException | MQBrokerException | InterruptedException | MQClientException e) {
                                    LOGGER.error("Failed to create consumer group {} on broker {}", consumerGroup.getGroupName(), broker.getAddress());
                                }
                            }
                        }
                    }
                }
            }
        }

        if (null != adminExt) {
            adminExt.shutdown();
        }
    }
}
