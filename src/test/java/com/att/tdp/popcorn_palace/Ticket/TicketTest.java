package com.att.tdp.popcorn_palace.Ticket;

import com.att.tdp.popcorn_palace.entity.Ticket;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class TicketTest {

    @Autowired
    private TicketTestService ticketTestService;

    private ObjectMapper objectMapper;
    private Ticket ticket0;
    private Ticket ticket1;
    @Autowired
    private ShowtimeService showtimeService;

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

    @Test
    public void deleteAllTicketRepository() throws Exception {
        ticketTestService.deleteAllTicketRepository();
        assertThat(ticketTestService.ticketRepository.findAll().isEmpty(), is(true));
    }

    @Test
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

    @Test
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
    }

}
