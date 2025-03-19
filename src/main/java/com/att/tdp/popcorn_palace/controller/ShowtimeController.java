package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {
    @Autowired
    private ShowtimeService showtimeService;

    @GetMapping("/{showtimeId}")
    public ResponseEntity<?> getMovieById(@PathVariable("showtimeId") Long showtimeId) {
        Showtime showtime = showtimeService.getShowtimeById(showtimeId);
        return ResponseEntity.ok(showtime);
    }

    @PostMapping("")
    public ResponseEntity<?> saveMovie(@RequestBody Showtime showtime) {
        Showtime savedShowtime = showtimeService.saveShowtime(showtime);
        return ResponseEntity.status(201).body(savedShowtime);
    }

    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<?> deleteMovie(@PathVariable("showtimeId") Long showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<?> updateMovie(@PathVariable("showtimeId") Long showtimeId, @RequestBody Showtime showtime) {
        Showtime updatedShowtime = showtimeService.updateShowtime(showtimeId, showtime);
        return ResponseEntity.ok(updatedShowtime);
    }
}
