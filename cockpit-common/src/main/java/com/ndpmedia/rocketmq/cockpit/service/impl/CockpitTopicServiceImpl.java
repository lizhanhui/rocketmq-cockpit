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
    public Set<String> getTopics(DefaultMQAdminExt defaultMQAdminExt) {
        Set<String> resultT = new HashSet<>();
        TopicList topics = null;
        try {
            topics = defaultMQAdminExt.fetchAllTopicList();
            for (String topicName : topics.getTopicList()) {
                if (topicName.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX) || topicName.startsWith(MixAll.DLQ_GROUP_TOPIC_PREFIX))
                    continue;

                resultT.add(topicName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultT;
    }

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
    public TopicConfig getTopicConfigByTopicName(DefaultMQAdminExt defaultMQAdminExt, String topic) {
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setTopicName(topic);

        TopicRouteData topicRouteData = null;
        boolean flag = true;
        while (flag) {
            try {
                topicRouteData = defaultMQAdminExt.examineTopicRouteInfo(topic);
                flag = false;
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getMessage().contains("No topic route info"))
                    flag = false;
            }
        }
        if (null != topicRouteData) {
            List<QueueData> lists = topicRouteData.getQueueDatas();

            int readQ = 0;
            int writeQ = 0;
            int perm = 0;
            for (QueueData queueData : lists) {
                readQ = Math.max(readQ, queueData.getReadQueueNums());
                writeQ = Math.max(writeQ, queueData.getWriteQueueNums());
                perm = Math.max(perm, queueData.getPerm());
                topicConfig.setTopicSysFlag(queueData.getTopicSynFlag());
            }
            topicConfig.setWriteQueueNums(writeQ);
            topicConfig.setReadQueueNums(readQ);
            topicConfig.setPerm(perm);

            return topicConfig;
        }

        logger.info("[sync topic] big error! find topic but no topic config !");
        return null;
    }

    @Override
    public Set<String> getTopicBrokers(DefaultMQAdminExt defaultMQAdminExt, String topic) {
        TopicRouteData topicRouteData = null;
        boolean flag = true;
        while (flag) {
            try {
                topicRouteData = defaultMQAdminExt.examineTopicRouteInfo(topic);
                flag = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<BrokerData> brokerDatas = topicRouteData.getBrokerDatas();
        Set<String> topicBroker = new HashSet<>();

        for (BrokerData brokerData : brokerDatas) {
            for (Map.Entry<Long, String> entry : brokerData.getBrokerAddrs().entrySet()) {
                topicBroker.add(entry.getValue());
            }
        }

        return topicBroker;
    }

    @Override
    public boolean rebuildTopicConfig(DefaultMQAdminExt defaultMQAdminExt, TopicConfig topicConfig, String broker) {

        Set<String> localBroker = getTopicBrokers(defaultMQAdminExt, topicConfig.getTopicName());

        if (!localBroker.contains(broker)) {
            boolean flag = true;
            while (flag) {
                try {
                    defaultMQAdminExt.createAndUpdateTopicConfig(broker, topicConfig);
                    flag = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            logger.info("[cockpit topic service]add topic config:" + topicConfig + " to broker :" + broker);

            return true;
        }

        return false;
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
            Set<String> masterBrokerAddressSet = new HashSet<>();
            if (null != topic.getBrokerAddress() && !topic.getBrokerAddress().isEmpty())
                masterBrokerAddressSet.add(topic.getBrokerAddress());
            else
                masterBrokerAddressSet.addAll(
                    CommandUtil.fetchMasterAddrByClusterName(defaultMQAdminExt, topic.getClusterName()));

            defaultMQAdminExt.
                    deleteTopicInBroker(masterBrokerAddressSet, topic.getTopic());

            Set<String> nameServerAddress = new HashSet<String>(defaultMQAdminExt.getNameServerAddressList());

            // Delete from name server.
            defaultMQAdminExt.deleteTopicInNameServer(nameServerAddress, topic.getTopic(), Helper.getStringBuild(masterBrokerAddressSet, ";"));
        } catch (Exception e) {
            logger.warn("[DELETE][TOPIC][MQADMIN] try to delete topic failed." + e);
            return false;
        } finally {
            defaultMQAdminExt.shutdown();
        }
        return true;
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
        return null;
    }
}
