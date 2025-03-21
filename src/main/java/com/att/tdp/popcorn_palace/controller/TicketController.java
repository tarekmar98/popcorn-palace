package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Ticket;
import com.att.tdp.popcorn_palace.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller class responsible for handling API requests related to ticket booking.
 * This class provides an endpoint for booking tickets by interacting with the TicketService.
 */
@RestController
public class TicketController {
    @Autowired
    private TicketService ticketService;

    /**
     * Endpoint for booking a ticket. This method handles the HTTP POST request to create
     * a new ticket booking based on the provided ticket details. It interacts with the
     * TicketService to validate, book, and save the ticket, and returns the booking response.
     *
     * @param ticket the Ticket object containing the details of the booking, including showtime ID, seat number, and user ID
     * @return a ResponseEntity containing a map with the booking ID if the booking is successful, and a 201 CREATED status code
     */
    @PostMapping("/bookings")
    public ResponseEntity<?> bookTicket(@RequestBody Ticket ticket) {
        Map<String, String> response = ticketService.bookTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
