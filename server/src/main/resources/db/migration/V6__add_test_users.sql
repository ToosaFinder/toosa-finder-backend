insert into users values
    (77777, 'test1@example.com', 'test1', '$2a$10$tJMOu5zPwmDzvRaYAFSVVuRlDIvFydSL/MlYpelcxzRWSAY0R7id2', '2020-11-09'),
    (77778, 'test2@example.com', 'test2', '$2a$10$cyjM8Fojwmy1jE9Aske1a.J/PW.utHktTyawr6I/x8C04v5B/RVSC', '2020-11-28'),
    (77779, 'test3@example.com', 'test3', '$2a$10$n7rIid.jzd4XlfpMZ09UjOMzY/LEwDv79knwTWnYZ8bFnMet0p/ei', '2020-12-18');

insert into users_roles(user_id, role_id) values
    (77777, 1), (77778, 1), (77779, 1);