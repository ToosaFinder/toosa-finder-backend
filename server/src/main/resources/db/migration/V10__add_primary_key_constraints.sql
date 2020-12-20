alter table users_roles
    add constraint users_roles_pkey primary key (user_id, role_id);

alter table events_tags
    add constraint events_tags_pkey primary key (event_id, tag_id);

alter table events_participants
    add constraint events_participants_pkey primary key (event_id, participant_id);

alter table events_administrators
    add constraint events_administrators_pkey primary key (event_id, administrator_id);
