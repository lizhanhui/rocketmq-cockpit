package com.ndpmedia.rocketmq.cockpit.scheduler;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.TopicBrokerInfo;
import com.ndpmedia.rocketmq.cockpit.mybatis.mapper.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

/**
 * Created by robert on 2015/6/9.
 * data aging scheduler.
 * 1.consume progress, message flow
 * 2.login message
 */
@Component
public class DataAgingScheduler {
    private Logger logger = LoggerFactory.getLogger(DataAgingScheduler.class);

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private CockpitMessageMapper cockpitMessageMapper;

    @Autowired
    private ConsumeProgressMapper consumeProgressMapper;

    @Autowired
    private ConsumerGroupMapper consumerGroupMapper;

    @Autowired
    private TopicMapper topicMapper;

    /**
     * delete consumer group
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteDeletedConsumerGroup(){
        logger.info("[MONITOR][DELETE CONSUMER GROUP] Start to clean consumer group data");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        List<ConsumerGroup> consumerGroups = consumerGroupMapper.listToDEL(calendar.getTime());
        for (ConsumerGroup consumerGroup:consumerGroups){
            deleteConsumerGroup(consumerGroup);
        }

        logger.info("[MONITOR][DELETE CONSUMER GROUP] clean consumer group data end");
    }

    /**
     * delete topic
     */
    @Scheduled(cron = "31 3 0 * * *")
    public void deleteDeletedTopic(){
        logger.info("[MONITOR][DELETE TOPIC] Start to clean topic data");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        List<TopicBrokerInfo> topicBrokerInfos = topicMapper.listToDEL(calendar.getTime());
        for (TopicBrokerInfo topicBrokerInfo:topicBrokerInfos){
            deleteTopic(topicBrokerInfo);
        }
        logger.info("[MONITOR][DELETE TOPIC] clean topic data end");
    }

    /**
     * schedule:delete old comsume progress 、message flow records.
     * period:one hour(the first second of an hour)
     * span: records old than one day
     */
    @Scheduled(cron = "0 0 * * * *")
    public void deleteDeprecatedData() {
        logger.info("[MONITOR][OLD DATA DELETE] Start to clean deprecated data");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        int numberOfRecordsDeleted = consumeProgressMapper.bulkDelete("", calendar.getTime());
        logger.info("Deleted " + numberOfRecordsDeleted + " consume progress records.");
//        numberOfRecordsDeleted = cockpitMessageMapper.bulkDelete(calendar.getTime());
//        logger.info("Deleted " + numberOfRecordsDeleted + " message flow records.");

        calendar.add(Calendar.HOUR_OF_DAY, -48);

        numberOfRecordsDeleted = consumeProgressMapper.bulkDeleteT(calendar.getTime());
        logger.info("Deleted " + numberOfRecordsDeleted + " topic progress records.");

        List<Integer> IDs = consumeProgressMapper.groupTableIDS();
        for(int id : IDs){
            try {
                numberOfRecordsDeleted = consumeProgressMapper.bulkDelete("_" + id, calendar.getTime());
                logger.info("Deleted " + numberOfRecordsDeleted + " in " + id + "'s consume progress records.");
            }catch (Exception e){
                logger.warn("[MONITOR][OLD DATA DELETE] some table already deleted.");
            }
        }

        logger.info("[MONITOR][OLD DATA DELETE]  clean deprecated data end");
    }

    /**
     * schedule: delete login message
     * period:30 second
     * span:old than 30 minutes
     */
    @Scheduled(fixedRate = 60000)
    public void deleteDeprecatedLoginData() {
        logger.info("[MONITOR][LOGIN DELETE] Start to clean login data");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30);
        loginMapper.delete(calendar.getTime());
        logger.info("[MONITOR][LOGIN DELETE] clean login data end");
    }

    @Transactional
    private void deleteConsumerGroup(ConsumerGroup consumerGroup){
        try {
            long nums = 0;
            logger.info("[MONITOR][DELETE CONSUMER GROUP] START TO DELETE :" + consumerGroup.getGroupName());
            //delete from consumer_group
            nums = consumerGroupMapper.delete(consumerGroup.getId());
            logger.info("[MONITOR][DELETE CONSUMER GROUP] DELETE FROM consumer_group :" + nums);
            //delete from topic_consumer_group_xref
            nums = consumerGroupMapper.deleteTCG(consumerGroup.getId());
            logger.info("[MONITOR][DELETE CONSUMER GROUP] DELETE FROM topic_consumer_group_xref :" + nums);
            //delete from consumer_group_table_xref
            nums = consumerGroupMapper.deleteCGT(consumerGroup.getGroupName());
            logger.info("[MONITOR][DELETE CONSUMER GROUP] DELETE FROM consumer_group_table_xref :" + nums);
            //delete from broker_consumer_group_xref
            nums = consumerGroupMapper.deleteBCG(consumerGroup.getId());
            logger.info("[MONITOR][DELETE CONSUMER GROUP] DELETE FROM broker_consumer_group_xref :" + nums);
            //delete from project_consumer_group_xref
            consumerGroupMapper.disconnectProject(consumerGroup.getId(), 0);
        }catch (Exception e){
            logger.error("[MONITOR][DELETE CONSUMER GROUP] DELETE FAILED! group:" + consumerGroup.getGroupName());
        }
    }

    @Transactional
    private void deleteTopic(TopicBrokerInfo topicBrokerInfo){
        try {
            long nums = 0;
            logger.info("[MONITOR][DELETE TOPIC] START TO DELETE :" + topicBrokerInfo.getTopicMetadata().getTopic());
            //delete from topic_broker_info
            nums = topicMapper.deleteTB(topicBrokerInfo.getTopicMetadata().getId(), topicBrokerInfo.getBroker().getId());
            logger.info("[MONITOR][DELETE TOPIC] delete from topic_broker_info :" + nums);
            //check this topic has other broker
            if (topicMapper.queryTopicBrokerInfo(topicBrokerInfo.getTopicMetadata().getId(), 0, 0).size() > 0){
                logger.info("[MONITOR][DELETE TOPIC] just delete the topic :" + topicBrokerInfo.getTopicMetadata().getTopic() + " on broker :"
                    + topicBrokerInfo.getBroker().getBrokerName());
            }else {
                //delete from project_topic_xref
                topicMapper.disconnectProject(topicBrokerInfo.getTopicMetadata().getId(), 0);
                //delete from topic
                nums = topicMapper.delete(topicBrokerInfo.getTopicMetadata().getId());
                logger.info("[MONITOR][DELETE TOPIC] delete from topic : " + nums);
                //delete from topic_dc_xref
                nums = topicMapper.deleteTDX(topicBrokerInfo.getTopicMetadata().getId());
                logger.info("[MONITOR][DELETE TOPIC] delete from topic_dc_xref :" + nums);
                //delete from topic_consumer_group_xref
                nums = topicMapper.deleteTCGX(topicBrokerInfo.getTopicMetadata().getId());
                logger.info("[MONITOR][DELETE TOPIC] delete from topic_consumer_group_xref :" + nums);
                //delete from topic_stat
                nums = topicMapper.deleteTS(topicBrokerInfo.getTopicMetadata().getId());
                logger.info("[MONITOR][DELETE TOPIC] delete from topic_stat :" + nums);
                //delete from topic_team_xref
                nums = topicMapper.deleteTTX(topicBrokerInfo.getTopicMetadata().getId());
                logger.info("[MONITOR][DELETE TOPIC] delete from topic_team_xref :" + nums);
            }
        }catch (Exception e){
            logger.error("[MONITOR][DELETE TOPIC]DELETE FAILED! topic:" + topicBrokerInfo.getTopicMetadata().getTopic());
        }
    }
}
