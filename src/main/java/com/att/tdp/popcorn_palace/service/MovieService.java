package com.att.tdp.popcorn_palace.service;


import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getAllMovie() {
        return movieRepository.findAll();
    }

    public Movie saveMovie(Movie movie) {
        if (movie.getId() != null) {
            throw new IllegalArgumentException("Can't assign the id of a new entity");
        }

        if (isMovieExist(movie.getTitle())) {
            throw new IllegalArgumentException("Movie already exists with title - " + movie.getTitle());
        }

        String validation = movie.validate();
        if (validation != null) {
            throw new IllegalArgumentException(validation);
        }

        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    public Movie getMovieByTitle(String title) {
        Movie movie = movieRepository.getMovieByTitle(title);
        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found with title - " + title);
        }
        return movie;
    }

    public Movie updateMovie(String movieTitle, Movie movie) {
        if (movie.getId() != null) {
            throw new IllegalArgumentException("Can't assign the id of a new entity");
        }

        Movie existingMovie = movieRepository.getMovieByTitle(movieTitle);
        if (existingMovie == null) {
            throw new ResourceNotFoundException("Movie not found with title - " + movieTitle);
        }

        if (!Objects.equals(movie.getTitle(), movieTitle) && isMovieExist(movie.getTitle())) {
            throw new IllegalArgumentException("Movie already exists with title - " + movie.getTitle());
        }

        String validation = movie.validate();
        if (validation != null) {
            throw new IllegalArgumentException(validation);
        }

        existingMovie.setTitle(movie.getTitle());
        existingMovie.setGenre(movie.getGenre());
        existingMovie.setDuration(movie.getDuration());
        existingMovie.setRating(movie.getRating());
        existingMovie.setReleaseYear(movie.getReleaseYear());
        return movieRepository.save(existingMovie);
    }

    public boolean isMovieExist(String title) {
        return movieRepository.getMovieByTitle(title) != null;
    }

    public boolean isMovieExistById(Long id) {
        return movieRepository.existsById(id);
    }

}
