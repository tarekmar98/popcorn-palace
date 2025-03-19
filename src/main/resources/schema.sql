CREATE TABLE IF NOT EXISTS task (
    description VARCHAR(64) NOT NULL,
    completed   VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS movie (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) UNIQUE,
    genre VARCHAR(255),
    duration DOUBLE PRECISION,
    rating DOUBLE PRECISION,
    release_year INT
);

CREATE TABLE IF NOT EXISTS showtime (
    id SERIAL PRIMARY KEY,
    movieId INT,
    theater VARCHAR(255),
    startTime TIMESTAMPTZ,
    endTime TIMESTAMPTZ,
    price DOUBLE PRECISION,
    CONSTRAINT fk_movie FOREIGN KEY (movieId) REFERENCES movie(id)
);