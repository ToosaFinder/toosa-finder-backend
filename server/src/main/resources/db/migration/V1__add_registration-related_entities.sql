create table users(
    id bigserial primary key,
    email varchar(320) not null,
    login varchar(512) not null,
    password varchar(4096) not null,
    registration_time timestamp not null
);

create table roles(
    id bigserial primary key,
    name varchar(256) not null
);

create table users_roles(
    user_id bigint not null references users on update cascade on delete cascade,
    role_id bigint not null references roles on update cascade on delete cascade
);

create table email_tokens(
    id bigserial primary key,
    token uuid not null unique,
    user_id bigint not null references users on update cascade on delete cascade,
    creation_time timestamp not null
);
