package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.ConsumerGroupMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitConsumerGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("cockpitConsumerGroupService")
public class CockpitConsumerGroupServiceImpl implements CockpitConsumerGroupService {

    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    @Transactional
    @Override
    public void delete(long consumerGroupId) {
        consumerGroupMapper.deleteConsumerGroupTeamAssociation(consumerGroupId, 0);
        consumerGroupMapper.delete(consumerGroupId);
    }

    @Override
    public ConsumerGroup getBaseBean(String consumerGroupName) {
        return consumerGroupMapper.getBase(consumerGroupName);
    }

    @Override
    @Transactional
    public void insert(ConsumerGroup consumerGroup, long teamId) {
        consumerGroupMapper.insert(consumerGroup);
        consumerGroupMapper.associateTeam(consumerGroup.getId(), teamId);
    }
}
