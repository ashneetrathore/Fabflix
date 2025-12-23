CREATE DATABASE IF NOT EXISTS moviedb;
USE moviedb;

CREATE TABLE IF NOT EXISTS movies(
	id VARCHAR(10) PRIMARY KEY DEFAULT '',
	title VARCHAR(100) NOT NULL DEFAULT '',
	year INTEGER NOT NULL,
	director VARCHAR(100) NOT NULL DEFAULT '',
	FULLTEXT(title)
);

CREATE TABLE IF NOT EXISTS stars(
	id VARCHAR(10) PRIMARY KEY DEFAULT '',
	name VARCHAR(100) NOT NULL DEFAULT '',
	birthYear INTEGER
);

CREATE TABLE IF NOT EXISTS stars_in_movies(
	starId VARCHAR(10) NOT NULL DEFAULT '',
	movieId VARCHAR(10) NOT NULL DEFAULT '',
	FOREIGN KEY (starId) REFERENCES stars(id),
	FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS genres(
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS genres_in_movies(
	genreId INTEGER NOT NULL,
    movieId VARCHAR(10) NOT NULL DEFAULT '',
    FOREIGN KEY (genreId) REFERENCES genres(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS creditcards(
	id VARCHAR(20) PRIMARY KEY DEFAULT '',
    firstName VARCHAR(50) NOT NULL DEFAULT '',
    lastName VARCHAR(50) NOT NULL DEFAULT '',
    expiration DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS customers(
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL DEFAULT '',
    lastName VARCHAR(50) NOT NULL DEFAULT '',
    ccId VARCHAR(20) NOT NULL DEFAULT '',
    address VARCHAR(200) NOT NULL DEFAULT '',
    email VARCHAR(50) NOT NULL DEFAULT '',
    password VARCHAR(20) NOT NULL DEFAULT '',
    FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

CREATE TABLE IF NOT EXISTS sales(
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    customerId INTEGER NOT NULL,
    movieId VARCHAR(10) NOT NULL DEFAULT '',
    salesDate DATE NOT NULL,
    FOREIGN KEY (customerId) REFERENCES customers(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS ratings(
	movieId VARCHAR(10) PRIMARY KEY DEFAULT '',
    rating FLOAT NOT NULL,
    numVotes INTEGER NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE INDEX movies_index ON movies(id, title, year, director);
CREATE INDEX stars_index ON stars(id, name, birthYear);
CREATE INDEX sim_index ON stars_in_movies(starId, movieId);
CREATE INDEX genres_index ON genres(id, name);
CREATE INDEX gim_index ON genres_in_movies(genreId, movieId);
CREATE INDEX ratings_index ON ratings(movieId, rating, numVotes);