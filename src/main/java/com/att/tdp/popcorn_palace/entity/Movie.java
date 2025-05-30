package com.att.tdp.popcorn_palace.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing a movie in a cinema management system.
 * The Movie class contains attributes to define its title, genre, duration, rating, and release year.
 * This class is annotated with JPA annotations to be mapped to a database table.
 * It includes validation logic for its fields.
 */
@Getter
@Setter
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;
    private Double duration;
    private Double rating;
    private Integer releaseYear;

    public Movie() {}

    public Movie(String title, String genre, Double duration, Double rating, Integer releaseYear) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.releaseYear = releaseYear;
    }

    public String validate() {
        if (title == null || title.isEmpty()) {
            return "Movie title cannot be empty";
        }

        if (genre == null || genre.isEmpty()) {
            return "Movie genre cannot be empty";
        }

        if (duration == null) {
            return "Movie duration cannot be empty";
        }

        if (duration < 0) {
            return "Movie duration cannot be negative";
        }

        if (rating == null) {
            return "Movie rating cannot be empty";
        }

        if (rating < 0) {
            return "Movie rating cannot be negative";
        }

        if (releaseYear == null) {
            return "Movie release year cannot be empty";
        }

        return null;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id + ", title=" + title + ", genre=" + genre + ", duration=" + duration + ", rating="
                + rating + ", releaseYear=" + releaseYear + "]";
    }
}
