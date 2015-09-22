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
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicNSService;
import com.ndpmedia.rocketmq.cockpit.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("cockpitTopicNSService")
public class CockpitTopicNSServiceImpl implements CockpitTopicNSService {

    private Logger logger = LoggerFactory.getLogger(CockpitTopicNSServiceImpl.class);

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
    public TopicConfig getTopicConfigByTopicName(DefaultMQAdminExt defaultMQAdminExt, String topic) {
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setTopicName(topic);

        TopicRouteData topicRouteData = null;
        int flag = 0;
        while (flag++ < 5) {
            try {
                topicRouteData = defaultMQAdminExt.examineTopicRouteInfo(topic);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getMessage().contains("No topic route info"))
                    break;
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
        Set<String> topicBroker = new HashSet<>();
        TopicRouteData topicRouteData = null;
        int flag = 0;
        while (flag++ < 5) {
            try {
                topicRouteData = defaultMQAdminExt.examineTopicRouteInfo(topic);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (null != topicRouteData) {
            List<BrokerData> brokerDatas = topicRouteData.getBrokerDatas();

            for (BrokerData brokerData : brokerDatas) {
                for (Map.Entry<Long, String> entry : brokerData.getBrokerAddrs().entrySet()) {
                    topicBroker.add(entry.getValue());
                }
            }
        }
        return topicBroker;
    }

    @Override
    public boolean rebuildTopicConfig(DefaultMQAdminExt defaultMQAdminExt, TopicConfig topicConfig, String broker) {

        Set<String> localBroker = getTopicBrokers(defaultMQAdminExt, topicConfig.getTopicName());

        if (!localBroker.contains(broker)) {
            int flag = 0;
            while (flag++ < 5) {
                try {
                    defaultMQAdminExt.createAndUpdateTopicConfig(broker, topicConfig);
                    break;
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

    private static TopicConfig wrapTopicToTopicConfig(Topic topic) {
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setWriteQueueNums(topic.getWriteQueueNum());
        topicConfig.setReadQueueNums(topic.getReadQueueNum());
        topicConfig.setTopicName(topic.getTopic());
        return topicConfig;
    }

}
