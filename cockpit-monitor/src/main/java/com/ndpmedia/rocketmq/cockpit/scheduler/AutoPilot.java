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
import com.ndpmedia.rocketmq.cockpit.model.*;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.BrokerMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.WarningMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicMQService;
import com.ndpmedia.rocketmq.cockpit.service.impl.CockpitConsumerGroupMQServiceImpl;
import com.ndpmedia.rocketmq.cockpit.util.Helper;
import com.ndpmedia.rocketmq.cockpit.util.TopicTranslate;
import com.ndpmedia.rocketmq.cockpit.util.WarnMsgHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

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
    private WarningMapper warningMapper;

    /**
     * 1.check topic ,get the nums of dc and the topic
     * 2.check consumer group,if some broker have topic but do not have consumer group
     */
    @Scheduled(fixedDelay = 600000)
    public void autoPilot() {
        //get connection
        MQAdminExt adminExt = null;
        try {
            adminExt = new DefaultMQAdminExt(Helper.getInstanceName());
            adminExt.start();

            //get every topic nums in different dc
            autoPilotTopic(adminExt);

            // for each topic-broker pair, make sure its associated consumer group is there.
            autoPilotConsumerGroup(adminExt);
        } catch (MQClientException e) {
            LOGGER.error("[MONITOR][AUTO-PILOT]Fatal Error: Failed to start admin tool", e);
            return;
        } finally {
            if (null != adminExt) {
                adminExt.shutdown();
            }
        }
    }

    private void autoPilotConsumerGroup(MQAdminExt adminExt) {
        Map<String, Set<String>> brokerAddressConsumerGroupCache = new HashMap<>();

        List<TopicMetadata> topicMetadataList = cockpitTopicDBService.getTopics(Status.ACTIVE, Status.APPROVED);
        for (TopicMetadata topicMetadata : topicMetadataList) {
            TopicRouteData topicRouteData = null;
            List<BrokerData> brokerDataList = new ArrayList<>();
            try {
                topicRouteData = adminExt.examineTopicRouteInfo(topicMetadata.getTopic());
                brokerDataList.addAll(topicRouteData.getBrokerDatas());
            } catch (Exception e) {
                LOGGER.warn("[MONITOR][AUTO_PILOT] get topic route failed!" + topicMetadata.getTopic());
            }

            for (BrokerData brokerData : brokerDataList) {
                for (Map.Entry<Long, String> brokerAddress : brokerData.getBrokerAddrs().entrySet()) {

                    // We only need to handle master broker node
                    if (brokerAddress.getKey() != MixAll.MASTER_ID) {
                        continue;
                    }

                    getGroupByBroker(adminExt, brokerAddressConsumerGroupCache, brokerAddress);
                    fetchConsumerGroup(adminExt, topicMetadata.getId(), brokerAddress, brokerAddressConsumerGroupCache);
                }
            }
        }
    }

    private void getGroupByBroker(MQAdminExt adminExt, Map<String, Set<String>> brokerAddressConsumerGroupCache, Map.Entry<Long, String> brokerAddress) {
        if (!brokerAddressConsumerGroupCache.containsKey(brokerAddress.getValue())) {
            Set<String> consumerGroups = new HashSet<String>();
            try {
                SubscriptionGroupWrapper subscriptionGroups = adminExt.fetchAllSubscriptionGroups(brokerAddress.getValue(), 3000);
                for (SubscriptionGroupConfig subscriptionGroupConfig : subscriptionGroups.getSubscriptionGroupTable().values()) {
                    consumerGroups.add(subscriptionGroupConfig.getGroupName());
                }
            } catch (Exception e) {
                LOGGER.error("[MONITOR][AUTO-PILOT]Failed to get subscription group info from broker: {}", brokerAddress.getValue());
            }
            brokerAddressConsumerGroupCache.put(brokerAddress.getValue(), consumerGroups);
        }
    }

    private void fetchConsumerGroup(MQAdminExt adminExt, long topicId, Map.Entry<Long, String> brokerAddress, Map<String, Set<String>> brokerAddressConsumerGroupCache) {
        Set<String> consumerGroupsOnBroker = brokerAddressConsumerGroupCache.get(brokerAddress.getValue());
        // fetch associated consumer groups from DB.
        List<ConsumerGroup> associatedConsumerGroups = cockpitConsumerGroupDBService.listByTopic(topicId);
        for (ConsumerGroup consumerGroup : associatedConsumerGroups) {
            if (!consumerGroupsOnBroker.contains(consumerGroup.getGroupName())) {
                try {
                    adminExt.createAndUpdateSubscriptionGroupConfig(brokerAddress.getValue(),
                            CockpitConsumerGroupMQServiceImpl.wrap(consumerGroup), 15000L);
                } catch (Exception e) {
                    LOGGER.error("[MONITOR][AUTO-PILOT]Broker {} does not have consumer group: {} and trying to creating such consumer group fails!", brokerAddress.getValue(), consumerGroup.getGroupName());
                }
            }
        }
    }

    private void autoPilotTopic(MQAdminExt adminExt) {
        List<TopicAvailability> topicAvailabilityList = cockpitTopicDBService.queryTopicsAvailability(Status.APPROVED, Status.ACTIVE);

        if (null != topicAvailabilityList && !topicAvailabilityList.isEmpty()) {
            for (TopicAvailability topicAvailability : topicAvailabilityList) {
                if (topicAvailability.getAvailability() < TOPIC_AVAILABILITY_THRESHOLD) {
                    List<DataCenter> allowedDataCenters = cockpitTopicDBService.queryAllowedDC(topicAvailability.getTopicId());
                    if (null == allowedDataCenters || allowedDataCenters.isEmpty() || isLegal(topicAvailability, allowedDataCenters)) {
                        continue;
                    }

                    if (needMoreBroker(topicAvailability))
                        continue;

                    // Find candidate brokers to create topic on.
                    List<Long> currentHostingBrokers = getBrokers(topicAvailability);

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

                    List<Long> consumerGroupIds = cockpitTopicDBService.queryAssociatedConsumerGroup(topicAvailability.getTopicId());

                    if (consumerGroupIds.isEmpty()) {
                        String msg = String.format("Trying to create topic[ID: %s] but it does not have associated consumer groups. ", topicAvailability.getTopicId());
                        warningMapper.create(WarnMsgHelper.makeWarning(Level.CRITICAL, msg));

                        // Will NOT create topic without associated consumer groups.
                        continue;
                    }

                    addTopics(adminExt, topicAvailability, candidateBrokers, consumerGroupIds);
                }
            }
        }
    }

    private void addTopics(MQAdminExt adminExt, TopicAvailability topicAvailability, List<Long> candidateBrokers, List<Long> consumerGroupIds) {
        for (Long brokerId : candidateBrokers) {
            Broker broker = brokerMapper.get(brokerId);
            for (Long consumerGroupId : consumerGroupIds) {
                // ensure broker has all consumer groups which are associated with topic under creation.
                if (!brokerMapper.hasConsumerGroup(brokerId, consumerGroupId)) {
                    ConsumerGroup consumerGroup = cockpitConsumerGroupDBService.get(consumerGroupId, null);
                    try {
                        // For each topic, create associated consumer group on the target, matched brokers.
                        adminExt.createAndUpdateSubscriptionGroupConfig(broker.getAddress(),
                                CockpitConsumerGroupMQServiceImpl.wrap(consumerGroup), 20000);
                        brokerMapper.createConsumerGroup(brokerId, consumerGroupId);

                    } catch (RemotingException | MQBrokerException | InterruptedException | MQClientException e) {
                        LOGGER.error("Failed to create consumer group {} on broker {}", consumerGroup.getGroupName(), broker.getAddress());
                    }
                }
            }

            // create topic on broker.
            List<TopicBrokerInfo> existingTopicBrokerInfo = null;
            try {
                existingTopicBrokerInfo = cockpitTopicDBService.queryTopicBrokerInfo(topicAvailability.getTopicId(), brokerId, 0);
                TopicBrokerInfo topicBrokerInfo = null;
                if (existingTopicBrokerInfo.isEmpty()) {
                    topicBrokerInfo = TopicTranslate.createTopicBrokerInfo(broker, cockpitTopicDBService.getTopic(topicAvailability.getTopicId()));
                } else {
                    assert existingTopicBrokerInfo.size() == 1;
                    topicBrokerInfo = existingTopicBrokerInfo.get(0);
                }

                // Create topic on matched brokers or update topic read/write queue number.
                adminExt.createAndUpdateTopicConfig(broker.getAddress(),
                        TopicTranslate.wrapTopicToTopicConfig(topicBrokerInfo), 15000L);
                if (existingTopicBrokerInfo.isEmpty()) {
                    cockpitTopicDBService.insertTopicBrokerInfo(topicBrokerInfo);
                }
            } catch (RemotingException | MQBrokerException | MQClientException | InterruptedException e) {
                LOGGER.error("[MONITOR][AUTO-PILOT]Failed to create topic", e);
            }
        }
    }

    private List<Long> getBrokers(TopicAvailability topicAvailability) {
        List<Long> currentHostingBrokers = new ArrayList<Long>();
        List<TopicBrokerInfo> topicBrokerInfoList = cockpitTopicDBService.queryTopicBrokerInfo(topicAvailability.getTopicId(), 0, topicAvailability.getDcId());
        for (TopicBrokerInfo topicBrokerInfo : topicBrokerInfoList) {
            currentHostingBrokers.add(topicBrokerInfo.getBroker().getId());
        }
        return currentHostingBrokers;
    }

    private boolean needMoreBroker(TopicAvailability topicAvailability) {
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.HOUR, -2);
        List<Broker> brokers = brokerMapper.list(null, null, 0, topicAvailability.getDcId(), calender.getTime());
        if (topicAvailability.getAvailability() > brokers.size()) {
            // We do not have more brokers.
            // TODO auto add broker.
            String msgTemplate = "Availability of topic: %s in DC: %s is %s, but we have no more brokers in this DC";
            String msg = String.format(msgTemplate, topicAvailability.getTopicId(), topicAvailability.getDcId(), topicAvailability.getAvailability());
            LOGGER.warn(msg);

            warningMapper.create(WarnMsgHelper.makeWarning(Level.WARN, msg));
            return true;
        }

        return false;
    }

    private boolean isLegal(TopicAvailability topicAvailability, List<DataCenter> allowedDataCenters) {
        for (DataCenter dataCenter : allowedDataCenters) {
            if (dataCenter.getId() == topicAvailability.getDcId()) {
                return true;
            }
        }
        return false;
    }
}
