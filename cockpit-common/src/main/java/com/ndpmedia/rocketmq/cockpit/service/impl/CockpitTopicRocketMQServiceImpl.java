package com.ndpmedia.rocketmq.cockpit.service.impl;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.TopicConfig;
import com.alibaba.rocketmq.common.protocol.body.TopicList;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.common.protocol.route.QueueData;
import com.alibaba.rocketmq.common.protocol.route.TopicRouteData;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.admin.MQAdminExt;
import com.ndpmedia.rocketmq.cockpit.exception.CockpitException;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.TopicMapper;
import com.ndpmedia.rocketmq.cockpit.service.CockpitTopicRocketMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("cockpitTopicRocketMQService")
public class CockpitTopicRocketMQServiceImpl implements CockpitTopicRocketMQService {

    private Logger logger = LoggerFactory.getLogger(CockpitTopicRocketMQServiceImpl.class);

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public Set<String> fetchAllTopics(MQAdminExt adminExt, boolean includeSystemTopic) throws CockpitException {
        boolean createAdmin = (null == adminExt);
        try {
            if (createAdmin) {
                adminExt = new DefaultMQAdminExt("CockpitMQAdmin");
                adminExt.start();
            }

            TopicList topicList = adminExt.fetchAllTopicList();
            Set<String> topicSet = topicList.getTopicList();
            if (!includeSystemTopic) {
                Iterator<String> topicIterator = topicSet.iterator();
                while (topicIterator.hasNext()) {
                    String topic = topicIterator.next();
                    if (topic.startsWith(MixAll.DLQ_GROUP_TOPIC_PREFIX)
                            || topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
                        topicIterator.remove();
                    }
                }
            }
            return topicSet;
        } catch (RemotingException | MQClientException | InterruptedException e) {
            throw new CockpitException("Failed to interact with name server.");
        } finally {
            if (createAdmin && null != adminExt) {
                adminExt.shutdown();
            }
        }
    }

    @Override
    public TopicConfig getTopicConfigByTopicName(MQAdminExt adminExt, String topic) throws CockpitException {

        boolean createAdmin = (null == adminExt);


        try {
            if (createAdmin) {
                adminExt = new DefaultMQAdminExt("CockpitMQAdmin");
                adminExt.start();
            }

            TopicConfig topicConfig = new TopicConfig();
            topicConfig.setTopicName(topic);

            TopicRouteData topicRouteData = null;
            int flag = 0;
            while (flag++ < 5) {
                try {
                    topicRouteData = adminExt.examineTopicRouteInfo(topic);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("No topic route info")) {
                        break;
                    }
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
        } catch (MQClientException e) {
            throw new CockpitException("Admin tool falters.", e);
        } finally {
            if (createAdmin && null != adminExt) {
                adminExt.shutdown();
            }
        }

        logger.info("[sync topic] big error! find topic but no topic config !");
        return null;
    }

    @Override
    public Set<String> getTopicBrokers(MQAdminExt adminExt, String topic) throws CockpitException {
        boolean createAdmin = (null == adminExt);

        try {
            if (createAdmin) {
                adminExt = new DefaultMQAdminExt("CockpitMQAdmin");
                adminExt.start();
            }

            Set<String> topicBroker = new HashSet<>();
            TopicRouteData topicRouteData = null;
            int flag = 0;
            while (flag++ < 5) {
                try {
                    topicRouteData = adminExt.examineTopicRouteInfo(topic);
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
        } catch (MQClientException e) {
            throw new CockpitException("Admin tool falters.", e);
        } finally {
            if (createAdmin && null != adminExt) {
                adminExt.shutdown();
            }
        }
    }

    @Override
    public boolean rebuildTopicConfig(MQAdminExt adminExt, TopicConfig topicConfig, String broker) throws CockpitException {

        boolean createAdmin = (null == adminExt);

        try {
            if (createAdmin) {
                adminExt = new DefaultMQAdminExt("CockpitMQAdmin");
                adminExt.start();
            }

            Set<String> localBroker = getTopicBrokers(adminExt, topicConfig.getTopicName());
            if (!localBroker.contains(broker)) {
                int flag = 0;
                while (flag++ < 5) {
                    try {
                        adminExt.createAndUpdateTopicConfig(broker, topicConfig);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                logger.info("[cockpit topic service]add topic config:" + topicConfig + " to broker :" + broker);

                return true;
            }

            return false;
        } catch (MQClientException e) {
            throw new CockpitException("Admin tool falters", e);
        } finally {
            if (createAdmin && null != adminExt) {
                adminExt.shutdown();
            }
        }
    }

    @Override
    public boolean createOrUpdateTopic(MQAdminExt adminExt, Topic topic) throws CockpitException {
        throw new CockpitException("Not Implemented");
    }

    @Override
    public boolean deleteTopic(MQAdminExt adminExt, Topic topic) throws CockpitException {
        throw new CockpitException("Not Implemented");
    }

    public static TopicConfig wrapTopicToTopicConfig(Topic topic) {
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setWriteQueueNums(topic.getWriteQueueNum());
        topicConfig.setReadQueueNums(topic.getReadQueueNum());
        topicConfig.setTopicName(topic.getTopic());
        return topicConfig;
    }
}