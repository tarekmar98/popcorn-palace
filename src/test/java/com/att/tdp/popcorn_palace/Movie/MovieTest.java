package com.att.tdp.popcorn_palace.Movie;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying the functionality of Movie-related operations.
 *
 * This class contains unit tests that ensure the correctness of movie addition,
 * deletion, update, and retrieval. The tests are executed within a Spring Boot test context,
 * using `MockMvc` to simulate HTTP requests and responses.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovieTest {

    @Autowired
    private MovieTestService movieTestService;

    private ObjectMapper objectMapper;
    private Movie currMovie0;
    private Movie currMovie1;

    /**
     * Initializes test data before each test by adding two movies,
     * verifying their HTTP responses, and parsing the responses into Movie objects.
     */
    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();
        movieTestService.deleteAll();
        try {
            MvcResult result = movieTestService.addMovie(movieTestService.movieTitles.get(0));
            assertEquals(201, result.getResponse().getStatus());
            currMovie0 = objectMapper.readValue(result.getResponse().getContentAsString(), Movie.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MvcResult result = movieTestService.addMovie(movieTestService.movieTitles.get(1));
            assertEquals(201, result.getResponse().getStatus());
            currMovie1 = objectMapper.readValue(result.getResponse().getContentAsString(), Movie.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cleans up test data after each test execution by deleting all movies added during the test.
     *
     * This method iterates over the list of movie titles maintained by the `movieTestService` and
     * invokes the `deleteMovie` method for each title, ensuring that the test environment is reset.
     *
     * @throws Exception if an error occurs during the deletion of a movie
     */
    @AfterEach
    public void cleanUp() throws Exception {
        for (String movieTitle : movieTestService.movieTitles) {
            movieTestService.deleteMovie(movieTitle);
        }
    }

    /**
     * Tests the `deleteAll` method of the `movieTestService` to ensure all movie entities
     * are deleted from the movie repository.
     */
    @Test
    @Order(1)
    public void deleteAll() {
        movieTestService.deleteAll();
        assertThat(movieTestService.movieRepository.findAll().isEmpty(), is(true));
    }

    /**
     * Tests the flow of adding movies and verifies the system's behavior when attempting
     * to add movies that already exist or do not exist in the test data.
     *
     * The method performs the following steps:
     * 1. Attempts to add two movies from the test data that are considered invalid for addition.
     *    Verifies that the HTTP status code in both cases is 400 (Bad Request).
     * 2. Retrieves all movies currently in the system.
     * 3. Ensures all expected existing movies have been validated and none are missing,
     *    and That no non-existing movies are present.
     *
     * @throws Exception if an error occurs during the process of adding movies,
     *                   retrieving movies, or performing assertions.
     */
    @Test
    @Order(2)
    public void addMovieFlow() throws Exception {
        MvcResult response = movieTestService.addMovie(movieTestService.movieTitles.get(2));
        assertEquals(400, response.getResponse().getStatus());
        response = movieTestService.addMovie(movieTestService.movieTitles.get(3));
        assertEquals(400, response.getResponse().getStatus());
        List<Movie> movies = movieTestService.getAllMovies();
        Map<String, Movie> existMovies = new HashMap<>();
        Map<String, Movie> notExistMovies = new HashMap<>();
        for (int i = 0; i < movieTestService.movieTitles.size(); i++) {
            Movie currMovie = objectMapper.treeToValue(movieTestService.moviesData.get(movieTestService.movieTitles.get(i)).get("content"), Movie.class);
            if (i < 2) {
                existMovies.put(movieTestService.movieTitles.get(i), currMovie);
            } else {
                notExistMovies.put(movieTestService.movieTitles.get(i), currMovie);
            }
        }

        for (Movie movie : movies) {
            assertFalse(notExistMovies.containsKey(movie.getTitle()));
            if (existMovies.containsKey(movie.getTitle())) {
                assertNotEquals(existMovies.get(movie.getTitle()), movie);
                existMovies.remove(movie.getTitle());
            }

        }

        assertTrue(existMovies.isEmpty());
    }

    /**
     * Tests the flow for updating movies and validates the responses.
     *
     * Verifies the following:
     * 1. Successfully updates a movie and checks the response and database consistency.
     * 2. Attempts invalid updates and ensures the response status is 400,
     *    with the database state remaining unchanged.
     */
    @Test
    @Order(3)
    public void updateMovieFlow() throws Exception {
        MvcResult response = movieTestService.updateMovie(movieTestService.movieTitles.get(0), 0);
        currMovie0 = objectMapper.readValue(response.getResponse().getContentAsString(), Movie.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(movieTestService.movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)).toString(), currMovie0.toString());

        response = movieTestService.updateMovie(movieTestService.movieTitles.get(0), 1);
        assertEquals(400, response.getResponse().getStatus());
        assertEquals(movieTestService.movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)).toString(), currMovie0.toString());

        response = movieTestService.updateMovie(movieTestService.movieTitles.get(1), 0);
        assertEquals(400, response.getResponse().getStatus());
        assertEquals(movieTestService.movieRepository.getMovieByTitle(movieTestService.movieTitles.get(1)).toString(), currMovie1.toString());

        response = movieTestService.updateMovie(movieTestService.movieTitles.get(0), 2);
        assertEquals(400, response.getResponse().getStatus());
        assertEquals(movieTestService.movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)).toString(), currMovie0.toString());
    }

    /**
     * Tests the flow for deleting movies and validates the outcomes.
     *
     * Steps:
     * 1. Deletes an existing movie and verifies the response and removal from the repository.
     * 2. Attempts to update the deleted movie, expecting a 404 response with no repository changes.
     * 3. Re-adds the deleted movie, checks the response, and ensures it is correctly saved in the repository.
     * 4. Deletes another movie and verifies the response and its removal from the repository.
     */
    @Test
    @Order(4)
    public void deleteMovieFlow() throws Exception {
        MvcResult response = movieTestService.deleteMovie(movieTestService.movieTitles.get(0));
        assertEquals(200, response.getResponse().getStatus());
        assertNull(movieTestService.movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)));

        response = movieTestService.updateMovie(movieTestService.movieTitles.get(0), 0);
        assertEquals(404, response.getResponse().getStatus());
        assertNull(movieTestService.movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)));

        response = movieTestService.addMovie(movieTestService.movieTitles.get(0));
        assertEquals(201, response.getResponse().getStatus());
        Movie movie = objectMapper.readValue(response.getResponse().getContentAsString(), Movie.class);
        assertEquals(movieTestService.movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)).toString(), movie.toString());

        response = movieTestService.deleteMovie(movieTestService.movieTitles.get(1));
        assertEquals(200, response.getResponse().getStatus());
        assertNull(movieTestService.movieRepository.getMovieByTitle(movieTestService.movieTitles.get(1)));
    }

}
