create table events(
    id bigserial primary key,
    name varchar(256) not null,
    creator_id bigint not null references users on update cascade on delete cascade,
    description varchar(4096) not null,
    address varchar(512) not null,
    -- широта и долгота хранятся в формате decimal degrees
    latitude real not null,
    longitude real not null,
    participants_limit int not null,
    start_time timestamp not null,
    is_public boolean not null default true,
    is_closed boolean not null default true
);

create table tags(
    id bigserial primary key,
    name varchar(30) not null
);

create table events_tags(
    event_id bigint not null references events on update cascade on delete cascade,
    tag_id bigint not null references tags on update cascade on delete cascade
);

create table events_participants(
    event_id bigint not null references events on update cascade on delete cascade,
    participant_id bigint not null references users on update cascade on delete cascade
);

create table events_administrators(
    event_id bigint not null references events on update cascade on delete cascade,
    administrator_id bigint not null references users on update cascade on delete cascade
);