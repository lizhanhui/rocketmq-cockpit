USE cockpit;

INSERT INTO status_lu(id, name) VALUES (1, "DRAFT");
INSERT INTO status_lu(id, name) VALUES (2, "ACTIVE");
INSERT INTO status_lu(id, name) VALUES (3, "DELETED");

INSERT INTO cockpit_role(id, name) VALUES(1, 'ROLE_ADMIN');
INSERT INTO cockpit_role(id, name) VALUES(2, 'ROLE_MANAGER');
INSERT INTO cockpit_role(id, name) VALUES(3, 'ROLE_USER');
INSERT INTO cockpit_role(id, name) VALUES(4, 'ROLE_WATCHER');

INSERT INTO cockpit_user_role_xref(user_id, role_id) VALUES (1, 1);
INSERT INTO cockpit_user_role_xref(user_id, role_id) VALUES (2, 3);

INSERT INTO data_center(id, name) VALUES (1, 'US East');
INSERT INTO data_center(id, name) VALUES (2, 'Singapore');
INSERT INTO data_center(id, name) VALUES (3, 'US West');
INSERT INTO data_center(id, name) VALUES (5, 'South America');

INSERT INTO team(id, name) VALUES (1, "TP");
INSERT INTO team(id, name) VALUES (2, "Facebook");
INSERT INTO team(id, name) VALUES (3, "YeahMobi");

INSERT INTO project(id, name, description, team_id) VALUES (1, 'Default', 'Default Project to host orphans', 1);