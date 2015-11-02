create table desk(id int NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) not NULL );
create table chair(id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
                   name VARCHAR(100) not NULL,
                   desk_id INT NOT NULL REFERENCES desk(id));
insert into desk(id, name) VALUE (1, "d1");
insert into chair(id, name, desk_id) VALUE (1, "c1", 1);