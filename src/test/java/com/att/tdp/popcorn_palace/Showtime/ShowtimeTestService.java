package com.att.tdp.popcorn_palace.Showtime;

import com.att.tdp.popcorn_palace.Movie.MovieTestService;
import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Service
public class ShowtimeTestService {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    public MovieTestService movieTestService;

    public JsonNode showtimesData;
    private ObjectMapper objectMapper;
    public List<List<Showtime>> updatedShowtimes;
    public Movie movie;
    public List<Long> showtimesId;

    public ShowtimeTestService() {}

    @PostConstruct
    public void init() {
        try {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            showtimesId = new ArrayList<>();
            String resourcesRoot = Paths.get("").toAbsolutePath() +
                    "\\src\\test\\java\\com\\att\\tdp\\popcorn_palace\\resources\\";
            File file = new File(resourcesRoot, "DataTest.json");
            JsonNode data = objectMapper.readTree(file);
            JsonNode moviesData = data.get("movies");
            List<String> movieTitles = new ArrayList<>();
            moviesData.fieldNames().forEachRemaining(movieTitles::add);
            movieTestService.deleteMovie(movieTitles.get(0));
            MvcResult result = movieTestService.addMovie(movieTitles.get(0));
            movie = objectMapper.readValue(result.getResponse().getContentAsString(), Movie.class);
            showtimesData = data.get("showtimes");
            updatedShowtimes = new ArrayList<>();
            for (int i = 0; i < showtimesData.size(); i++) {
                ObjectNode objectNode = (ObjectNode) showtimesData.get(i).get("content");
                if (objectNode.has("movieId")) {
                    objectNode.put("movieId", movie.getId());
                }

                JsonNode jsonUpdates = showtimesData.get(i).get("updates");
                List<Showtime> showtimes = new ArrayList<>();
                for (int j = 0; j < jsonUpdates.size(); j++) {
                    Showtime currShowtime = objectMapper.treeToValue(jsonUpdates.get(j), Showtime.class);
                    currShowtime.setMovieId(movie.getId());
                    showtimes.add(currShowtime);
                }

                updatedShowtimes.add(showtimes);
            }

            updatedShowtimes.get(1).get(1).setMovieId(Long.valueOf("99999999"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Showtime getShowtimeById(Long id) throws Exception {
        MvcResult result = mockMvc.perform(get("/showtimes/" + id.toString()))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Showtime showtime = objectMapper.readValue(jsonResponse, Showtime.class);
        return showtime;
    }

    public MvcResult getShowtimeByIdMvc(Long id) throws Exception {
        MvcResult result = mockMvc.perform(get("/showtimes/" + id.toString()))
                .andReturn();

        return result;
    }

    public MvcResult addShowtime(int showtimeNum) throws Exception {
        Showtime showtime = objectMapper.treeToValue(showtimesData.get(showtimeNum).get("content"), Showtime.class);
        Long currId = showtime.getId();
        showtime.setId(null);
        String showtimeString = objectMapper.writeValueAsString(showtime);

        MvcResult result = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(showtimeString))
                .andReturn();

        showtime.setId(currId);
        if (result.getResponse().getStatus() == 201) {
            ObjectNode objectNode = (ObjectNode) showtimesData.get(showtimeNum).get("content");
            Showtime returnedShowtime = objectMapper.readValue(result.getResponse().getContentAsString(), Showtime.class);
            objectNode.put("id", returnedShowtime.getId());
            showtimesId.add(returnedShowtime.getId());
            for (int i = 0; i < updatedShowtimes.get(showtimeNum).size(); i++) {
                updatedShowtimes.get(showtimeNum).get(i).setId(returnedShowtime.getId());
            }
        }

        return result;
    }

    public MvcResult updateShowtime(int showtimeNum, int updateNum) throws Exception {
        Showtime showtime = updatedShowtimes.get(showtimeNum).get(updateNum);
        Long currId = showtime.getId();
        showtime.setId(null);
        String showtimeString = objectMapper.writeValueAsString(showtime);

        MvcResult result = mockMvc.perform(post("/showtimes/update/" + currId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(showtimeString))
                .andReturn();

        showtime.setId(currId);
        return result;

    }

    public MvcResult deleteShowtime(Long id) throws Exception {
        MvcResult result = mockMvc.perform(delete("/showtimes/" + id.toString()))
                .andReturn();

        return result;
    }
}
