# (1): create database
create database if not exists videodirectordb;

# (2): tables
use videodirectordb;

drop table event;
drop table video;
drop table event_videos;

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
	tilt int not null,
	name varchar(255) not null, 
	rating int,
	primary key(id)
);

create table if not exists event_videos(
	event_id int not null, 
	video_id int not null, 
	primary key(event_id, video_id)
);

# (3): test data
insert into event (id, name) value (111, "Testing Event");
insert into event (id, name) value (222, "Testing Event2");

insert into video values ('', 
	'2014-10-18 18:14:06.0', 
	300, 640,480, 11,41, 
	'VID_20141018_181401.mp4', 
	10);

insert into event_videos values(111, 1);

# SELECT v.id, v.rating, e.event_id FROM videos AS v, event_videos AS e WHERE v.id = e.video_id AND e.id = 111 ORDER BY v.rating;
