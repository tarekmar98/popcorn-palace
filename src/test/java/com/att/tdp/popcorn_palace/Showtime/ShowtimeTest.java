package com.att.tdp.popcorn_palace.Showtime;

import com.att.tdp.popcorn_palace.entity.Showtime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
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
public class ShowtimeTest {

    @Autowired
    private ShowtimeTestService showtimeTestService;

    private ObjectMapper objectMapper;
    private Showtime showtime0;
    private Showtime showtime1;

    @BeforeEach
    public void init() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        showtimeTestService.deleteAll();
        try {
            MvcResult result = showtimeTestService.addShowtime(0);
            assertEquals(201, result.getResponse().getStatus());
            showtime0 = objectMapper.readValue(result.getResponse().getContentAsString(), Showtime.class);
            showtime0.setMovieId(showtimeTestService.movie.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MvcResult result = showtimeTestService.addShowtime(1);
            assertEquals(201, result.getResponse().getStatus());
            showtime1 = objectMapper.readValue(result.getResponse().getContentAsString(), Showtime.class);
            showtime1.setMovieId(showtimeTestService.movie.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @AfterEach
    public void cleanUp() throws Exception {
        for (Long showtimeId : showtimeTestService.showtimesId) {
            showtimeTestService.deleteShowtime(showtimeId);
        }
    }

    @Test
    public void deleteAll() throws Exception {
        showtimeTestService.deleteAll();
        assertThat(showtimeTestService.showtimeRepository.findAll().isEmpty(), is(true));
    }

    @Test
    public void addShowtimeFlow() throws Exception {
        Showtime showtime0 = objectMapper.treeToValue(showtimeTestService.showtimesData.get(0).get("content"), Showtime.class);
        Showtime showtimeResponse = showtimeTestService.getShowtimeById(showtime0.getId());
        assertEquals(showtime0.toString(), showtimeResponse.toString());

        MvcResult response = showtimeTestService.addShowtime(2);
        assertEquals(400, response.getResponse().getStatus());

        response = showtimeTestService.addShowtime(3);
        assertEquals(400, response.getResponse().getStatus());

        List<Showtime> existShowtimes = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Showtime currShowtime = objectMapper.treeToValue(showtimeTestService.showtimesData.get(i).get("content"), Showtime.class);
            existShowtimes.add(currShowtime);
        }

        for (Showtime currShowtime : existShowtimes) {
            Showtime returnedShowtime = showtimeTestService.getShowtimeById(currShowtime.getId());
            assertEquals(currShowtime.toString(), returnedShowtime.toString());
        }
    }

    @Test
    public void updateShowtimeFlow() throws Exception {
        MvcResult response = showtimeTestService.updateShowtime(1, 0);
        assertEquals(200, response.getResponse().getStatus());
        Showtime showtimeResponse = objectMapper.readValue(response.getResponse().getContentAsString(), Showtime.class);
        assertEquals(showtimeTestService.updatedShowtimes.get(1).get(0).toString(), showtimeResponse.toString());

        showtimeResponse = showtimeTestService.getShowtimeById(showtimeResponse.getId());
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(showtimeTestService.updatedShowtimes.get(1).get(0).toString(), showtimeResponse.toString());

        response = showtimeTestService.updateShowtime(0, 0);
        assertEquals(400, response.getResponse().getStatus());

        response = showtimeTestService.updateShowtime(1, 1);
        assertEquals(404, response.getResponse().getStatus());

        response = showtimeTestService.updateShowtime(0, 1);
        assertEquals(400, response.getResponse().getStatus());
    }

    @Test
    public void deleteShowtimeFlow() throws Exception {
        MvcResult response = showtimeTestService.deleteShowtime(showtime0.getId());
        assertEquals(200, response.getResponse().getStatus());

        response = showtimeTestService.getShowtimeByIdMvc(showtime0.getId());
        assertEquals(404, response.getResponse().getStatus());

        response = showtimeTestService.updateShowtime(0, 0);
        assertEquals(404, response.getResponse().getStatus());

        response = showtimeTestService.addShowtime(0);
        assertEquals(201, response.getResponse().getStatus());
        Showtime showtimeResponse = objectMapper.readValue(response.getResponse().getContentAsString(), Showtime.class);
        showtime0.setId(showtimeResponse.getId());
        assertEquals(showtime0.toString(), showtimeResponse.toString());

        response = showtimeTestService.deleteShowtime(showtime0.getId());
        assertEquals(200, response.getResponse().getStatus());

        response = showtimeTestService.deleteShowtime(Long.valueOf("99999999"));
        assertEquals(404, response.getResponse().getStatus());
    }
}
