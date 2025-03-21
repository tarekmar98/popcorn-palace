package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for performing custom database operations related to the Showtime entity.
 * This interface extends JpaRepository, providing built-in methods for interacting with the Showtime database table.
 */
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("SELECT s FROM Showtime s WHERE s.theater = :theater")
    List<Showtime> getShowtimeByTheater(@Param("theater") String theater);

}
