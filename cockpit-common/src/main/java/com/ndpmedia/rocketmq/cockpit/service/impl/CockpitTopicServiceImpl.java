package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.common.protocol.body.TopicList;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.common.protocol.route.QueueData;
import com.alibaba.rocketmq.common.protocol.route.TopicRouteData;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.CommandUtil;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicService;
import com.ndpmedia.rocketmq.cockpit.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("cockpitTopicService")
public class CockpitTopicServiceImpl implements CockpitTopicService {

    private Logger logger = LoggerFactory.getLogger(CockpitTopicServiceImpl.class);

    @Autowired
    private TopicMapper topicMapper;

    private static final List PERMISSIONS = Arrays.asList(2, 4, 6);

    @Override
    public List<Topic> getDelTopics() {
        return topicMapper.delList();
    }

    @Override
    public List<Topic> getActiveTopics() {
        return topicMapper.activeList();
    }

    @Override
    public List<Topic> getTopic(String topic) {
        return topicMapper.detailList(-1, topic, 2);
    }

    @Override
    public boolean register(long id) {
        try{
            topicMapper.register(id);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean unregister(long id){
        try{
            topicMapper.unregister(id);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static TopicConfig wrapTopicToTopicConfig(Topic topic) {
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setWriteQueueNums(topic.getWriteQueueNum());
        topicConfig.setReadQueueNums(topic.getReadQueueNum());
        topicConfig.setTopicName(topic.getTopic());
        return topicConfig;
    }

    @Override
    public List<Long> getTeamId(Topic topic){
        return topicMapper.getTeamId(topic.getId());
    }

    @Transactional
    @Override
    public void insert(Topic topic, long teamId) {

        if (!PERMISSIONS.contains(topic.getPermission())) {
            topic.setPermission(6);
        }

        if (topic.getReadQueueNum() <= 0) {
            topic.setReadQueueNum(Topic.DEFAULT_READ_QUEUE_NUM);
        }

        if (topic.getWriteQueueNum() <= 0) {
            topic.setWriteQueueNum(Topic.DEFAULT_WRITE_QUEUE_NUM);
        }

        topicMapper.insert(topic);
        topicMapper.associateTeam(topic.getId(), teamId);
    }

    @Transactional
    @Override
    public void remove(long topicId, long teamId) {
        topicMapper.delete(topicId);
        topicMapper.removeTopicTeamAssociation(topicId, teamId);
    }

    @Override
    public Topic getBaseBean(String topicName) {
        return topicMapper.get(0, topicName, null, null);
    }
}
