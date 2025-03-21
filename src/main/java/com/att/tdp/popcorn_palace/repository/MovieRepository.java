package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for performing custom database operations related to the Movie entity.
 * This interface extends JpaRepository, providing built-in methods for interacting with the Movie database table.
 */
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE m.title = :title")
    Movie getMovieByTitle(@Param("title") String title);

}
