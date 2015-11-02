-- Find topics that resides on less than 2 broker servers.
-- -----------------------------------------------------------------------------------------------
SELECT topic_dc_xref.topic_id
  , topic_dc_xref.dc_id
  , COUNT(broker.id)
FROM topic_dc_xref
  LEFT JOIN topic_broker_xref ON topic_dc_xref.topic_id = topic_broker_xref.topic_id
  LEFT JOIN broker ON broker.id = topic_broker_xref.broker_id AND broker.dc = topic_dc_xref.dc_id
GROUP BY topic_dc_xref.dc_id, topic_dc_xref.topic_id
HAVING COUNT(broker.id) < 2;
-- -----------------------------------------------------------------------------------------------

-- Find Brokers that has fewest topic queues on.
-- -----------------------------------------------------------------------------------------------
SELECT broker.dc
  , broker.id AS broker_id
  , IFNULL(SUM(topic.read_queue_num + topic.write_queue_num), 0) AS topic_queue_num
FROM broker
  LEFT JOIN topic_broker_xref ON broker.id = topic_broker_xref.broker_id
  LEFT JOIN topic ON topic_broker_xref.topic_id = topic.id
GROUP BY broker.dc, broker.id
ORDER BY topic_queue_num;
-- -----------------------------------------------------------------------------------------------


