package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroupHosting;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ConsumerGroupMapper {

    List<ConsumerGroup> list(@Param("projectId") long projectId,
                             @Param("clusterName") String clusterName,
                             @Param("consumerGroupName") String consumerGroupName,
                             @Param("brokerId") long brokerId,
                             @Param("brokerAddress") String brokerAddress,
                             @Param("limits") int limits);

    List<ConsumerGroup> listByTopic(@Param("topicId")long topicId);

    List<ConsumerGroup> listByProject(@Param("projectId") long projectId);

    List<ConsumerGroup> listByOtherProject(@Param("projectId") long projectId);

    List<ConsumerGroup> listToDEL(@Param("syncTime") Date syncTime);

    ConsumerGroup get(@Param("id") long id);

    ConsumerGroup getByName(@Param("groupName") String groupName);

    void refresh(@Param("brokerId")long brokerId,
                 @Param("consumerGroupId")long consumerGroupId);

    long insert(ConsumerGroup consumerGroup);

    void update(ConsumerGroup consumerGroup);

    int delete(long id);

    int deleteTCG(long id);

    int deleteCGT(@Param("groupName") String groupName);

    int deleteBCG(long id);

    List<ConsumerGroupHosting> queryHosting(@Param("consumerGroupId") long consumerGroupId,
                                            @Param("statuses") int[] status,
                                            @Param("brokerId") Integer brokerId,
                                            @Param("dcId") long dcId,
                                            @Param("syncTimeDeadline") Date syncTimeDeadline);

    void disconnectProject(@Param(value = "consumerGroupId") long consumerGroupId,
                           @Param(value = "projectId") long projectId);

    void connectProject(@Param(value = "consumerGroupId") long consumerGroupId,
                        @Param(value = "projectId") long projectId);


    List<ConsumerGroupHosting> queryEndangeredHosting(@Param("brokerId") long brokerId);

    List<ConsumerGroupHosting> queryApprovedHosting(@Param("brokerId") long brokerId);

    long updateConsumerGroupTopics(@Param("date") Date date);

}
