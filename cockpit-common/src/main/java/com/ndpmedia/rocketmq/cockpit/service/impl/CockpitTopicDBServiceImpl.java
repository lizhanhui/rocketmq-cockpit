package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.TopicBrokerInfo;
import com.ndpmedia.rocketmq.cockpit.model.TopicMetadata;
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
    public List<TopicMetadata> getTopics(Status... statuses) {
        int[] statusIds = new int[statuses.length];
        int i = 0;
        for (Status s : statuses) {
            statusIds[i++] = s.ordinal();
        }
        return topicMapper.list(0, statusIds, null);
    }

    @Override
    public TopicMetadata getTopic(String clusterName, String topic) {
        return topicMapper.getMetadataByTopic(clusterName, topic);
    }

    @Override
    public boolean activate(long id) {
        try{
            TopicMetadata topic = topicMapper.getMetadata(id);
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
            TopicMetadata topic = topicMapper.getMetadata(id);
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
    public void insert(long projectId, TopicBrokerInfo topicBrokerInfo) {
        insert(topicBrokerInfo.getTopicMetadata());

        topicMapper.insertTopicBrokerInfo(topicBrokerInfo);

        topicMapper.connectProject(topicBrokerInfo.getTopicMetadata().getId(), projectId);
    }

    @Override
    public void insert(TopicMetadata topicMetadata) {
        if (exists(topicMetadata.getClusterName(), topicMetadata.getTopic())) {
            return;
        }

        topicMapper.insert(topicMetadata);
    }

    @Override
    public void insertTopicBrokerInfo(TopicBrokerInfo topicBrokerInfo) {
        topicMapper.insertTopicBrokerInfo(topicBrokerInfo);
    }

    @Override
    public void refreshTopicBrokerInfo(long topicId, long brokerId) {
        topicMapper.refresh(brokerId, topicId);
    }

    @Override
    public void insertTopicProjectInfo(long topicId, long projectId) {
        topicMapper.connectProject(topicId, projectId);
    }

    @Transactional
    @Override
    public void remove(long topicId, long projectId) {
        topicMapper.disconnectProject(topicId, projectId);
        topicMapper.delete(topicId);
    }

    @Override
    public boolean exists(String clusterName, String topic) {
        return topicMapper.getMetadataByTopic(null, topic) != null;
    }

    @Override
    public List<Long> getProjectIDs(long topicId, String topic) {
        return topicMapper.getProjects(topicId, topic);
    }

    @Override
    public boolean isDCAllowed(long topicId, long dcId) {
        return topicMapper.isDCAllowed(topicId, dcId);
    }

    @Override
    public void addDCAllowed(long topicId, long dcId, Status status) {
        topicMapper.insertDCAllowed(topicId, dcId, status);
    }
}
