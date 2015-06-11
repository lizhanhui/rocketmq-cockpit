package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

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
     * List by team ID and topic.
     * @param teamId Team ID, 0 for administrator.
     * @param topic Topic name.
     * @return List of topics.
     */
    List<Topic> list(@Param("teamId") long teamId, @Param("topic") String topic, @Param("statusId") long statusId);

    List<Topic> detailList(@Param("teamId") long teamId, @Param("topic") String topic, @Param("statusId") long statusId);

    List<Topic> delList();

    List<Topic> activeList();

    List<Long> getTeamId(@Param("topicId") long topicId);

    void associateTeam(@Param("topicId") long topicId, @Param("teamId") long teamId);

    void removeTopicTeamAssociation(@Param("topicId") long topicId, @Param("teamId") long teamId);
}
