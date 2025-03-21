package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The ShowtimeController class handles HTTP requests related to showtime operations.
 * It provides endpoints for retrieving, saving, deleting, and updating showtime data.
 */
@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {
    @Autowired
    private ShowtimeService showtimeService;

    /**
     * Retrieves a showtime based on the provided showtime ID.
     *
     * @param showtimeId the ID of the showtime to be retrieved
     * @return a ResponseEntity containing the retrieved Showtime object.
     */
    @GetMapping("/{showtimeId}")
    public ResponseEntity<?> getMovieById(@PathVariable("showtimeId") Long showtimeId) {
        Showtime showtime = showtimeService.getShowtimeById(showtimeId);
        return ResponseEntity.ok(showtime);
    }

    /**
     * Saves a new Showtime entity to the system after performing necessary validations.
     *
     * @param showtime the Showtime object to save, containing information such as movie ID, theater, start time,
     *                 end time, and ticket price.
     * @return a ResponseEntity containing the saved Showtime object along with HTTP status 201
     *         if the save operation is successful.
     */
    @PostMapping("")
    public ResponseEntity<?> saveMovie(@RequestBody Showtime showtime) {
        Showtime savedShowtime = showtimeService.saveShowtime(showtime);
        return ResponseEntity.status(201).body(savedShowtime);
    }

    /**
     * Deletes a showtime based on the provided showtime ID.
     *
     * @param showtimeId the ID of the showtime to be deleted
     * @return a ResponseEntity indicating the result of the delete operation
     */
    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<?> deleteMovie(@PathVariable("showtimeId") Long showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates an existing showtime based on the provided showtime ID and
     * Showtime object after performing necessary validations.
     *
     * @param showtimeId the ID of the showtime to be updated
     * @param showtime the Showtime object containing updated information such as movie ID,
     *                 theater, start time, end time, and ticket price
     * @return a ResponseEntity containing the updated Showtime object
     */
    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<?> updateMovie(@PathVariable("showtimeId") Long showtimeId, @RequestBody Showtime showtime) {
        Showtime updatedShowtime = showtimeService.updateShowtime(showtimeId, showtime);
        return ResponseEntity.ok(updatedShowtime);
    }
}
