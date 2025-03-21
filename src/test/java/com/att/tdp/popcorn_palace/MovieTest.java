package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.resources.MovieTestService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieTestService movieTestService;

    private JsonNode moviesData;
    private ObjectMapper objectMapper;
    private List<String> movieTitles;
    private Map<String, List<Movie>> updatedMovies;
    private Movie currMovie0;
    private Movie currMovie1;
    private String resourcesRoot;

    @BeforeEach
    public void init() {
        try {
            objectMapper = new ObjectMapper();
            resourcesRoot = Paths.get("").toAbsolutePath() +
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

            MvcResult result = movieTestService.addMovie(movieTitles.get(0));
            currMovie0 = objectMapper.readValue(result.getResponse().getContentAsString(), Movie.class);
            result = movieTestService.addMovie(movieTitles.get(1));
            currMovie1 = objectMapper.readValue(result.getResponse().getContentAsString(), Movie.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void cleanUp() throws Exception {
        for (String movieTitle : movieTitles) {
            movieTestService.deleteMovie(movieTitle);
        }
    }

    @Test
    public void deleteAll() throws Exception {
        movieRepository.deleteAll();
        assertThat(movieRepository.findAll().isEmpty(), is(true));
    }

    @Test
    public void addMovieFlow() throws Exception {
        MvcResult response = movieTestService.addMovie(movieTitles.get(2));
        assertEquals(400, response.getResponse().getStatus());
        response = movieTestService.addMovie(movieTitles.get(3));
        assertEquals(400, response.getResponse().getStatus());
        List<Movie> movies = movieTestService.getAllMovies();
        Map<String, Movie> existMovies = new HashMap<>();
        Map<String, Movie> notExistMovies = new HashMap<>();
        for (int i = 0; i < movieTitles.size(); i++) {
            Movie currMovie = objectMapper.treeToValue(moviesData.get(movieTitles.get(i)).get("content"), Movie.class);
            if (i < 2) {
                existMovies.put(movieTitles.get(i), currMovie);
            } else {
                notExistMovies.put(movieTitles.get(i), currMovie);
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

    @Test
    public void updateMovieFlow() throws Exception {
        MvcResult response = movieTestService.updateMovie(movieTitles.get(0), 0);
        response.getResponse().getContentAsString();
        currMovie0 = objectMapper.readValue(response.getResponse().getContentAsString(), Movie.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(movieRepository.getMovieByTitle(movieTitles.get(0)).toString(), currMovie0.toString());

        response = movieTestService.updateMovie(movieTitles.get(0), 1);
        assertEquals(400, response.getResponse().getStatus());
        assertEquals(movieRepository.getMovieByTitle(movieTitles.get(0)).toString(), currMovie0.toString());

        response = movieTestService.updateMovie(movieTitles.get(1), 0);
        assertEquals(400, response.getResponse().getStatus());
        assertEquals(movieRepository.getMovieByTitle(movieTitles.get(1)).toString(), currMovie1.toString());

        response = movieTestService.updateMovie(movieTitles.get(0), 2);
        assertEquals(400, response.getResponse().getStatus());
        assertEquals(movieRepository.getMovieByTitle(movieTitles.get(0)).toString(), currMovie0.toString());
    }

    @Test
    public void deleteMovieFlow() throws Exception {
        MvcResult response = movieTestService.deleteMovie(movieTitles.get(0));
        assertEquals(200, response.getResponse().getStatus());
        assertNull(movieRepository.getMovieByTitle(movieTitles.get(0)));

        response = movieTestService.updateMovie(movieTitles.get(0), 0);
        assertEquals(404, response.getResponse().getStatus());
        assertNull(movieRepository.getMovieByTitle(movieTitles.get(0)));

        response = movieTestService.addMovie(movieTitles.get(0));
        assertEquals(201, response.getResponse().getStatus());
        Movie movie = objectMapper.readValue(response.getResponse().getContentAsString(), Movie.class);
        assertEquals(movieRepository.getMovieByTitle(movieTitles.get(0)).toString(), movie.toString());

        response = movieTestService.deleteMovie(movieTitles.get(1));
        assertEquals(200, response.getResponse().getStatus());
        assertNull(movieRepository.getMovieByTitle(movieTitles.get(1)));
    }

}
