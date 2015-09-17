package com.ndpmedia.rocketmq.cockpit;

import com.ndpmedia.rocketmq.cockpit.model.TopicAvailability;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.BrokerMapper;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoPilot {

    @Autowired
    private BrokerMapper brokerMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Scheduled(fixedDelay = 30000)
    public void autoPilot() {
        List<TopicAvailability> topicAvailabilityList = topicMapper.queryTopicsAvailability();

        // find out those topic that requires allocation of more broker resources.

        // find out brokers in matched DC that have smallest load

        // For each topic, create associated consumer group on the target, matched brokers.

        // Create topic on matched brokers or update topic read/write queue number.
    }

}
