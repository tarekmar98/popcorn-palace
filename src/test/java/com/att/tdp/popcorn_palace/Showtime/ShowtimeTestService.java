package com.att.tdp.popcorn_palace.Showtime;

import com.att.tdp.popcorn_palace.Movie.MovieTestService;
import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Service class to manage and test showtimes functionalities. This class is
 * used to initialize test data and perform CRUD operations for showtimes
 * using MockMvc. It involves integration with the MovieTestService and
 * ShowtimeRepository for dependencies.
 */
@Service
public class ShowtimeTestService {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    public MovieTestService movieTestService;

    @Autowired
    public ShowtimeRepository showtimeRepository;

    public JsonNode showtimesData;
    private ObjectMapper objectMapper;
    public List<List<Showtime>> updatedShowtimes;
    public Movie movie;
    public List<Long> showtimesId;

    public ShowtimeTestService() {}

    /**
     * Initializes test data for movies and showtimes:
     * 1. Deletes and re-adds a test movie, verifying the operation.
     * 2. Updates and prepares showtime data linked to the test movie.
     */
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
            assertEquals(201, result.getResponse().getStatus());
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

    /**
     * Retrieves a Showtime object by its unique identifier.
     *
     * @param id the unique identifier of the showtime to retrieve
     * @return the Showtime object corresponding to the provided id
     * @throws Exception if an error occurs during the HTTP request or response processing
     */
    public Showtime getShowtimeById(Long id) throws Exception {
        MvcResult result = mockMvc.perform(get("/showtimes/" + id.toString()))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Showtime showtime = objectMapper.readValue(jsonResponse, Showtime.class);
        return showtime;
    }

    /**
     * Retrieves a showtime by its ID using the MVC framework.
     *
     * @param id The unique identifier of the showtime to be retrieved.
     * @return The MvcResult containing the response of the request for the specified showtime.
     * @throws Exception if an error occurs while performing the request.
     */
    public MvcResult getShowtimeByIdMvc(Long id) throws Exception {
        MvcResult result = mockMvc.perform(get("/showtimes/" + id.toString()))
                .andReturn();

        return result;
    }

    /**
     * Adds a showtime to the system by making a POST request and returns the result of the operation.
     * The showtime data is retrieved from a preloaded source, modified, and sent in the POST request.
     * Updates the local data structure with the newly assigned ID if the operation is successful.
     *
     * @param showtimeNum the index of the showtime in the preloaded data to be added
     * @return an MvcResult object containing the server's response to the POST request
     * @throws Exception if an error occurs during the execution of the POST request or data processing
     */
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

    /**
     * Updates an existing showtime with new details provided in the request.
     *
     * @param showtimeNum the index of the showtime to be updated in the updatedShowtimes list
     * @param updateNum the index of the updated showtime details within the nested list
     * @return an MvcResult object containing the result of the mock MVC request used to update the showtime
     * @throws Exception if an error occurs while performing the mock MVC request
     */
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

    /**
     * Deletes a showtime with the specified ID.
     *
     * @param id the unique identifier of the showtime to be deleted
     * @return the result of the mock MVC request, encapsulated in an MvcResult object
     * @throws Exception if an error occurs during the deletion process
     */
    public MvcResult deleteShowtime(Long id) throws Exception {
        MvcResult result = mockMvc.perform(delete("/showtimes/" + id.toString()))
                .andReturn();

        return result;
    }

    /**
     * Deletes all records from the associated data repository.
     */
    public void deleteAll() {
        showtimeRepository.deleteAll();
    }
}
