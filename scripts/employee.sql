CREATE DATABASE IF NOT EXISTS moviedb;
USE moviedb;

CREATE TABLE IF NOT EXISTS employees(
	email VARCHAR(50) PRIMARY KEY DEFAULT '',
    password VARCHAR(20) NOT NULL,
    fullname VARCHAR(100)
);

INSERT INTO employees VALUES('classta@email.edu', 'classta', 'TA CS122B');