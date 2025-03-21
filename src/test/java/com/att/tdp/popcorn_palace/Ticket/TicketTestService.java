package com.att.tdp.popcorn_palace.Ticket;

import com.att.tdp.popcorn_palace.Showtime.ShowtimeTestService;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.entity.Ticket;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Service class designed to support ticket-related test operations
 * in the application, including initialization, booking, retrieval,
 * and deletion of ticket records.
 */
@Service
public class TicketTestService {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    public ShowtimeTestService showtimeTestService;

    @Autowired
    public TicketRepository ticketRepository;

    private ObjectMapper objectMapper;
    public Showtime showtime0;
    public Showtime showtime1;
    public List<Ticket> tickets;

    public TicketTestService() {}

    /**
     * Initializes test data for movies and showtimes:
     * 1. Reads data from `DataTest.json`.
     * 2. Deletes and re-adds a test movie, verifying the operation.
     * 3. Updates and prepares showtime data linked to the test movie.
     */
    @PostConstruct
    public void init() {
        try {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String resourcesRoot = Paths.get("").toAbsolutePath() +
                    "\\src\\test\\java\\com\\att\\tdp\\popcorn_palace\\resources\\";
            File file = new File(resourcesRoot, "DataTest.json");
            showtimeTestService.deleteAll();
            MvcResult result = showtimeTestService.addShowtime(0);
            assertEquals(201, result.getResponse().getStatus());
            showtime0 = objectMapper.readValue(result.getResponse().getContentAsString(), Showtime.class);
            showtime0.setMovieId(showtimeTestService.movie.getId());
            result = showtimeTestService.addShowtime(1);
            assertEquals(201, result.getResponse().getStatus());
            showtime1 = objectMapper.readValue(result.getResponse().getContentAsString(), Showtime.class);
            showtime1.setMovieId(showtimeTestService.movie.getId());
            JsonNode data = objectMapper.readTree(file);
            JsonNode ticketsData = data.get("tickets");
            tickets = new ArrayList<>();
            for (int i = 0; i < ticketsData.size(); i++) {
                Ticket ticket = objectMapper.treeToValue(ticketsData.get(i).get("content"), Ticket.class);
                if (ticket.getShowtimeId() == 1) {
                    ticket.setShowtimeId(showtime0.getId());
                } else if (ticket.getShowtimeId() == 2) {
                    ticket.setShowtimeId(showtime1.getId());
                } else {
                    ticket.setShowtimeId(Long.valueOf("99999999"));
                }

                tickets.add(ticket);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a POST request to create a booking for the specified ticket number.
     * If the booking is successful, the ticket's booking ID is updated with the
     * ID returned in the response.
     *
     * @param ticketNum the index of the ticket in the tickets list to be booked
     * @return an MvcResult object containing the response of the booking request
     * @throws Exception if an error occurs during the booking process
     */
    public MvcResult setTicket(int ticketNum) throws Exception {
        Ticket ticket = tickets.get(ticketNum);
        String ticketString = objectMapper.writeValueAsString(ticket);
        MvcResult response = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ticketString))
                .andReturn();

        if (response.getResponse().getStatus() == 201) {
            JsonNode jsonResponse = objectMapper.readTree(response.getResponse().getContentAsString());
            ticket.setBookingId(jsonResponse.get("bookingId").asText());
        }

        return response;
    }

    /**
     * Retrieves all ticket records from the ticket repository.
     *
     * @return a list of all tickets stored in the repository
     * @throws Exception if there is an error during the retrieval process
     */
    public List<Ticket> getAllTicketRepository() throws Exception {
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets;
    }

    /**
     * Deletes all ticket records from the ticket repository.
     *
     * @throws Exception if an error occurs during the deletion process
     */
    public void deleteAllTicketRepository() throws Exception {
        ticketRepository.deleteAll();
    }
}
