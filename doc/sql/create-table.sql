CREATE DATABASE IF NOT EXISTS cockpit CHARACTER SET 'UTF8';

USE cockpit;

CREATE TABLE IF NOT EXISTS name_server (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  ip VARCHAR(64) NOT NULL,
  port SMALLINT NOT NULL DEFAULT 9876,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE = INNODB;

-- DEPRECATED
CREATE TABLE IF NOT EXISTS ip_mapping(
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  inner_ip VARCHAR(64) NOT NULL,
  public_ip VARCHAR(64) NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE = INNODB;
-- END OF DEPRECATED

CREATE TABLE IF NOT EXISTS data_center (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS broker (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  cluster_name VARCHAR(255) NOT NULL DEFAULT 'DefaultCluster',
  broker_name VARCHAR(255) NOT NULL,
  broker_id SMALLINT NOT NULL,
  address VARCHAR(255) NOT NULL,
  version VARCHAR(100) DEFAULT '3.2.2',
  dc INT NOT NULL REFERENCES data_center(id),
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP ,
  sync_time TIMESTAMP NULL,
  CONSTRAINT uniq_cluster_name_id UNIQUE (cluster_name, broker_name, broker_id)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS topic (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  topic VARCHAR(255) BINARY NOT NULL,
  cluster_name VARCHAR(100) NOT NULL DEFAULT 'DefaultCluster',
  `order` BOOL DEFAULT FALSE,
  status INT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE = INNODB;

CREATE TABLE topic_broker_xref (
  broker_id INT NOT NULL REFERENCES broker(id),
  topic_id INT NOT NULL REFERENCES topic(id),
  permission TINYINT NOT NULL DEFAULT 6,
  write_queue_num INT NOT NULL DEFAULT 4,
  read_queue_num INT NOT NULL DEFAULT 4,
  status_id INT NOT NULL DEFAULT 1 REFERENCES status_lu(id) ON DELETE RESTRICT ,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP ,
  sync_time TIMESTAMP NULL,
  CONSTRAINT uniq_broker_topic UNIQUE (broker_id, topic_id)
) ENGINE = INNODB;

CREATE TABLE topic_dc_xref(
  topic_id INT NOT NULL REFERENCES topic(id),
  dc_id INT NOT NULL REFERENCES data_center(id),
  status INT REFERENCES status_lu(id),
  CONSTRAINT uniq_topic_dc UNIQUE (topic_id, dc_id)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS consumer_group (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  cluster_name VARCHAR(255) NOT NULL DEFAULT 'DefaultCluster',
  which_broker_when_consume_slowly INT NOT NULL DEFAULT 1,
  consume_from_broker_id INT NOT NULL DEFAULT 0,
  group_name VARCHAR(255) NOT NULL,
  consume_enable BOOL NOT NULL DEFAULT TRUE ,
  consume_broadcast_enable BOOL NOT NULL DEFAULT FALSE,
  retry_max_times INT NOT NULL DEFAULT 16,
  retry_queue_num MEDIUMINT NOT NULL DEFAULT 1,
  consume_from_min_enable BOOL NOT NULL DEFAULT TRUE,
  status_id INT NOT NULL DEFAULT 1 REFERENCES status_lu(id) ON DELETE RESTRICT ,
  warn_threshold INT NOT NULL DEFAULT 1000,
  fatal_threshold INT NOT NULL DEFAULT 50000,
  project_id INT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS topic_consumer_group_xref (
  topic_id INT NOT NULL REFERENCES topic(id),
  consumer_group_id INT NOT NULL REFERENCES consumer_group(id),
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP ,
  CONSTRAINT uniq_topic_consumer_group UNIQUE (topic_id, consumer_group_id)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS broker_consumer_group_xref (
  broker_id INT NOT NULL REFERENCES broker(id),
  consumer_group_id INT NOT NULL REFERENCES consumer_group(id) ,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  update_time TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP ,
  sync_time TIMESTAMP NULL,
  CONSTRAINT uniq_broker_consumer_group UNIQUE (broker_id, consumer_group_id)
);

CREATE TABLE IF NOT EXISTS status_lu (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS consume_progress (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  consumer_group VARCHAR(255) NOT NULL,
  topic VARCHAR(255) NOT NULL,
  broker_name VARCHAR(255) NOT NULL,
  queue_id INT NOT NULL,
  broker_offset BIGINT NOT NULL DEFAULT 0,
  consumer_offset BIGINT NOT NULL DEFAULT 0,
  last_timestamp BIGINT NOT NULL DEFAULT 0,
  diff BIGINT NOT NULL DEFAULT 0,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `create_time` (`create_time`),
  KEY `consume_progress_topic` (`topic`),
  KEY `consume_progress_group` (`consumer_group`),
  KEY `consume_progress_group_t` (`consumer_group`,`create_time`)
) ENGINE = INNODB  DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS topic_progress (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  topic VARCHAR(255) NOT NULL,
  broker_name VARCHAR(255) NOT NULL,
  queue_id INT NOT NULL,
  broker_offset BIGINT NOT NULL DEFAULT 0,
  last_timestamp BIGINT NOT NULL DEFAULT 0,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `create_time` (`create_time`),
  INDEX `topic_progress_topic` (`topic`)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS name_server_kv (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name_space VARCHAR(255) NOT NULL,
  `key` VARCHAR(255) NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  status_id INT NOT NULL REFERENCES status_lu(id) ON DELETE RESTRICT
);


-- User
-- SET sql_mode='NO_AUTO_VALUE_ON_ZERO';
CREATE TABLE IF NOT EXISTS team (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS project (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL ,
  description VARCHAR(255) NOT NULL,
  team_id INT NOT NULL REFERENCES team(id)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS project_topic_xref (
  project_id VARCHAR(255) NOT NULL REFERENCES project(id),
  topic_id VARCHAR(255) NOT NULL REFERENCES topic(id),
  CONSTRAINT uniq_project_topic UNIQUE (project_id, topic_id)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS project_consumer_group_xref (
  project_id VARCHAR(255) NOT NULL REFERENCES project(id),
  consumer_group_id VARCHAR(255) NOT NULL REFERENCES consumer_group(id),
  CONSTRAINT uniq_project_consumer_group UNIQUE (project_id, consumer_group_id)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS cockpit_user (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(32) NOT NULL ,
  password  VARCHAR(64) NOT NULL,
  email VARCHAR(255) NOT NULL ,
  status_id INT NOT NULL REFERENCES status_lu(id) ON DELETE RESTRICT
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS team_user_xref (
  team_id INT NOT NULL REFERENCES team(id) ON DELETE RESTRICT ,
  user_id INT NOT NULL REFERENCES cockpit_user(id) ON DELETE RESTRICT ,
  CONSTRAINT UNIQUE (team_id, user_id)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS cockpit_role (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS cockpit_user_role_xref (
  user_id INT NOT NULL REFERENCES cockpit_user(id) ON DELETE RESTRICT ,
  role_id INT NOT NULL REFERENCES cockpit_role(id) ON DELETE RESTRICT ,
  CONSTRAINT UNIQUE (user_id, role_id)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS cockpit_user_login (
  user_name VARCHAR(32) NOT NULL ,
  login_status INT NOT NULL DEFAULT 1,
  retry INT NOT NULL DEFAULT 0,
  lock_time BIGINT NOT NULL DEFAULT 0
) ENGINE = INNODB;

-- Resource ownership

CREATE TABLE IF NOT EXISTS consumer_group_table_xref (
  id INT NOT NULL,
  group_name VARCHAR(255) NOT NULL,
  CONSTRAINT UNIQUE (id, group_name)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS topic_team_xref(
  topic_id INT NOT NULL REFERENCES topic(id) ON DELETE RESTRICT ,
  team_id INT NOT NULL REFERENCES team(id) ON DELETE RESTRICT ,
  CONSTRAINT UNIQUE (topic_id, team_id)
) ENGINE = INNODB;


CREATE TABLE IF NOT EXISTS consumer_group_team_xref(
  consumer_group_id INT NOT NULL REFERENCES consumer_group(id) ON DELETE RESTRICT ,
  team_id INT NOT NULL REFERENCES team(id) ON DELETE RESTRICT ,
  CONSTRAINT UNIQUE (consumer_group_id, team_id)
) ENGINE = INNODB;


-- Login
CREATE TABLE IF NOT EXISTS login (
  id INT NOT NULL PRIMARY KEY NOT NULL AUTO_INCREMENT,
  login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_id INT NOT NULL REFERENCES cockpit_user(id),
  token CHAR(32) NOT NULL
) ENGINE = INNODB;


-- Statistics

CREATE TABLE IF NOT EXISTS topic_stat (
  topic_id INT NOT NULL REFERENCES topic(id),
  broker_id INT NOT NULL REFERENCES broker(id),
  in_tps FLOAT NOT NULL DEFAULT 0,
  out_tps FLOAT NOT NULL DEFAULT 0,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS broker_stat (
  broker_id INT NOT NULL REFERENCES broker(id),
  in_tps FLOAT NOT NULL DEFAULT 0,
  out_tps FLOAT NOT NULL DEFAULT 0,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS warn_level_lu (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  info VARCHAR(255)
) ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS warning (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  msg MEDIUMTEXT NOT NULL ,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status INT NOT NULL REFERENCES status_lu(id),
  level INT NOT NULL REFERENCES warn_level_lu(id)
) ENGINE = INNODB;


CREATE TABLE resource_type_lu (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL
) ENGINE = INNODB;

CREATE TABLE resource_permission(
  resource_id INT NOT NULL,
  team_id INT NOT NULL REFERENCES team(id),
  resource_type_id INT REFERENCES resource_type_lu(id)
) ENGINE = INNODB;

CREATE INDEX idx_token ON login(token) USING HASH;

ALTER TABLE topic ADD CONSTRAINT uniq_cluster_topic UNIQUE(cluster_name, topic);

ALTER TABLE project ADD CONSTRAINT uniq_project UNIQUE(name);

ALTER TABLE consumer_group ADD CONSTRAINT  uniq_cluster_consumer_group UNIQUE (cluster_name, group_name);

ALTER TABLE broker ADD CONSTRAINT  uniq_cluster_broker_name_broker_id UNIQUE (cluster_name, broker_name, broker_id);

ALTER TABLE resource_permission ADD CONSTRAINT uniq_resource_id_type_team UNIQUE (resource_id, resource_type_id, team_id);

-- 新库无需执行
-- ALTER TABLE topic_progress ADD INDEX create_time (create_time);
-- ALTER TABLE consume_progress ADD INDEX create_time (create_time);
