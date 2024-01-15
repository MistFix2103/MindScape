    create table if not exists comment(
        id binary(16) PRIMARY KEY not null,
        text varchar(2048),
        time datetime(6),
        author_id binary(16) not null,
        story_id binary(16) not null
        ) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

    create table if not exists confirmation_code (
        id binary(16) primary key  not null,
        code varchar(255) not null,
        expiration_date datetime(6) not null,
        type enum ('USER_REGISTRATION','EXPERT_REGISTRATION','PASSWORD_RESET','CHANGE_EMAIL','CHANGE_PASSWORD','TWO_FACTOR') not null,
        email varchar(255) not null
        ) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

    create table if not exists story (
        id binary(16) primary key not null,
        text varchar(255),
        time datetime(6),
        author_id binary(16) not null
        )ENGINE=InnoDB DEFAULT CHARSET=UTF8;

    create table if not exists story_usage_req (
        id binary(16) primary key not null,
        researcher_id binary(16) not null,
        story_id binary(16) not null
        ) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

    create table if not exists users (
        id binary(16) primary key not null,
        document LONGBLOB,
        email varchar(255) not null,
        password varchar(255) not null,
        role enum ('USER','EXPERT','RESEARCHER') not null,
        username varchar(255) not null,
        verified TINYINT(1),
        `2fa` TINYINT(1) default '0' not null,
        image MEDIUMBLOB
        ) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

     alter table users add constraint email_fk unique (email);