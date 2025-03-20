package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Ticket;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ShowtimeService showtimeService;

    public Map<String, String> bookTicket(Ticket ticket) {
        System.out.println("1"+ticket);
        String validation = ticket.validate();
        System.out.println(validation);
        if (validation != null) {
            throw new ResourceNotFoundException(validation);
        }
        System.out.println("2"+ticket);

        if (!showtimeService.isShowtimeExist(ticket.getShowtimeId())) {
            throw new IllegalArgumentException("Showtime not found with id - " + ticket.getShowtimeId());
        }
        System.out.println("3"+ticket);

        if (!seatIsEmpty(ticket.getShowtimeId(), ticket.getSeatNumber())) {
            throw new IllegalArgumentException("Seat is not empty");
        }
        System.out.println("4"+ticket);

        ticketRepository.save(ticket);
        Map<String, String> response = new HashMap<>();
        response.put("bookingId", ticket.getBookingId());
        return response;

    }

    public boolean seatIsEmpty(Long showtimeId, Integer seatNumber) {
        Ticket oldTicket = ticketRepository.getTicketsByShowtimeSeatNumber(showtimeId, seatNumber);
        return oldTicket == null;
    }

}
