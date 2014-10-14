#(1): database
create database if not exists videodirectordb;

#(2): tables
use videodirectordb;
create table if not exists event (
		id int not null auto_increment, 
		name varchar(255) not null, 
		primary key(id)
);

create table if not exists video(
	id int not null auto_increment,
	finish_time timestamp not null,
	duration int not null,
	width int not null,
	height int not null,
	shaking int not null,
	name varchar(255) not null, 
	primary key(id)
);

create table if not exists event_videos(
	event_id int not null, 
	video_id int not null, 
	primary key(event_id, video_id));


insert into event (id, name) value (111, "Testing Fixture");
