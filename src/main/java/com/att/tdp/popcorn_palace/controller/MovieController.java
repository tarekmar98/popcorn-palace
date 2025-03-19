package com.att.tdp.popcorn_palace.controller;
import com.att.tdp.popcorn_palace.entity.*;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllMovies() {
        List<Movie> movies = movieService.getAllMovie();
        return ResponseEntity.ok(movies);
    }

    @PostMapping("")
    public ResponseEntity<?> saveMovie(@RequestBody Movie movie) {
        Movie savedMovie = movieService.saveMovie(movie);
        return ResponseEntity.status(201).body(savedMovie);
    }

    @DeleteMapping("{movieTitle}")
    public ResponseEntity<?> deleteMovie(@PathVariable("movieTitle") String movieTitle) {
        Movie movie = movieService.getMovieByTitle(movieTitle);
        movieService.deleteMovie(movie.getId());
        return ResponseEntity.ok().build();

    }

    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<?> updateMovie(@PathVariable("movieTitle") String movieTitle, @RequestBody Movie movie) {
        Movie updatedMovie = movieService.updateMovie(movieTitle, movie);
        return ResponseEntity.ok(updatedMovie);
    }
}
