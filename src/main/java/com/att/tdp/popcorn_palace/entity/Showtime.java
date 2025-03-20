package com.att.tdp.popcorn_palace.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long movieId;
    private String theater;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Double price;

    public Showtime() {}

    public Showtime(Long movieId, String theater, OffsetDateTime startTime, OffsetDateTime endTime, Double price) {
        this.movieId = movieId;
        this.theater = theater;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    public String validate(List<Showtime> showtimesSameTheater) {
        if (movieId == null) {
            return "Showtime movieId cannot be empty";
        }

        if (theater == null || theater.isEmpty()) {
            return "Showtime theater cannot be empty";
        }

        if (startTime == null) {
            return "Showtime startTime cannot be empty";
        }

        if (endTime == null) {
            return "Showtime endTime cannot be empty";
        }

        if (price == null) {
            return "Showtime price cannot be empty";
        }

        if (price < 0) {
            return "Showtime price cannot be negative";
        }

        return null;
    }

    @Override
    public String toString() {
        return "Showtime [id=" + id + ", movieId=" + movieId + ", theater=" + theater + ", startTime=" + startTime
                + ", endTime=" + endTime;
    }
}
