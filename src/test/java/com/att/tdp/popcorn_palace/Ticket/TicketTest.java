package com.att.tdp.popcorn_palace.Ticket;

import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.entity.Ticket;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TicketTest {

    @Autowired
    private TicketTestService ticketTestService;

    private ObjectMapper objectMapper;
    private Ticket ticket0;
    private Ticket ticket1;
    @Autowired
    private ShowtimeService showtimeService;

    /**
     * Initializes the environment and test data needed for ticket-related test cases.
     * Performs the following operations:
     * 1. Configures the `ObjectMapper` with JavaTimeModule for date-time serialization.
     * 2. Clears all ticket records from the repository.
     * 3. Attempts to set up and book two tickets from the test data.
     *
     * @throws Exception if any error occurs during initialization or ticket booking.
     */
    @BeforeEach
    public void init() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ticketTestService.deleteAllTicketRepository();
        try {
            ticket0 = ticketTestService.tickets.get(0);
            MvcResult result = ticketTestService.setTicket(0);
            assertEquals(201, result.getResponse().getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ticket1 = ticketTestService.tickets.get(1);
            MvcResult result = ticketTestService.setTicket(1);
            assertEquals(201, result.getResponse().getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Tests the functionality of deleting all ticket records from the ticket repository,
     * and verifies that All tickets are deleted from the repository
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    @Order(1)
    public void deleteAllTicketRepository() throws Exception {
        ticketTestService.deleteAllTicketRepository();
        assertThat(ticketTestService.ticketRepository.findAll().isEmpty(), is(true));
    }

    /**
     * Tests the flow of adding tickets through the booking system, ensuring proper status
     * codes are returned and verifying the storage of tickets within the repository.
     *
     * The method performs the following tests:
     * 1. Add tickets and verifies the HTTP response status codes.
     * 2. Retrieves all ticket records from the repository and
     *    checks that only the tickets that were supposed to be added were added.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    @Order(2)
    public void addTicketFlow() throws Exception {
        MvcResult response = ticketTestService.setTicket(2);
        assertEquals(400, response.getResponse().getStatus());

        response = ticketTestService.setTicket(3);
        assertEquals(404, response.getResponse().getStatus());

        response = ticketTestService.setTicket(4);
        assertEquals(400, response.getResponse().getStatus());

        List<Ticket> allTickets = ticketTestService.getAllTicketRepository();
        List<String> allTicketsString = new ArrayList<>();
        for (Ticket ticket : allTickets) {
            allTicketsString.add(ticket.toString());
        }

        for (int i = 0; i < ticketTestService.tickets.size(); i++) {
            if (i < 2) {
                assertTrue(allTicketsString.contains(ticketTestService.tickets.get(i).toString()));
            } else {
                assertFalse(allTicketsString.contains(ticketTestService.tickets.get(i).toString()));
            }
        }

    }

    /**
     * Tests the functionality of deleting a showtime along with its associated tickets.
     *
     * This method performs the following steps:
     * 1. Retrieves all existing tickets from the ticket repository before the deletion operation,
     *    and verifies that specific tickets are present in the repository prior to deletion.
     * 2. Deletes a specified showtime using its ID, which also triggers the deletion of the related tickets.
     * 3. Retrieves all tickets from the repository after the deletion operation, and verifies that
     *    the tickets associated with the deleted showtime are no longer present in the repository.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    @Order(3)
    public void deleteShowtimeWithTickets() throws Exception {
        List<Ticket> ticketsBefore = ticketTestService.getAllTicketRepository();
        List<String> ticketsBeforeString = new ArrayList<>();
        for (Ticket ticket : ticketsBefore) {
            ticketsBeforeString.add(ticket.toString());
        }

        assertTrue(ticketsBeforeString.contains(ticket0.toString()));
        assertTrue(ticketsBeforeString.contains(ticket1.toString()));
        showtimeService.deleteShowtime(ticketTestService.showtime0.getId());

        List<Ticket> ticketsAfter = ticketTestService.getAllTicketRepository();
        List<String> ticketsAfterString = new ArrayList<>();
        for (Ticket ticket : ticketsAfter) {
            ticketsAfterString.add(ticket.toString());
        }

        assertFalse(ticketsAfterString.contains(ticket0.toString()));
        ticketTestService.showtime0.setId(null);
        MvcResult result = ticketTestService.showtimeTestService.addShowtime(0);
        ticketTestService.showtime0 = objectMapper.readValue(result.getResponse().getContentAsString(), Showtime.class);
        ticketTestService.showtime0.setMovieId(ticketTestService.showtimeTestService.movie.getId());
    }

}
