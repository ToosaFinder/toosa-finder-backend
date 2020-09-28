alter table email_tokens
add column expiration_time bigint not null default 0;

alter table email_tokens
rename column token to value;