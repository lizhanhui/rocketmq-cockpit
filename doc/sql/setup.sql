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