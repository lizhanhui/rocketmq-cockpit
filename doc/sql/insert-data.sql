USE cockpit;

INSERT INTO cockpit_user(id, username, password, email, status_id) VALUES (1, 'root', '$2a$10$.b5O4pAsTd6CyptUbXrMtOjJH1qYlbcVzk8WDPvb8dWwAXpfKZHwS', "admin@rocketmq.com", 5);
INSERT INTO cockpit_user(id, username, password, email, status_id) VALUES (2, 'xutao', '$2a$10$Q9W2Zh/h/viu/zSMT2CbL.Dnt5LLbDGDMo.N/xfc7m6TIUCbeLpvO', "robert@yeahmobi.com", 5);

INSERT INTO cockpit_role(id, name) VALUES(1, 'ROLE_ADMIN');
INSERT INTO cockpit_role(id, name) VALUES(2, 'ROLE_MANAGER');
INSERT INTO cockpit_role(id, name) VALUES(3, 'ROLE_USER');
INSERT INTO cockpit_role(id, name) VALUES(4, 'ROLE_WATCHER');

INSERT INTO cockpit_user_role_xref(user_id, role_id) VALUES (1, 1);
INSERT INTO cockpit_user_role_xref(user_id, role_id) VALUES (1, 2);
INSERT INTO cockpit_user_role_xref(user_id, role_id) VALUES (2, 2);
INSERT INTO cockpit_user_role_xref(user_id, role_id) VALUES (2, 3);

INSERT INTO consumer_group(group_name, broker_address, broker_id) VALUES ('CG_QuickStart', '54.94.212.186:10911', 0);
INSERT INTO consumer_group(group_name, broker_address, broker_id) VALUES ('CG_QuickStart', '172.30.50.54:10911', 0);
INSERT INTO consumer_group(group_name, broker_address, broker_id) VALUES ('C_GKT_MQ_GROUP', '172.30.50.54:10911', 0);

INSERT INTO data_center(id, name) VALUES (100, 'Development');
INSERT INTO data_center(id, name) VALUES (1, 'US East');
INSERT INTO data_center(id, name) VALUES (2, 'Singapore');
INSERT INTO data_center(id, name) VALUES (3, 'US West');
INSERT INTO data_center(id, name) VALUES (5, 'South America');
INSERT INTO data_center(id, name) VALUES (7, 'Ali Test');

-- Insert name server list.
INSERT INTO name_server(ip, port, create_time, update_time) VALUES ('54.173.39.198', 9876, now(), now());
INSERT INTO name_server(ip, port, create_time, update_time) VALUES ('54.173.209.191', 9876, NOW(), NOW());
INSERT INTO name_server(ip, port, create_time, update_time) VALUES ('172.30.50.54', 9876, NOW(), NOW());
INSERT INTO name_server(ip, port, create_time, update_time) VALUES ('10.2.255.100', 9876, NOW(), NOW());

INSERT INTO name_server_kv(id, name_space, `key`, `value`, status_id) VALUES (NULL , "DC_SELECTOR", "DC_DISPATCH_STRATEGY", "BY_RATIO", 2);
INSERT INTO name_server_kv(id, name_space, `key`, `value`, status_id) VALUES (NULL , "DC_SELECTOR", "DC_DISPATCH_RATIO", "1:0.25,2:0.3,3:0.25,5:0.2", 2);

INSERT INTO project(id, name, description, team_id) VALUES (1, 'Default', 'Default Project to host orphans', 1);
INSERT INTO project(id, name, description, team_id) VALUES (2, 'Test Project', 'Project Desc', 2);

INSERT INTO resource_type_lu(id, name) VALUES (1, 'Project');

INSERT INTO status_lu(id, name) VALUES (1, "DRAFT");
INSERT INTO status_lu(id, name) VALUES (2, "PENDING");
INSERT INTO status_lu(id, name) VALUES (3, "APPROVED");
INSERT INTO status_lu(id, name) VALUES (4, "REJECTED");
INSERT INTO status_lu(id, name) VALUES (5, "ACTIVE");
INSERT INTO status_lu(id, name) VALUES (6, "DELETED");

INSERT INTO team(id, name) VALUES (1, "TP");
INSERT INTO team(id, name) VALUES (2, "Facebook");
INSERT INTO team(id, name) VALUES (3, "AFF Network");
INSERT INTO team(id, name) VALUES (8, "TEST");

INSERT INTO team_user_xref(team_id, user_id) VALUES (1, 1);
INSERT INTO team_user_xref(team_id, user_id) VALUES (1, 2);

INSERT INTO topic(topic, broker_address) VALUES ('T_QuickStart', '54.94.212.186:10911');
INSERT INTO topic(topic, broker_address) VALUES ('T_QuickStart', '54.94.203.40:10911');
INSERT INTO topic(topic, broker_address) VALUES ('TopicTest_Robert', '172.30.50.54:10911');
INSERT INTO topic(topic, broker_address) VALUES ('T_PARSER', '172.30.50.54:10911');
INSERT INTO topic(topic, broker_address) VALUES ('T_QuickStart', '172.30.50.54:10911');
INSERT INTO topic(topic, broker_address) VALUES ('T_QuickStart', '172.30.50.54:10911');

INSERT INTO warn_level_lu(id, name, info) VALUES (1, 'Info', "Information");
INSERT INTO warn_level_lu(id, name, info) VALUES (2, 'Warn', "Warning");
INSERT INTO warn_level_lu(id, name, info) VALUES (3, 'Error', "Error");
INSERT INTO warn_level_lu(id, name, info) VALUES (4, 'Critical', "Critial");
INSERT INTO warn_level_lu(id, name, info) VALUES (5, 'Fatal', "Fatal");