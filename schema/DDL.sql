
-- 사용자 - 테이블
create table member (
    id               bigint auto_increment,
    name             varchar(50),
    password_hash    varchar(250),
    email            varchar(100),
    nickname         varchar(100),
    role             varchar(50),
    created_at       datetime,
    created_by       varchar(100),
    modified_at      datetime,
    modified_by      varchar(100),
    primary key (id)
);


-- 알람 - 테이블
create table alarm (
    id            bigint auto_increment,
    member_id     bigint,
    message       text,
    target_id     int,
    type          varchar(100),
    alarm_checked int,
    create_time   datetime,
    created_by    varchar(100),
    modified_at   datetime,
    primary key (id),
    foreign key (member_id) references member(id) on delete cascade
);


-- 프로필 - 테이블
create table profile (
    id            bigint auto_increment,
    member_id     bigint,
    bio           varchar(250),
    image_url     text,
    created_at    datetime,
    created_by    varchar(100),
    modified_at   datetime,
    modified_by   varchar(100),
    primary key (id),
    foreign key (member_id) references member(id) on delete cascade
);


-- 친구 - 테이블
create table friend (
    id          bigint auto_increment,
    sender_id   bigint,
    receiver_id bigint,
    created_at  datetime,
    created_by  varchar(100),
    modified_at datetime,
    modified_by varchar(100),
    primary key (id),
    foreign key (sender_id) references member(id) on delete cascade,
    foreign key (receiver_id) references member(id) on delete cascade
);


-- 게시판 - 테이블
create table article (
    id          bigint auto_increment,
    member_id   bigint,
    title       varchar(100),
    content     text,
    created_at  datetime,
    created_by  varchar(100),
    modified_at datetime,
    modified_by varchar(100),
    primary key (id),
    foreign key (member_id) references member(id) on delete cascade
);


-- 게시판 댓글 - 테이블
create table article_comment (
    id          bigint auto_increment,
    article_id  bigint,
    content     text,
    created_at  datetime,
    created_by  varchar(100),
    modified_at datetime,
    modified_by varchar(100),
    primary key (id),
    foreign key (article_id) references article(id) on delete cascade
);


-- 게시판 상태 - 테이블
create table article_statistic (
    id          bigint auto_increment,
    article_id  bigint,
    likes       int,
    views       int,
    created_at  datetime,
    created_by  varchar(100),
    modified_at datetime,
    modified_by varchar(100),
    primary key (id),
    foreign key (article_id) references article(id) on delete cascade
);


-- 게시판 좋아요 - 테이블
create table article_like (
    id        bigint auto_increment not null primary key,
    member_id         bigint        not null,
    article_id        bigint        not null,
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (article_id) REFERENCES article (id)
);


-- 유저 피드 - 테이블
create table member_feed (
    id          bigint auto_increment,
    member_id   bigint,
    title       varchar(100),
    content     text,
    created_at  datetime,
    created_by  varchar(100),
    modified_at datetime,
    modified_by varchar(100),
    primary key (id),
    foreign key (member_id) references member(id) on delete cascade
);


-- 유저 스터디 피드 댓글 - 테이블
create table member_feed_comment (
    id          bigint auto_increment,
    member_feed_id bigint,
    content     text,
    created_at  datetime,
    created_by  varchar(100),
    modified_at datetime,
    modified_by varchar(100),
    primary key (id),
    foreign key (member_feed_id) references member_feed(id) on delete cascade
);


-- 유저 피드 상태 - 테이블

create table member_feed_statistic (
    id             bigint auto_increment,
    member_feed_id bigint,
    likes          int,
    views          int,
    created_at     datetime,
    created_by     varchar(100),
    modified_at    datetime,
    modified_by    varchar(100),
    primary key (id),
    foreign key (member_feed_id) references member_feed(id) on delete cascade
);


-- 스터디 그룹 - 테이블
create table study_group (
    id              bigint auto_increment,
    study_leader_id bigint,
    group_name      varchar(100),
    study_topic     text,
    created_at      datetime,
    created_by      varchar(100),
    modified_at     datetime,
    modified_by     varchar(100),
    primary key (id)
);


-- 스터디 사용자 - 테이블
create table study_member (
    id             bigint auto_increment,
    member_id      bigint,
    study_group_id bigint,
    created_at     datetime,
    created_by     varchar(100),
    modified_at    datetime,
    modified_by    varchar(100),
    primary key (id),
    foreign key (member_id) references member(id) on delete cascade,
    foreign key (study_group_id) references study_group(id) on delete cascade
);


-- 유저 피드 좋아요 - 테이블
create table member_feed_like (
    id        bigint auto_increment not null primary key,
    member_id         bigint        not null,
    member_feed_id    bigint        not null,
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (member_feed_id) REFERENCES member_feed (id)
);