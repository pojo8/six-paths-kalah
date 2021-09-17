create table game_state
(
    id     serial not null,
    uri    varchar(100),
    url    varchar(150),
    status json
);

ALTER SEQUENCE game_state_id_seq RESTART WITH 1000;