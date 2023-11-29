insert into users
    (login, password)
values
    ('login@gmail.com', '{noop}123');

insert into users
    (login, password, token)
values
    ('loginwithtoken@gmail.com', '{noop}123', '01d3da2b-d02e-42fe-8a08-fdb6d1f597d6');
