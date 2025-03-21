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

    public List<Ticket> getAllTicketRepository() throws Exception {
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets;
    }

    public void deleteAllTicketRepository() throws Exception {
        ticketRepository.deleteAll();
    }
}
