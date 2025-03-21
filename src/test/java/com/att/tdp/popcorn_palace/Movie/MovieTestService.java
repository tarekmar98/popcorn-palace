package com.att.tdp.popcorn_palace.Movie;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Service class for handling various movie-related operations used in testing.
 * This class interacts with the MockMvc framework to simulate HTTP requests
 * and provides utility methods to test operations on movies.
 */
@Service
public class MovieTestService {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    public MovieRepository movieRepository;

    private Map<String, List<Movie>> updatedMovies;
    public List<String> movieTitles;
    public JsonNode moviesData;
    private ObjectMapper objectMapper;

    /**
     * Loads and parses movie test data from a JSON file.
     * <p>
     * Initializes `moviesData`, a list of `movieTitles`,
     * and a map `updatedMovies` containing movie updates.
     * Logs errors if any occur during initialization.
     */
    public MovieTestService() {
        try {
            objectMapper = new ObjectMapper();
            String resourcesRoot = Paths.get("").toAbsolutePath() +
                    "\\src\\test\\java\\com\\att\\tdp\\popcorn_palace\\resources\\";
            File file = new File(resourcesRoot, "DataTest.json");
            JsonNode data = objectMapper.readTree(file);
            moviesData = data.get("movies");
            movieTitles = new ArrayList<>();
            moviesData.fieldNames().forEachRemaining(movieTitles::add);
            updatedMovies = new HashMap<>();
            for (String movieTitle : movieTitles) {
                JsonNode jsonUpdates = moviesData.get(movieTitle).get("updates");
                List<Movie> movies = new ArrayList<>();
                for (int i = 0; i < jsonUpdates.size(); i++) {
                    Movie movie = objectMapper.treeToValue(jsonUpdates.get(i), Movie.class);
                    movies.add(movie);
                }

                updatedMovies.put(movieTitle, movies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a list of all movies by performing a GET request to the "/movies/all" endpoint.
     *
     * @return a list of Movie objects retrieved from the endpoint.
     * @throws Exception if an error occurs during the HTTP request or response processing.
     */
    public List<Movie> getAllMovies() throws Exception {
        MvcResult result = mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<Movie> movies = objectMapper.readValue(jsonResponse, new TypeReference<List<Movie>>() {});
        return movies;
    }

    /**
     * Adds a movie by sending a POST request to the "/movies" endpoint.
     *
     * @param movieTitle the title of the movie to be added. This is used to fetch the movie details from the preloaded test data.
     * @return the result of the HTTP POST request as an MvcResult object.
     * @throws Exception if an error occurs during request creation, execution, or response handling.
     */
    public MvcResult addMovie(String movieTitle) throws Exception {
        Movie movie = objectMapper.treeToValue(moviesData.get(movieTitle).get("content"), Movie.class);
        String movieString = objectMapper.writeValueAsString(movie);

        MvcResult result = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieString))
                .andReturn();

        return result;

    }

    /**
     * Updates the specified movie with the corresponding update information by
     * sending a POST request to the "/movies/update/{movieTitle}" endpoint.
     *
     * @param movieTitle the title of the movie to be updated.
     * @param updateNum the index of the update information in the list of updates for the specified movie.
     * @return the result of the HTTP POST request as an MvcResult object.
     * @throws Exception if an error occurs during request creation, execution, or response handling.
     */
    public MvcResult updateMovie(String movieTitle, int updateNum) throws Exception {
        Movie movie = updatedMovies.get(movieTitle).get(updateNum);
        String movieString = objectMapper.writeValueAsString(movie);

        MvcResult result = mockMvc.perform(post("/movies/update/" + movieTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieString))
                .andReturn();

        return result;

    }

    /**
     * Deletes a movie with the given title from the system.
     *
     * @param movieTitle the title of the movie to be deleted
     * @return the result of the delete operation encapsulated in an MvcResult object
     * @throws Exception if an error occurs during the delete operation
     */
    public MvcResult deleteMovie(String movieTitle) throws Exception {
        MvcResult result = mockMvc.perform(delete("/movies/" + movieTitle))
                .andReturn();

        return result;
    }

    /**
     * Deletes all entities from the movie repository.
     */
    public void deleteAll() {
        movieRepository.deleteAll();
    }
}
