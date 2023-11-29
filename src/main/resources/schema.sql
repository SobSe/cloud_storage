
create table users (
    id serial,
    login varchar(255),
    password varchar(255),
    token varchar(255),
    PRIMARY KEY (id)
);

create table files (
    id bigserial,
    file_name varchar(255),
    data bytea,
    user_id integer,
    size bigint,
    constraint fk_users FOREIGN KEY (user_id) references users (id)
);