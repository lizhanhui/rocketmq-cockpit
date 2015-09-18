package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.DataCenter;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import com.ndpmedia.rocketmq.cockpit.model.TopicAvailability;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TopicMapper {

    long insert(Topic topic);

    void delete(long id);

    void update(Topic topic);

    Topic get(@Param("id") long id, @Param("topic") String topic);

    /**
     * List by project or status.
     * @param projectId Project ID.
     * @return List of topics.
     */
    List<Topic> list(@Param("projectId") long projectId,
                     @Param("status") long status,
                     @Param("broker") String broker,
                     @Param("cluster") String cluster);

    List<Long> getProjects(long topicId, String topic);

    List<TopicAvailability> queryTopicsAvailability();

    List<DataCenter> queryAllowedDC(long topicId);

    List<Long> queryTopicHostingBrokerIds(@Param("topicId") long topicId, @Param("dcId") long dcId);

    void connectProject(@Param("topicId") long topicId, @Param("projectId") long projectId);

    void disconnectProject(@Param("topicId") long topicId, @Param("projectId") long projectId);
}
