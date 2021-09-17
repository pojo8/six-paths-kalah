create table game_turn
(
    turn_id     serial not null,
    game_id     integer,
    next_player integer
);