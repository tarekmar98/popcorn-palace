package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.entity.Ticket;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for performing custom database operations related to the Ticket entity.
 * This interface extends JpaRepository, providing built-in methods for interacting with the Ticket database table.
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t WHERE t.showtimeId = :showtimeId AND t.seatNumber = :seatNumber")
    Ticket getTicketsByShowtimeSeatNumber(@Param("showtimeId") Long showtimeId, @Param("seatNumber") Integer seatNumber);

    @Modifying
    @Transactional
    @Query("DELETE FROM Ticket t WHERE t.showtimeId = :showtimeId")
    void deleteTicketByShowtimeId(@Param("showtimeId") Long showtimeId);
}
