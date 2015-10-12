package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.DataCenter;
import com.ndpmedia.rocketmq.cockpit.model.Status;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.model.TopicAvailability;
import com.ndpmedia.rocketmq.cockpit.model.TopicHosting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TopicMapper {

    long insert(Topic topic);

    void insertTopicBrokerInfo(@Param("topic")Topic topic,
                               @Param("brokerId") long brokerId);

    void delete(long id);

    void update(Topic topic);

    void updateTopicBrokerInfo(@Param("topic") Topic topic,
                               @Param("brokerId") long brokerId);

    Topic get(@Param("id") long id, @Param("topic") String topic);

    void refresh(@Param("brokerId") long brokerId,
                 @Param("topicId") long topicId);

    /**
     * List by project or statusIds.
     * @param projectId Project ID.
     * @return List of topics.
     */
    List<Topic> list(@Param("projectId") long projectId,
                     @Param("statusIds") int[] statusIds,
                     @Param("broker") String broker,
                     @Param("cluster") String cluster);

    List<Long> getProjects(long topicId, String topic);

    List<TopicAvailability> queryTopicsAvailability();

    List<DataCenter> queryAllowedDC(long topicId);

    boolean isDCAllowed(@Param("topicId") long topicId,
                        @Param("dcId")long dcId);

    void insertDCAllowed(@Param("topicId") long topicId,
                         @Param("dcId")long dcId,
                         @Param("status")Status status);

    List<TopicHosting> queryHosting(@Param("topicId") long topicId,
                                    @Param("topic") String topic,
                                    @Param("dcId") int dcId);

    List<Long> queryTopicHostingBrokerIds(@Param("topicId") long topicId, @Param("dcId") long dcId);

    List<Long> queryAssociatedConsumerGroup(@Param("topicId") long topicId);

    void connectProject(@Param("topicId") long topicId, @Param("projectId") long projectId);

    void disconnectProject(@Param("topicId") long topicId, @Param("projectId") long projectId);
}
