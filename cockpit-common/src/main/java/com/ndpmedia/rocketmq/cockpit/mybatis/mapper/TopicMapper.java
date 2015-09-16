package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.Project;
import com.ndpmedia.rocketmq.cockpit.model.Topic;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface TopicMapper {

    long insert(Topic topic);

    void delete(long id);

    void register(long id);

    void unregister(long id);

    void update(Topic topic);

    Topic get(@Param("id") long id,@Param("topic")  String topic,@Param("broker")  String broker,@Param("cluster")  String cluster);

    /**
     * List by project or status.
     * @param projectId Project ID.
     * @return List of topics.
     */
    List<Topic> list(@Param("projectId") long projectId, @Param("status") long status);

    List<Long> getProjects(long topicId);

    void connectProject(@Param("topicId") long topicId, @Param("projectId") long projectId);

    void disconnectProject(@Param("topicId") long topicId, @Param("projectId") long projectId);
}
