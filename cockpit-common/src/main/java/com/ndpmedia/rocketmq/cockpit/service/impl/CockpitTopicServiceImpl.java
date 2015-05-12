package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.common.protocol.body.TopicList;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("cockpitTopicService")
public class CockpitTopicServiceImpl implements CockpitTopicService {

    private Logger logger = LoggerFactory.getLogger(CockpitTopicServiceImpl.class);

    @Autowired
    private TopicMapper topicMapper;

    private static final List PERMISSIONS = Arrays.asList(2, 4, 6);

    @Override
    public Set<String> fetchTopics() {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Helper.getInstanceName());
        try {
            defaultMQAdminExt.start();

            TopicList topicList = defaultMQAdminExt.fetchAllTopicList();

            return topicList.getTopicList();
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("[QUERY][TOPIC][MQADMIN] try to get topic failed." + e);
        } finally {
            defaultMQAdminExt.shutdown();
        }
        return null;
    }


    @Override
    public boolean createOrUpdateTopic(Topic topic) {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Helper.getInstanceName());
        try {
            defaultMQAdminExt.start();
            TopicConfig topicConfig = wrapTopicToTopicConfig(topic);
            if (null != topic.getBrokerAddress() && !topic.getBrokerAddress().isEmpty()) {
                defaultMQAdminExt.createAndUpdateTopicConfig(topic.getBrokerAddress(), topicConfig);
                if (topic.isOrder()) {
                    // 注册顺序消息到 nameserver
                    String brokerName = CommandUtil.fetchBrokerNameByAddr(defaultMQAdminExt, topic.getBrokerAddress());
                    String orderConf = brokerName + ":" + topicConfig.getWriteQueueNums();
                    defaultMQAdminExt.createOrUpdateOrderConf(topicConfig.getTopicName(), orderConf, false);
                }
            } else {
                Set<String> masterSet = CommandUtil
                        .fetchMasterAddrByClusterName(defaultMQAdminExt, topic.getClusterName());
                for (String address : masterSet) {
                    defaultMQAdminExt.createAndUpdateTopicConfig(address, topicConfig);
                }

                if (topic.isOrder()) {
                    // 注册顺序消息到 nameserver
                    Set<String> brokerNameSet = CommandUtil
                            .fetchBrokerNameByClusterName(defaultMQAdminExt, topic.getClusterName());
                    StringBuilder orderConf = new StringBuilder();
                    String splitter = "";
                    for (String s : brokerNameSet) {
                        orderConf.append(splitter).append(s).append(":").append(topicConfig.getWriteQueueNums());
                        splitter = ";";
                    }
                    defaultMQAdminExt.createOrUpdateOrderConf(topicConfig.getTopicName(), orderConf.toString(), true);
                }
            }
        } catch (Exception e) {
            logger.warn("[ADD][TOPIC][MQADMIN]try to add topic failed." + e);
            return false;
        } finally {
            defaultMQAdminExt.shutdown();
        }

        return true;

    }

    @Override
    public boolean deleteTopic(Topic topic) {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt();
        defaultMQAdminExt.setInstanceName(Helper.getInstanceName());
        try {
            defaultMQAdminExt.start();
            Set<String> nameServerAddress = new HashSet<String>(defaultMQAdminExt.getNameServerAddressList());

            // Delete from brokers.
            Set<String> masterBrokerAddressSet =
                    CommandUtil.fetchMasterAddrByClusterName(defaultMQAdminExt, topic.getClusterName());

            defaultMQAdminExt.deleteTopicInBroker(masterBrokerAddressSet, topic.getTopic());

            // Delete from name server.
            defaultMQAdminExt.deleteTopicInNameServer(nameServerAddress, topic.getTopic());
        } catch (Exception e) {
            logger.warn("[DELETE][TOPIC][MQADMIN] try to delete topic failed." + e);
            return false;
        } finally {
            defaultMQAdminExt.shutdown();
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
}
