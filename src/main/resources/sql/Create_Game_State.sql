create table game_state
(
    id     serial not null,
    url    varchar(150),
    status json
);

ALTER SEQUENCE game_state_id_seq RESTART WITH 1000;