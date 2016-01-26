package com.ndpmedia.rocketmq.cockpit.mybatis.mapper;

import com.ndpmedia.rocketmq.cockpit.model.ConsumeProgress;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ConsumeProgressMapper {

    long create(@Param("group") String group);

    long insert(List<ConsumeProgress> consumeProgress);

    long insertPrivate(@Param("map") Map<String, Object> params);

    long updateTopicProgress(@Param("date") Date date);

    void delete(long id);

    List<String> consumerGroupList();

    List<String> topicList(@Param("consumerGroup") String consumerGroup);

    List<String> brokerList(@Param("consumerGroup") String consumerGroup, @Param("topic") String topic);

    List<String> queueList(@Param("consumerGroup") String consumerGroup, @Param("topic") String topic,
            @Param("brokerName") String brokerName);

    /**
     * Retrieve consume progress records by specified parameters. All of these parameters are optional.
     *
     * @param consumerGroup Optional consumer group.
     * @param topic         Optional topic.
     * @param brokerName    Optional broker name.
     * @param queueId       Optional queue ID, use -1 in case all queues are wanted.
     * @return list of consume progress records.
     */
    List<ConsumeProgress> diffList(@Param("consumerGroup") String consumerGroup, @Param("topic") String topic,
            @Param("brokerName") String brokerName, @Param("queueId") int queueId, @Param("tableID") String tableID );

    /**
     * Retrieve consume progress records by specified parameters. All of these parameters are optional.
     *
     * @param consumerGroup Optional consumer group.
     * @param topic         Optional topic.
     * @param brokerName    Optional broker name.
     * @param queueId       Optional queue ID, use -1 in case all queues are wanted.
     * @param date    Optional date
     * @return list of consume progress records.
     */
    List<Map<Object, Object>> lastDiff(@Param("consumerGroup") String consumerGroup, @Param("topic") String topic,
            @Param("brokerName") String brokerName, @Param("queueId") int queueId, @Param("date") Date date);

    /**
     * Retrieve consume progress records by specified parameters. All of these parameters are optional.
     *
     * @param consumerGroup Optional consumer group.
     * @param topic         Optional topic.
     * @param brokerName    Optional broker name.
     * @param queueId       Optional queue ID, use -1 in case all queues are wanted.
     * @return list of consume progress records.
     */
    List<ConsumeProgress> list(@Param("consumerGroup") String consumerGroup, @Param("topic") String topic,
            @Param("brokerName") String brokerName, @Param("queueId") int queueId);


    List<ConsumeProgress> brokerTPSListOLD(@Param("consumerGroup") String consumerGroup, @Param("topic") String topic,
                                    @Param("brokerName") String brokerName, @Param("queueId") int queueId);

    /**
     *
     * @param topic
     * @return
     */
    List<ConsumeProgress> brokerTPSList(@Param("consumerGroup") String consumerGroup, @Param("topic") String topic,
            @Param("brokerName") String brokerName, @Param("queueId") int queueId);

    /**
     * Delete data that are older than the specified date.
     *
     * @param date specified date
     * @return number of rows deleted.
     */
    int bulkDelete(@Param("groupName")String groupName,@Param("date") Date date);

    int bulkDeleteT(@Param("date") Date date);

    List<Map<Object, Object>> groupTableXREFList();

    int groupTableMINID();

    int groupTableMAXID();

    List<Integer> groupTableIDS();

    int groupTableID(@Param("groupName") String groupName);

    int groupTableXREFInsert(@Param("id") int id, @Param("groupName") String groupName);

    List<ConsumeProgress> lastrow();

    List<ConsumeProgress> topicReady(@Param("date") Date date);

    List<String> showTables();

    int dropTable(@Param("name") String name);
}
