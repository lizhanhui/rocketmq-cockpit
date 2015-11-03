package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroupHosting;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumerGroupMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupDBService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("cockpitConsumerGroupDBService")
public class CockpitConsumerGroupDBServiceImpl implements CockpitConsumerGroupDBService {
    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    public List<ConsumerGroup> list(long projectId, String clusterName, String consumerGroupName, long brokerId,
                                    String brokerAddress){
        return consumerGroupMapper.list(projectId, clusterName, consumerGroupName, brokerId, brokerAddress);
    }

    @Override
    public List<ConsumerGroup> listByTopic(long topicId) {
        return consumerGroupMapper.listByTopic(topicId);
    }

    @Override
    public ConsumerGroup get(long consumerGroupId, String consumerGroupName) {
        if (consumerGroupId > 0) {
            return consumerGroupMapper.get(consumerGroupId);
        }

        if (null != consumerGroupName) {
            return consumerGroupMapper.getByName(consumerGroupName);
        }

        throw new IllegalArgumentException("Either consumer group ID or Name should be present");
    }

    @Override
    public void activate(long consumerGroupId) {
        ConsumerGroup consumerGroup = consumerGroupMapper.get(consumerGroupId);
        if (null != consumerGroup && consumerGroup.getStatus() != Status.ACTIVE) {
            consumerGroup.setStatus(Status.ACTIVE);
            consumerGroupMapper.update(consumerGroup);
        }
    }


    @Override
    @Transactional
    public void insert(ConsumerGroup consumerGroup, long projectId) {
        consumerGroupMapper.insert(consumerGroup);
        consumerGroupMapper.connectProject(consumerGroup.getId(), projectId);
    }

    @Transactional
    @Override
    public void delete(long consumerGroupId) {
        consumerGroupMapper.disconnectProject(consumerGroupId, 0);
        consumerGroupMapper.delete(consumerGroupId);
    }

    @Override
    public void update(ConsumerGroup consumerGroup){
        consumerGroupMapper.update(consumerGroup);
    }

    @Override
    public void refresh(long brokerId, long consumerGroupId) {
        consumerGroupMapper.refresh(brokerId, consumerGroupId);
    }

    @Override
    public List<ConsumerGroupHosting> listEndangeredConsumerGroupsByBroker(long brokerId) {
        return consumerGroupMapper.queryEndangeredHosting(brokerId);
    }
}
