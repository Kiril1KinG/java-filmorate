drop table if exists director_film, directors, feed, genre, rating, film_genre, "like", friendship, review, review_like, "user", film;

create table "user" (
	user_id int generated by default as identity primary key,
	email varchar(100) not null unique,
	login varchar(100) not null unique,
	name varchar(100) not null,
	birthday date
);

create table genre (
	genre_id int generated by default as identity primary key,
	name varchar(50) not null
);

create table rating (
	rating_id int generated by default as identity primary key,
	name varchar(50)
);

create table film (
	film_id int generated by default as identity primary key,
	name varchar(200) not null,
	description varchar(200),
	release_date date,
	duration bigint check(duration > 1),
	rating_id int not null references rating (rating_id) on delete restrict
);

create table film_genre (
	film_id int not null references film (film_id) on delete cascade,
	genre_id int not null references genre (genre_id) on delete cascade,
	primary key (film_id, genre_id)
);

create table "like" (
	film_id int not null references film (film_id) on delete cascade,
	user_id int not null references "user" (user_id) on delete cascade,
	primary key (film_id, user_id)
);

create table friendship (
	user_id int not null references "user" (user_id) on delete cascade,
	friend_id int not null references "user" (user_id) on delete cascade,
	friendship_status bool not null,
	primary key (user_id, friend_id)

);

create table review (
	review_id int generated by default as identity primary key,
	content varchar not null,
	is_positive bool not null,
	useful int not null,
	user_id int not null references "user" (user_id) on delete cascade,
	film_id int not null references film (film_id) on delete cascade,
	unique(user_id, film_id)
);

create table review_like (
	review_id int not null references review (review_id) on delete cascade,
	user_id int not null references "user" (user_id) on delete cascade,
	is_positive bool not null,
	primary key (review_id, user_id)

);

create table feed (
    event_id int generated by default as identity primary key,
    event_time timestamp not null,
    user_id int not null references "user" (user_id) on delete cascade,
    event_type varchar(20) not null,
    operation varchar(20) not null,
    entity_id int not null
);

create table directors (
    id   int generated by default as identity primary key,
    name varchar(100) not null
);

create table director_film (
    film_id int not null references film (film_id) on delete cascade,
    director_id int not null references directors (id) on delete restrict,
    primary key (film_id, director_id)
);

