package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.protocol.body.SubscriptionGroupWrapper;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.common.protocol.route.TopicRouteData;
import com.alibaba.rocketmq.common.subscription.SubscriptionGroupConfig;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.admin.MQAdminExt;
import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.model.BrokerLoad;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.DataCenter;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.TopicAvailability;
import com.ndpmedia.rocketmq.cockpit.model.TopicBrokerInfo;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.BrokerMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumerGroupMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import com.ndpmedia.rocketmq.cockpit.service.impl.CockpitConsumerGroupMQServiceImpl;
import com.ndpmedia.rocketmq.cockpit.service.impl.CockpitTopicMQServiceImpl;
import com.ndpmedia.rocketmq.cockpit.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AutoPilot {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoPilot.class);

    private static final int TOPIC_AVAILABILITY_THRESHOLD = 2;

    @Autowired
    private BrokerMapper brokerMapper;

    @Autowired
    private CockpitTopicMQService cockpitTopicMQService;

    @Autowired
    private CockpitTopicDBService cockpitTopicDBService;

    @Autowired
    private CockpitConsumerGroupDBService cockpitConsumerGroupDBService;

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
                    List<Long> currentHostingBrokers = new ArrayList<>();

                    List<TopicBrokerInfo> topicBrokerInfoList = topicMapper.queryTopicBrokerInfo(topicAvailability.getTopicId(), 0, topicAvailability.getDcId());
                    for (TopicBrokerInfo topicBrokerInfo : topicBrokerInfoList) {
                        currentHostingBrokers.add(topicBrokerInfo.getBroker().getId());
                    }

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
                    for (Long brokerId : candidateBrokers) {
                        for (Long consumerGroupId : consumerGroupIds) {
                            if (!brokerMapper.hasConsumerGroup(brokerId, consumerGroupId)) {
                                Broker broker = brokerMapper.get(brokerId);
                                ConsumerGroup consumerGroup = consumerGroupMapper.get(consumerGroupId);
                                try {
                                    // For each topic, create associated consumer group on the target, matched brokers.
                                    adminExt.createAndUpdateSubscriptionGroupConfig(broker.getAddress(),
                                            CockpitConsumerGroupMQServiceImpl.wrap(consumerGroup));
                                    brokerMapper.createConsumerGroup(brokerId, consumerGroupId);

                                    TopicBrokerInfo topicBrokerInfo = topicMapper
                                            .queryTopicBrokerInfo(topicAvailability.getTopicId(), brokerId, 0).get(0);

                                    // Create topic on matched brokers or update topic read/write queue number.
                                    adminExt.createAndUpdateTopicConfig(broker.getAddress(),
                                            CockpitTopicMQServiceImpl.wrapTopicToTopicConfig(topicBrokerInfo));
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

        // TODO for each topic-broker pair, make sure its associated consumer group is there.

        Map<String, Set<String>> brokerAddressConsumerGroupCache = new HashMap<>();

        List<TopicMetadata> topicMetadataList = cockpitTopicDBService.getTopics(Status.ACTIVE, Status.APPROVED);
        for (TopicMetadata topicMetadata : topicMetadataList) {
            try {
                TopicRouteData topicRouteData = adminExt.examineTopicRouteInfo(topicMetadata.getTopic());
                // fetch associated consumer groups from DB.
                List<ConsumerGroup> associatedConsumerGroups = cockpitConsumerGroupDBService.listByTopic(topicMetadata.getId());

                List<BrokerData> brokerDataList = topicRouteData.getBrokerDatas();
                for (BrokerData brokerData : brokerDataList) {
                    for (Map.Entry<Long, String> brokerAddress : brokerData.getBrokerAddrs().entrySet()) {

                        // We only need to handle master broker node
                        if (brokerAddress.getKey() != MixAll.MASTER_ID) {
                            continue;
                        }

                        if (!brokerAddressConsumerGroupCache.containsKey(brokerAddress.getValue())) {
                            Set<String> consumerGroups = new HashSet<>();
                            try {
                                SubscriptionGroupWrapper subscriptionGroups = adminExt.fetchAllSubscriptionGroups(brokerAddress.getValue(), 3000);
                                for (SubscriptionGroupConfig subscriptionGroupConfig : subscriptionGroups.getSubscriptionGroupTable().values()) {
                                    consumerGroups.add(subscriptionGroupConfig.getGroupName());
                                }
                            } catch (MQBrokerException e) {
                                e.printStackTrace();
                            }
                            brokerAddressConsumerGroupCache.put(brokerAddress.getValue(), consumerGroups);
                        }
                        Set<String> consumerGroupsOnBroker = brokerAddressConsumerGroupCache.get(brokerAddress.getValue());

                        for (ConsumerGroup consumerGroup : associatedConsumerGroups) {
                            if (!consumerGroupsOnBroker.contains(consumerGroup.getGroupName())) {
                                try {
                                    adminExt.createAndUpdateSubscriptionGroupConfig(brokerAddress.getValue(),
                                            CockpitConsumerGroupMQServiceImpl.wrap(consumerGroup));
                                } catch (MQBrokerException e) {
                                    LOGGER.error("Broker {} does not have consumer group: {} and trying to creating such consumer group fails!", brokerAddress.getValue(), consumerGroup.getGroupName());
                                    LOGGER.error("", e);
                                }
                            }
                        }
                    }
                }
            } catch (RemotingException | MQClientException | InterruptedException e) {
                LOGGER.error("Failed to fetch topicMetadata route data: {}", topicMetadata.getTopic());
            }

        }

        if (null != adminExt) {
            adminExt.shutdown();
        }
    }
}
