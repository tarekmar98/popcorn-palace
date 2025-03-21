package com.att.tdp.popcorn_palace.Movie;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest
@AutoConfigureMockMvc
public class MovieTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieTestService movieTestService;

    private ObjectMapper objectMapper;
    private Movie currMovie0;
    private Movie currMovie1;

    @BeforeEach
    public void init() {
        try {
            objectMapper = new ObjectMapper();
            MvcResult result = movieTestService.addMovie(movieTestService.movieTitles.get(0));
            currMovie0 = objectMapper.readValue(result.getResponse().getContentAsString(), Movie.class);
            result = movieTestService.addMovie(movieTestService.movieTitles.get(1));
            currMovie1 = objectMapper.readValue(result.getResponse().getContentAsString(), Movie.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void cleanUp() throws Exception {
        for (String movieTitle : movieTestService.movieTitles) {
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

    @Test
    public void updateMovieFlow() throws Exception {
        MvcResult response = movieTestService.updateMovie(movieTestService.movieTitles.get(0), 0);
        response.getResponse().getContentAsString();
        currMovie0 = objectMapper.readValue(response.getResponse().getContentAsString(), Movie.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)).toString(), currMovie0.toString());

        response = movieTestService.updateMovie(movieTestService.movieTitles.get(0), 1);
        assertEquals(400, response.getResponse().getStatus());
        assertEquals(movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)).toString(), currMovie0.toString());

        response = movieTestService.updateMovie(movieTestService.movieTitles.get(1), 0);
        assertEquals(400, response.getResponse().getStatus());
        assertEquals(movieRepository.getMovieByTitle(movieTestService.movieTitles.get(1)).toString(), currMovie1.toString());

        response = movieTestService.updateMovie(movieTestService.movieTitles.get(0), 2);
        assertEquals(400, response.getResponse().getStatus());
        assertEquals(movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)).toString(), currMovie0.toString());
    }

    @Test
    public void deleteMovieFlow() throws Exception {
        MvcResult response = movieTestService.deleteMovie(movieTestService.movieTitles.get(0));
        assertEquals(200, response.getResponse().getStatus());
        assertNull(movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)));

        response = movieTestService.updateMovie(movieTestService.movieTitles.get(0), 0);
        assertEquals(404, response.getResponse().getStatus());
        assertNull(movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)));

        response = movieTestService.addMovie(movieTestService.movieTitles.get(0));
        assertEquals(201, response.getResponse().getStatus());
        Movie movie = objectMapper.readValue(response.getResponse().getContentAsString(), Movie.class);
        assertEquals(movieRepository.getMovieByTitle(movieTestService.movieTitles.get(0)).toString(), movie.toString());

        response = movieTestService.deleteMovie(movieTestService.movieTitles.get(1));
        assertEquals(200, response.getResponse().getStatus());
        assertNull(movieRepository.getMovieByTitle(movieTestService.movieTitles.get(1)));
    }

}
