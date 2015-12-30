package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.ndpmedia.rocketmq.cockpit.model.*;
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
        if (statuses != null) {
            int[] statusIds = new int[statuses.length];
            int i = 0;
            for (Status s : statuses) {
                statusIds[i++] = s.ordinal();
            }

            return topicMapper.list(0, statusIds, null, 0);
        }else {

            return topicMapper.list(0, null, null, 0);
        }
    }

    @Override
    public List<TopicAvailability> queryTopicsAvailability(Status... statuses){
        return topicMapper.queryTopicsAvailability(statuses);
    }

    @Override
    public TopicMetadata getTopic(String clusterName, String topic) {
        return topicMapper.getMetadataByTopic(clusterName, topic);
    }

    @Override
    public TopicMetadata getTopic(long topicId){
        return topicMapper.getMetadata(topicId);
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
    public boolean activate(long topicId, long brokerId) {
        try {
            List<TopicBrokerInfo> topicBrokerInfos = topicMapper.queryTopicBrokerInfo(topicId, brokerId, 0);
            for (TopicBrokerInfo topicBrokerInfo:topicBrokerInfos){
                topicBrokerInfo.setStatus(Status.ACTIVE);
                topicMapper.updateTopicBrokerInfo(topicBrokerInfo);
            }
        }catch (Exception e){
            LOGGER.warn("" + e);
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
            LOGGER.warn("" + e);
            return false;
        }
        return true;
    }

    @Override
    public boolean deactivate(long topicId, long brokerId) {
        try{
            List<TopicBrokerInfo> topicBrokerInfos = topicMapper.queryTopicBrokerInfo(topicId, brokerId, 0);
            for (TopicBrokerInfo topicBrokerInfo:topicBrokerInfos){
                topicBrokerInfo.setStatus(Status.DELETED);
                topicMapper.updateTopicBrokerInfo(topicBrokerInfo);
            }
        }catch (Exception e) {
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
    public void update(TopicMetadata topicMetadata){
        topicMapper.update(topicMetadata);
    }

    @Override
    public void update(TopicBrokerInfo topicBrokerInfo){
        topicMapper.updateTopicBrokerInfo(topicBrokerInfo);
    }

    @Override
    public void insert(TopicMetadata topicMetadata, long projectId){
        if (exists(topicMetadata.getClusterName(), topicMetadata.getTopic())) {
            return;
        }

        topicMapper.insert(topicMetadata);

        topicMapper.connectProject(topicMetadata.getId(), projectId);
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
        topicMapper.changeTopicDCStatus(topicId, 0, Status.DELETED);
    }

    @Override
    public boolean exists(String clusterName, String topic) {
        TopicMetadata topicMetadata = topicMapper.getMetadataByTopic(clusterName, topic);
        return null != topicMetadata
                && topicMetadata.getStatus() != Status.DELETED;
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

    @Override
    public List<DataCenter> queryAllowedDC(long topicId){
        return topicMapper.queryAllowedDC(topicId);
    }

    @Override
    public List<TopicBrokerInfo> queryEndangeredTopicBrokerInfoList(long brokerId) {
        return topicMapper.queryEndangeredTopicsByBroker(brokerId);
    }

    public List<TopicBrokerInfo> queryApprovedTopicsByBroker(long brokerId) {
        return topicMapper.queryApprovedTopicsByBroker(brokerId);
    }

    @Override
    public List<TopicBrokerInfo> queryTopicBrokerInfoByTopic(long topicId, long brokerId, int dc){
        return queryTopicBrokerInfo(topicId, 0L, 0);
    }

    @Override
    public List<TopicBrokerInfo> queryTopicBrokerInfo(long topicId, long brokerId, int dc){
        return topicMapper.queryTopicBrokerInfo(topicId, brokerId, dc);
    }

    public List<Long> queryAssociatedConsumerGroup(long topicId){
        return topicMapper.queryAssociatedConsumerGroup(topicId);
    }
}
