alter table users
    add constraint email_unique unique(email),
    add constraint login_unique unique(login);