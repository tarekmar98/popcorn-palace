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

        List<Showtime> showtimesSameTheater = getShowtimeByTheater(showtime.getTheater());
        String validation = showtime.validate(showtimesSameTheater);
        if (validation != null) {
            throw new IllegalArgumentException(validation);
        }

        if (!movieService.isMovieExistById(showtime.getMovieId())) {
            throw new ResourceNotFoundException("Movie not found with id - " + showtime.getMovieId());
        }

        if (checkOverLap(showtime, showtimesSameTheater)) {
            throw new IllegalArgumentException("The showtime overlaps with an existing schedule for the same theater." +
                    " Please select a different time.");
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

        if (checkOverLap(showtime, showtimesSameTheater)) {
            throw new IllegalArgumentException("The showtime overlaps with an existing schedule for the same theater." +
                    " Please select a different time.");
        }

        Showtime existingShowtime = getShowtimeById(showtimeId);
        existingShowtime.setMovieId(showtime.getMovieId());
        existingShowtime.setTheater(showtime.getTheater());
        existingShowtime.setStartTime(showtime.getStartTime());
        existingShowtime.setEndTime(showtime.getEndTime());
        existingShowtime.setPrice(showtime.getPrice());
        return showtimeRepository.save(existingShowtime);
    }

    public boolean isShowtimeExist(Long showtimeId) {
        return showtimeRepository.existsById(showtimeId);
    }

    public boolean checkOverLap(Showtime showtime, List<Showtime> showtimesSameTheater) {
        for (Showtime showtimeIter : showtimesSameTheater) {
            if (!showtimeIter.getId().equals(showtime.getId())
                    && ((showtimeIter.getStartTime().compareTo(showtime.getStartTime()) <= 0
                    && showtime.getStartTime().compareTo(showtimeIter.getEndTime()) <= 0)
                    || (showtimeIter.getStartTime().compareTo(showtime.getEndTime()) <= 0
                    && showtime.getEndTime().compareTo(showtimeIter.getEndTime()) <= 0))) {
                return true;

            }
        }

        return false;
    }

}
