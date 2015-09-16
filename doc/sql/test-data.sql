INSERT INTO topic(id, topic, cluster_name, permission, write_queue_num, read_queue_num, unit, has_unit_subscription, broker_address, order_type, status_id, create_time, update_time)
    VALUES (1, 'Topic 1', "Default Cluster", 6, 8, 8, false, false, null, false, 1, null, null);
INSERT INTO topic(id, topic, cluster_name, permission, write_queue_num, read_queue_num, unit, has_unit_subscription, broker_address, order_type, status_id, create_time, update_time)
    VALUES (2, 'Topic 3', "Default Cluster", 6, 8, 8, false, false, null, false, 1, null, null);
INSERT INTO topic(id, topic, cluster_name, permission, write_queue_num, read_queue_num, unit, has_unit_subscription, broker_address, order_type, status_id, create_time, update_time)
    VALUES (3, 'Topic 3', "Default Cluster", 6, 8, 8, false, false, null, false, 1, null, null);

INSERT INTO broker(id, cluster_name, broker_name, broker_id, address, version, dc, last_update_time) VALUES (1, "Default Cluster", "broker 1", 0, "host1:10911", "3.2.2", 1, null);
INSERT INTO broker(id, cluster_name, broker_name, broker_id, address, version, dc, last_update_time) VALUES (2, "Default Cluster", "broker 2", 0, "host2:10911", "3.2.2", 1, null);
INSERT INTO broker(id, cluster_name, broker_name, broker_id, address, version, dc, last_update_time) VALUES (3, "Default Cluster", "broker 3", 0, "host3:10911", "3.2.2", 1, null);
INSERT INTO broker(id, cluster_name, broker_name, broker_id, address, version, dc, last_update_time) VALUES (4, "Default Cluster", "broker 4", 0, "host3:10911", "3.2.2", 2, null);

INSERT INTO topic_dc_xref(topic_id, dc_id, status) VALUES (1, 1, 1);
INSERT INTO topic_dc_xref(topic_id, dc_id, status) VALUES (2, 1, 1);
INSERT INTO topic_dc_xref(topic_id, dc_id, status) VALUES (2, 2, 1);

INSERT INTO topic_broker_xref(broker_id, topic_id, status) VALUES (1, 1, 1);
INSERT INTO topic_broker_xref(broker_id, topic_id, status) VALUES (2, 1, 1);
INSERT INTO topic_broker_xref(broker_id, topic_id, status) VALUES (2, 2, 1);

