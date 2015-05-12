package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsumerGroupMapper {

    List<ConsumerGroup> list(@Param("teamId") long teamId,
                             @Param("clusterName") String clusterName,
                             @Param("consumerGroupName") String consumerGroupName);

    ConsumerGroup get(long id);

    long insert(ConsumerGroup consumerGroup);

    void update(ConsumerGroup consumerGroup);

    void register(long id);

    void delete(long id);

    void deleteConsumerGroupTeamAssociation(@Param(value = "consumerGroupId") long consumerGroupId,
                                            @Param(value = "teamId") long teamId);

    void associateTeam(@Param(value = "consumerGroupId") long consumerGroupId,
                       @Param(value = "teamId") long teamId);

}
