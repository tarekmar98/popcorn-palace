package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE m.title = :title")
    Movie getMovieByTitle(@Param("title") String title);

}
