package com.att.tdp.popcorn_palace.controller;
import com.att.tdp.popcorn_palace.entity.*;
import com.att.tdp.popcorn_palace.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing Movie-related operations.
 * This class handles HTTP requests related to movies.
 * It provides endpoints for retrieving, creating, updating, and deleting movie records.
 */
@RestController
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    /**
     * Retrieves all movies from the data source.
     *
     * @return a ResponseEntity containing a list of Movie objects
     *         representing all available movies.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllMovies() {
        List<Movie> movies = movieService.getAllMovie();
        return ResponseEntity.ok(movies);
    }

    /**
     * Saves a new movie to the system by validating its details and persisting it to the data store.
     *
     * @param movie the Movie object containing the details of the movie to be saved
     * @return a ResponseEntity containing the saved Movie object with its assigned ID and a 201 status code
     */
    @PostMapping("")
    public ResponseEntity<?> saveMovie(@RequestBody Movie movie) {
        Movie savedMovie = movieService.saveMovie(movie);
        return ResponseEntity.status(201).body(savedMovie);
    }

    /**
     * Deletes a movie based on its title. The method retrieves the movie by its title,
     * then deletes it from the repository using its unique ID.
     *
     * @param movieTitle the title of the movie to be deleted; must not be null or empty
     * @return a ResponseEntity indicating the success of the deletion operation
     */
    @DeleteMapping("{movieTitle}")
    public ResponseEntity<?> deleteMovie(@PathVariable("movieTitle") String movieTitle) {
        Movie movie = movieService.getMovieByTitle(movieTitle);
        movieService.deleteMovie(movie.getId());
        return ResponseEntity.ok().build();

    }

    /**
     * Updates the details of an existing movie identified by its title.
     *
     * @param movieTitle the title of the movie to be updated; must not be null or empty
     * @param movie the Movie object containing the updated details of the movie
     * @return a ResponseEntity containing the updated Movie object
     */
    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<?> updateMovie(@PathVariable("movieTitle") String movieTitle, @RequestBody Movie movie) {
        Movie updatedMovie = movieService.updateMovie(movieTitle, movie);
        return ResponseEntity.ok(updatedMovie);
    }
}
