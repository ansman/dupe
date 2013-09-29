drop database dupe;
create database dupe;
use dupe;

create table tasks (
    id int primary key auto_increment,
    create_time datetime default NOW(),
    description varchar(256),
    done boolean default false
);

create table reports (
    id int primary key auto_increment,
    create_time datetime default NOW(),
    finalized boolean default false
);

create table comments (
    id int primary key auto_increment,
    task_id int not null,
    create_time datetime default NOW(),
    comment text
);

create table tasks_in_reports (
    report_id int not null,
    task_id int not null,
    planned boolean,
    primary key (report_id, task_id)
);

create table users (
    id int primary key,
    access_token varchar(256),
    login varchar(32),
    name varchar(64),
    email varchar(32),
    avatar_url varchar(256)
);

create index comments_task_idx on comments (task_id);
