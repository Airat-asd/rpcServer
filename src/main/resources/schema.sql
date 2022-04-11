create table game (
       id varchar(255) not null,
        weapon1 varchar(255),
        weapon2 varchar(255),
        player1_id varchar(255),
        player2_id varchar(255),
        primary key (id)
    );

create table player (
   id varchar(255) not null,
    game_result varchar(255),
    name varchar(255),
    session varchar(255),
    weapon integer,
    game_id varchar(255),
    primary key (id)
    );

alter table game
   add constraint FKuj22waqa0w9ju8c0re84u3pj
   foreign key (player1_id)
   references player;


alter table game
   add constraint FKf6omt0g5ph9r6rd6j2uih24cm
   foreign key (player2_id)
   references player;

alter table player
   add constraint FK8095bt0vv5capccv9870ln2n
   foreign key (game_id)
   references game;