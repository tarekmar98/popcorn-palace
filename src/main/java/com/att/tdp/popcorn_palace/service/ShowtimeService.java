package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.exception.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import com.att.tdp.popcorn_palace.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A service class responsible for handling all operations related to showtimes.
 * This includes creating, retrieving, updating, and deleting showtime entries.
 * Additionally, it validates showtime schedules and ensures there are no overlaps
 * within a theater.
 */
@Service
public class ShowtimeService {
    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TicketRepository ticketRepository;

    /**
     * Retrieves a Showtime object based on the provided showtime ID.
     *
     * @param showtimeId the ID of the showtime to be retrieved
     * @return the Showtime object associated with the given ID
     * @throws ResourceNotFoundException if no showtime is found with the provided ID
     */
    public Showtime getShowtimeById(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId).orElse(null);
        if (showtime == null) {
            throw new ResourceNotFoundException("Showtime not found with id - " + showtimeId);
        }

        return showtime;
    }

    /**
     * Saves a new Showtime entity to the repository after performing validations for overlaps,
     * movie existence, and theater-specific scheduling conflicts.
     *
     * @param showtime the Showtime object to save.
     * @return the saved Showtime object if all validations pass.
     * @throws IllegalArgumentException if the Showtime has an existing ID, if its
     *         schedule overlaps with another in the same theater, or if validation checks fail.
     * @throws ResourceNotFoundException if the movie associated with the Showtime does not exist.
     */
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

    /**
     * Deletes a showtime and its associated tickets from the database.
     *
     * @param showtimeId the ID of the showtime to be deleted
     * @throws ResourceNotFoundException if the showtime with the specified ID is not found
     */
    public void deleteShowtime(Long showtimeId) {
        Showtime oldShowtime = showtimeRepository.findById(showtimeId).orElse(null);
        if (oldShowtime == null) {
            throw new ResourceNotFoundException("Showtime not found with id - " + showtimeId);
        }

        ticketRepository.deleteTicketByShowtimeId(showtimeId);
        showtimeRepository.deleteById(showtimeId);
    }

    /**
     * Retrieves a list of showtimes for a specified theater.
     *
     * @param theater the name of the theater for which to retrieve the showtimes
     * @return a list of showtime objects associated with the specified theater
     */
    public List<Showtime> getShowtimeByTheater(String theater) {
        return showtimeRepository.getShowtimeByTheater(theater);
    }

    /**
     * Updates an existing Showtime with the specified details.
     *
     * @param showtimeId The ID of the showtime to update.
     * @param showtime The Showtime object containing updated information.
     *                 Its ID must be null as the method will set it.
     * @return The updated Showtime object after saving it to the repository.
     * @throws IllegalArgumentException If the provided Showtime object contains an ID
     *                                  or its details violate validation rules.
     * @throws ResourceNotFoundException If the Showtime or Movie associated with the
     *                                   provided IDs does not exist.
     */
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

    /**
     * Checks if a showtime exists in the repository based on the given showtime ID.
     *
     * @param showtimeId the unique identifier of the showtime to be checked
     * @return true if a showtime with the given ID exists, otherwise false
     */
    public boolean isShowtimeExist(Long showtimeId) {
        return showtimeRepository.existsById(showtimeId);
    }

    /**
     * Checks if the given showtime overlaps with any showtime in the list of showtimes for the same theater.
     *
     * @param showtime the showtime to check for overlaps
     * @param showtimesSameTheater the list of showtimes in the same theater to compare against
     * @return true if the given showtime overlaps with any showtime in the list, false otherwise
     */
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
