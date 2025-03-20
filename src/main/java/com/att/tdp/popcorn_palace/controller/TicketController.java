package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Ticket;
import com.att.tdp.popcorn_palace.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/bookings")
    public ResponseEntity<?> bookTicket(@RequestBody Ticket ticket) {
        Map<String, String> response = ticketService.bookTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
