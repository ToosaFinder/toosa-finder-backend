INSERT INTO public.events (id, name, creator_id, description, address, latitude, longitude, participants_limit,
                           start_time, is_public, is_closed)
VALUES (DEFAULT, 'Тестовое событие 1', 77777, 'этотестовое отписание а написал его паша', 'петухова, 1', 1.11, 2.22, 20,
        '2020-11-22 19:44:05.000000', true, false);

insert into events_tags(event_id, tag_id) VALUES (1, 1)