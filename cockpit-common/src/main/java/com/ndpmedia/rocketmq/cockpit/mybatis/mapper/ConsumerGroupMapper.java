package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroup;
import com.ndpmedia.rocketmq.cockpit.model.ConsumerGroupHosting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsumerGroupMapper {

    List<ConsumerGroup> list(@Param("projectId") long projectId,
                             @Param("clusterName") String clusterName,
                             @Param("consumerGroupName") String consumerGroupName,
                             @Param("brokerId") long brokerId,
                             @Param("brokerAddress") String brokerAddress);

    ConsumerGroup get(@Param("id") long id,
                      @Param("groupName") String groupName);

    long insert(ConsumerGroup consumerGroup);

    void update(ConsumerGroup consumerGroup);

    void delete(long id);

    List<ConsumerGroupHosting> queryHosting(@Param("consumerGroupId") long consumerGroupId,
                                            @Param("status") int status,
                                            @Param("brokerId") Integer brokerId,
                                            @Param("dcId") long dcId);

    void disconnectProject(@Param(value = "consumerGroupId") long consumerGroupId,
                           @Param(value = "projectId") long projectId);

    void connectProject(@Param(value = "consumerGroupId") long consumerGroupId,
                        @Param(value = "projectId") long projectId);

}
