package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service("cockpitTopicDBService")
public class CockpitTopicDBServiceImpl implements CockpitTopicDBService {

    private static final int PERMISSION_READ = 2;

    private static final int PERMISSION_WRITE = 4;

    private static final List PERMISSIONS = Arrays.asList(
            PERMISSION_READ, // Read
            PERMISSION_WRITE, // Write
            PERMISSION_READ | PERMISSION_WRITE // Read + Write
    );

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public List<Topic> getTopics(Status status) {
        return topicMapper.list(0, status.getId(), null, null);
    }

    @Override
    public Topic getTopic(String topic) {
        return topicMapper.get(-1, topic);
    }

    @Override
    public boolean activate(long id) {
        try{
            Topic topic = topicMapper.get(id, null);
            if (null != topic && topic.getStatus() != Status.ACTIVE) {
                topic.setStatus(Status.ACTIVE);
                topicMapper.update(topic);
            }
        } catch (Exception e){
            LOGGER.warn("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean deactivate(long id){
        try{
            Topic topic = topicMapper.get(id, null);
            if (null != topic && topic.getStatus() == Status.ACTIVE) {
                topic.setStatus(Status.DELETED);
                topicMapper.update(topic);
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Transactional
    @Override
    public void insert(Topic topic, long projectId) {

        if (!PERMISSIONS.contains(topic.getPermission())) {
            topic.setPermission(PERMISSION_READ | PERMISSION_WRITE);
        }

        if (topic.getReadQueueNum() <= 0) {
            topic.setReadQueueNum(Topic.DEFAULT_READ_QUEUE_NUM);
        }

        if (topic.getWriteQueueNum() <= 0) {
            topic.setWriteQueueNum(Topic.DEFAULT_WRITE_QUEUE_NUM);
        }

        topicMapper.insert(topic);
        topicMapper.connectProject(topic.getId(), projectId);
    }

    @Transactional
    @Override
    public void remove(long topicId, long projectId) {
        topicMapper.disconnectProject(topicId, projectId);
        topicMapper.delete(topicId);
    }

    @Override
    public boolean exists(String topic) {
        return topicMapper.get(0, topic) != null;
    }

    @Override
    public List<Long> getProjectIDs(long topicId, String topic) {
        return topicMapper.getProjects(topicId, topic);
    }
}
