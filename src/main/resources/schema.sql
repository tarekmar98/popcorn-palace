CREATE TABLE IF NOT EXISTS task (
    description VARCHAR(64) NOT NULL,
    completed   VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS movie (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    genre VARCHAR(255),
    duration DOUBLE PRECISION,
    rating DOUBLE PRECISION,
    release_year INT
);