package com.att.tdp.popcorn_palace.service;


import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service class responsible for managing movie-related operations.
 * This class provides methods for creating, updating, retrieving,
 * and deleting movies, along with validating movie details and existence checks.
 */
@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    /**
     * Retrieves all movies from the repository.
     *
     * @return a list of all movies.
     */
    public List<Movie> getAllMovie() {
        return movieRepository.findAll();
    }

    /**
     * Persists a new movie entity into the repository after validating its details.
     * This method validates the movie's properties and ensures that there are no
     * existing movies with the same title before saving it.
     *
     * @param movie the Movie object to be saved. It must have a null ID and pass validation checks.
     * @return the saved Movie object with an assigned ID.
     * @throws IllegalArgumentException if the movie has a non-null ID, if a movie with the same title already exists,
     *                                  or if the movie fails validation checks.
     */
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

    /**
     * Deletes a movie from the repository based on its unique ID.
     *
     * @param id the unique identifier of the movie to be deleted
     *           must not be null.
     */
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    /**
     * Retrieves a movie by its title from the repository.
     *
     * @param title the title of the movie to retrieve; must not be null or empty
     * @return the Movie object matching the given title
     * @throws ResourceNotFoundException if no movie is found with the specified title
     */
    public Movie getMovieByTitle(String title) {
        Movie movie = movieRepository.getMovieByTitle(title);
        if (movie == null) {
            throw new ResourceNotFoundException("Movie not found with title - " + title);
        }
        return movie;
    }

    /**
     * Updates an existing movie in the repository identified by its title. Validates the movie details,
     * ensures the title is unique or matches the current title, and saves the changes to the repository.
     *
     * @param movieTitle the original title of the movie to be updated; must not be null or empty
     * @param movie the updated Movie object containing the new details; must have a null ID and pass validation checks
     * @return the updated Movie object saved in the repository
     * @throws IllegalArgumentException if the updated Movie object has a non-null ID, if the title change results in a conflict,
     *                                  or if the Movie object fails validation
     * @throws ResourceNotFoundException if no existing movie is found with the specified title
     */
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

    /**
     * Checks if a movie with the given title exists in the repository.
     *
     * @param title the title of the movie.
     * @return true if a movie with the specified title exists, otherwise false
     */
    public boolean isMovieExist(String title) {
        return movieRepository.getMovieByTitle(title) != null;
    }

    /**
     * Checks if a movie exists in the repository by its ID.
     *
     * @param id the ID of the movie to check
     * @return true if the movie exists, false otherwise
     */
    public boolean isMovieExistById(Long id) {
        return movieRepository.existsById(id);
    }

}
