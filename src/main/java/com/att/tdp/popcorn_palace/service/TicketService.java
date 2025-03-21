package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Ticket;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service class responsible for handling operations related to the Ticket entity.
 * This class provides methods for booking tickets and checking seat availability.
 */
@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ShowtimeService showtimeService;

    /**
     * Books a ticket for a specific showtime and seat if all validations are met.
     * This method validates the ticket details, ensures the showtime exists,
     * and verifies that the specified seat is available. It then saves the ticket
     * and returns a response containing the booking ID.
     *
     * @param ticket the Ticket object containing the details of the showtime, seat, and user
     * @return a map containing the booking ID associated with the successfully booked ticket
     * @throws IllegalArgumentException if the ticket details are invalid or the seat is not empty
     * @throws ResourceNotFoundException if the specified showtime does not exist
     */
    public Map<String, String> bookTicket(Ticket ticket) {
        System.out.println("1"+ticket);
        String validation = ticket.validate();
        System.out.println(validation);
        if (validation != null) {
            throw new IllegalArgumentException(validation);
        }
        System.out.println("2"+ticket);

        if (!showtimeService.isShowtimeExist(ticket.getShowtimeId())) {
            throw new ResourceNotFoundException("Showtime not found with id - " + ticket.getShowtimeId());
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

    /**
     * Checks if a specific seat in a given showtime is empty or not.
     * Retrieves any existing ticket associated with the specified
     * showtime and seat number and returns true if the seat is not occupied.
     *
     * @param showtimeId the ID of the showtime to check the seat for
     * @param seatNumber the seat number to verify for availability
     * @return true if the seat is empty, false otherwise
     */
    public boolean seatIsEmpty(Long showtimeId, Integer seatNumber) {
        Ticket oldTicket = ticketRepository.getTicketsByShowtimeSeatNumber(showtimeId, seatNumber);
        return oldTicket == null;
    }

}
