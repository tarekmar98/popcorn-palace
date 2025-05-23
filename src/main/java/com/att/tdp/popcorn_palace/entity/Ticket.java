package com.att.tdp.popcorn_palace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Entity class representing a ticket in a cinema booking system.
 * A Ticket associates a specific user with a particular seat and showtime.
 * Each ticket is uniquely identified by a booking ID.
 * The class provides validation logic for its properties.
 */
@Getter
@Setter
@Entity
public class Ticket {
    @Id
    private String bookingId = UUID.randomUUID().toString();

    private Long showtimeId;
    private Integer seatNumber;
    private String userId;

    public Ticket() {}

    public Ticket(Long showtimeId, Integer seatNumber, String userId) {
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.userId = userId;
    }

    public String validate() {
        if (showtimeId == null) {
            return "Ticket showtimeId cannot be empty";
        }

        if (seatNumber == null) {
            return "Ticket seatNumber cannot be empty";
        }

        if (userId == null || userId.isEmpty()) {
            return "Ticket userId cannot be empty";
        }

        return null;
    }

    @Override
    public String toString() {
        return "Ticket [bookingId=" + bookingId + ", showtime=" + showtimeId + ", seatNumber=" + seatNumber
                + ", userId=" + userId + "]";
    }
}