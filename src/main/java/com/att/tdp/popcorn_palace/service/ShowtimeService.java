package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowtimeService {
    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TicketRepository ticketRepository;

    public Showtime getShowtimeById(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId).orElse(null);
        if (showtime == null) {
            throw new ResourceNotFoundException("Showtime not found with id - " + showtimeId);
        }

        return showtime;
    }

    public Showtime saveShowtime(Showtime showtime) {
        if (showtime.getId() != null) {
            throw new IllegalArgumentException("Can't assign the id of a new entity");
        }

        if (!movieService.isMovieExistById(showtime.getMovieId())) {
            throw new ResourceNotFoundException("Movie not found with id - " + showtime.getMovieId());
        }

        List<Showtime> showtimesSameTheater = getShowtimeByTheater(showtime.getTheater());
        String validation = showtime.validate(showtimesSameTheater);
        if (validation != null) {
            throw new IllegalArgumentException(validation);
        }

        return showtimeRepository.save(showtime);
    }

    public void deleteShowtime(Long showtimeId) {
        Showtime oldShowtime = showtimeRepository.findById(showtimeId).orElse(null);
        if (oldShowtime == null) {
            throw new ResourceNotFoundException("Showtime not found with id - " + showtimeId);
        }

        ticketRepository.deleteTicketByShowtimeId(showtimeId);
        showtimeRepository.deleteById(showtimeId);
    }

    public List<Showtime> getShowtimeByTheater(String theater) {
        return showtimeRepository.getShowtimeByTheater(theater);
    }

    public Showtime updateShowtime(Long showtimeId, Showtime showtime) {
        if (showtime.getId() != null) {
            throw new IllegalArgumentException("Can't assign the id of a new entity");
        }

        showtime.setId(showtimeId);
        Showtime oldShowtime = showtimeRepository.findById(showtimeId).orElse(null);
        if (oldShowtime == null) {
            throw new ResourceNotFoundException("Showtime not found with id - " + showtimeId);
        }

        if (showtime.getMovieId() != null && !movieService.isMovieExistById(showtime.getMovieId())) {
            throw new ResourceNotFoundException("Movie not found with id - " + showtime.getMovieId());
        }

        List<Showtime> showtimesSameTheater = getShowtimeByTheater(showtime.getTheater());
        String validation = showtime.validate(showtimesSameTheater);
        if (validation != null) {
            throw new IllegalArgumentException(validation);
        }

        Showtime existingShowtime = getShowtimeById(showtimeId);
        if (showtime.getMovieId() != null) {
            existingShowtime.setMovieId(showtime.getMovieId());
        }

        if (showtime.getTheater() != null) {
            existingShowtime.setTheater(showtime.getTheater());
        }

        if (showtime.getStartTime() != null) {
            existingShowtime.setStartTime(showtime.getStartTime());
        }

        if (showtime.getEndTime() != null) {
            existingShowtime.setEndTime(showtime.getEndTime());
        }

        if (showtime.getPrice() != null) {
            existingShowtime.setPrice(showtime.getPrice());
        }

        return showtimeRepository.save(existingShowtime);
    }

    public boolean isShowtimeExist(Long showtimeId) {
        return showtimeRepository.existsById(showtimeId);
    }

}
