package com.ndpmedia.rocketmq.cockpit;

import com.ndpmedia.rocketmq.cockpit.model.Broker;
import com.ndpmedia.rocketmq.cockpit.model.BrokerLoad;
import com.ndpmedia.rocketmq.cockpit.model.DataCenter;
import com.ndpmedia.rocketmq.cockpit.model.TopicAvailability;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.BrokerMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
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

    @Scheduled(fixedDelay = 30000)
    public void autoPilot() {
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
                    List<BrokerLoad> brokerLoadList = brokerMapper.queryBrokerLoad(topicAvailability.getDcId());
                    List<Long> candidateBrokers = new ArrayList<>();
                    for (BrokerLoad brokerLoad : brokerLoadList) {
                        if (!currentHostingBrokers.contains(brokerLoad.getBrokerId())) {
                            candidateBrokers.add(brokerLoad.getBrokerId());
                            if (candidateBrokers.size() + currentHostingBrokers.size() > TOPIC_AVAILABILITY_THRESHOLD) {
                                break;
                            }
                        }
                    }



                }
            }
        }

        // find out those topic that requires allocation of more broker resources.

        // find out brokers in matched DC that have smallest load

        // For each topic, create associated consumer group on the target, matched brokers.

        // Create topic on matched brokers or update topic read/write queue number.
    }

}
