USE moviedb;
DROP PROCEDURE IF EXISTS add_movie;

DELIMITER $$

CREATE PROCEDURE add_movie(IN mtitle VARCHAR(100), IN myear INT, IN mdirector VARCHAR(100), IN star_name VARCHAR(100), IN star_birthYear INT, IN genre_name VARCHAR(32), OUT msg VARCHAR(100))
BEGIN
	DECLARE fid VARCHAR(10);
	DECLARE sid VARCHAR(10);
	DECLARE gid VARCHAR(10);
	
	DECLARE max_mid VARCHAR(10);
	DECLARE max_sid VARCHAR(10);
	
	DECLARE part1 VARCHAR(10);
	DECLARE part2 VARCHAR(10);
	DECLARE id_num INT;
	DECLARE new_id_num INT;

	DECLARE exists1 INT;
	DECLARE exists2 INT;
	DECLARE exists3 INT;

	SELECT COUNT(*)
	INTO exists1
	FROM movies
	WHERE title = mtitle AND year = myear AND director = mdirector;

	IF exists1 = 0 THEN
		SELECT MAX(id)
		INTO max_mid
		FROM movies;
		
		SET part1 = REGEXP_REPLACE(max_mid, '[0-9]', '');
		SET part2 = REGEXP_REPLACE(max_mid, '[^0-9]', '');
		SET id_num = CAST(part2 AS UNSIGNED);
		SET new_id_num = id_num + 1;
		SET fid = CONCAT(part1, new_id_num);
		
		INSERT INTO movies(id, title, year, director) VALUES (fid, mtitle, myear, mdirector);
		
		IF star_birthYear IS NOT NULL THEN
			SELECT COUNT(*)
			INTO exists2
			FROM stars
			WHERE name = star_name AND birthYear = star_birthYear;
		ELSE
			SELECT COUNT(*)
			INTO exists2
			FROM stars
			WHERE name = star_name AND birthYear IS NULL;
		END IF;
			
		
		IF exists2 = 0 THEN
			SELECT MAX(id)
			INTO max_sid
			FROM stars;
			
			SET part1 = REGEXP_REPLACE(max_sid, '[0-9]', '');
			SET part2 = REGEXP_REPLACE(max_sid, '[^0-9]', '');
			SET id_num = CAST(part2 AS UNSIGNED);
			SET new_id_num = id_num + 1;
			SET sid = CONCAT(part1, new_id_num);
			
			IF star_birthYear IS NOT NULL THEN
				INSERT INTO stars(id, name, birthYear) VALUES (sid, star_name, star_birthYear);
			ELSE
				INSERT INTO stars(id, name) VALUES (sid, star_name);
			END IF;
		ELSE
			IF star_birthYear IS NOT NULL THEN
				SELECT id
				INTO sid
				FROM stars
				WHERE name = star_name AND birthYear = star_birthYear;
			ELSE
				SELECT id
				INTO sid
				FROM stars
				WHERE name = star_name AND birthYear IS NULL;
			END IF;
		END IF;
		
		INSERT INTO stars_in_movies(starId, movieId) VALUES (sid, fid);
		
		SELECT COUNT(*)
		INTO exists3
		FROM genres
		WHERE name = genre_name;
		
		IF exists3 = 0 THEN
			INSERT INTO genres(name) VALUES (genre_name);
		END IF;
		
		SELECT id
		INTO gid
		FROM genres
		WHERE name = genre_name;
		
		INSERT INTO genres_in_movies(genreId, movieId) VALUES (gid, fid);
		
		SET msg = CONCAT("SUCCESSFULLY ADDED Movie ID: ", fid, " Star ID: ", sid, " Genre ID: ", gid);
	ELSE
		SET msg = "ERROR: Movie already exists";
	END IF;
	
END
$$
DELIMITER ;
