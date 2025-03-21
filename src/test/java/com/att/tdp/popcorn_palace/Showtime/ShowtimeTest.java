package com.att.tdp.popcorn_palace.Showtime;

import com.att.tdp.popcorn_palace.entity.Showtime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShowtimeTest {

    @Autowired
    private ShowtimeTestService showtimeTestService;

    private ObjectMapper objectMapper;
    private Showtime showtime0;
    private Showtime showtime1;

    /**
     * Initializes the test context by performing the following actions:
     * 1. Deletes all records from the associated showtime repository to ensure a clean test environment.
     * 2. Adds two predefined showtime instances to the system, and validates
     *    their successful creation by checking the response status.
     * 3. Updates the `movieId` field for the test showtime instances using the ID of the associated movie.
     *
     * @throws Exception if any error occurs during the setup process, including ObjectMapper configuration,
     *                   data deletion, or showtime creation.
     */
    @BeforeEach
    public void init() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        showtimeTestService.deleteAll();
        try {
            MvcResult result = showtimeTestService.addShowtime(0);
            assertEquals(201, result.getResponse().getStatus());
            showtime0 = objectMapper.readValue(result.getResponse().getContentAsString(), Showtime.class);
            showtime0.setMovieId(showtimeTestService.movie.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MvcResult result = showtimeTestService.addShowtime(1);
            assertEquals(201, result.getResponse().getStatus());
            showtime1 = objectMapper.readValue(result.getResponse().getContentAsString(), Showtime.class);
            showtime1.setMovieId(showtimeTestService.movie.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Cleans up the test environment after each test execution.
     * This method iterates through the list of showtime IDs to delete the associated showtime.
     *
     * @throws Exception if any error occurs during the cleanup process, particularly when calling the `deleteShowtime` method.
     */
    @AfterEach
    public void cleanUp() throws Exception {
        for (Long showtimeId : showtimeTestService.showtimesId) {
            showtimeTestService.deleteShowtime(showtimeId);
        }
    }

    /**
     * Tests the deletion of all records in the showtime repository, then ensures that all records are
     * successfully removed from the showtime repository.
     */
    @Test
    @Order(1)
    public void deleteAll() {
        showtimeTestService.deleteAll();
        assertThat(showtimeTestService.showtimeRepository.findAll().isEmpty(), is(true));
    }

    /**
     * Tests the process of adding showtime records to the system and validating their presence.
     *
     * This method performs the following actions:
     * 1. Retrieves a predefined showtime from the test data and validates that it exists in the system
     *    by comparing its content with the response of the `getShowtimeById` method.
     * 2. Attempts to add showtimes using invalid indices from the preloaded data, expecting the server
     *    to return a 400 status code for each attempt.
     * 3. Retrieves a list of existing showtimes from preloaded test data and validates that each of them
     *    matches the corresponding record retrieved from the system based on their unique identifiers.
     *
     * @throws Exception if an error occurs during showtime retrieval, data comparison, or the addition of new showtimes
     */
    @Test
    @Order(2)
    public void addShowtimeFlow() throws Exception {
        Showtime showtime0 = objectMapper.treeToValue(showtimeTestService.showtimesData.get(0).get("content"), Showtime.class);
        Showtime showtimeResponse = showtimeTestService.getShowtimeById(showtime0.getId());
        assertEquals(showtime0.toString(), showtimeResponse.toString());

        MvcResult response = showtimeTestService.addShowtime(2);
        assertEquals(400, response.getResponse().getStatus());

        response = showtimeTestService.addShowtime(3);
        assertEquals(400, response.getResponse().getStatus());

        List<Showtime> existShowtimes = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Showtime currShowtime = objectMapper.treeToValue(showtimeTestService.showtimesData.get(i).get("content"), Showtime.class);
            existShowtimes.add(currShowtime);
        }

        for (Showtime currShowtime : existShowtimes) {
            Showtime returnedShowtime = showtimeTestService.getShowtimeById(currShowtime.getId());
            assertEquals(currShowtime.toString(), returnedShowtime.toString());
        }
    }

    /**
     * Tests the process of updating existing showtime records in the system and validating the responses.
     *
     * This test performs several actions:
     * 1. Successfully updates a predefined showtime record and validates the response status
     *    as well as the content of the updated showtime using direct retrieval from the system.
     * 2. Tests invalid update operations by providing incorrect or inconsistent indices,
     *    ensuring the server responds with appropriate HTTP status codes (400 for bad requests,
     *    and 404 when the entity is not found).
     *
     * @throws Exception if any error occurs during the update process or while validating the results
     */
    @Test
    @Order(3)
    public void updateShowtimeFlow() throws Exception {
        MvcResult response = showtimeTestService.updateShowtime(1, 0);
        assertEquals(200, response.getResponse().getStatus());
        Showtime showtimeResponse = objectMapper.readValue(response.getResponse().getContentAsString(), Showtime.class);
        assertEquals(showtimeTestService.updatedShowtimes.get(1).get(0).toString(), showtimeResponse.toString());

        showtimeResponse = showtimeTestService.getShowtimeById(showtimeResponse.getId());
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(showtimeTestService.updatedShowtimes.get(1).get(0).toString(), showtimeResponse.toString());

        response = showtimeTestService.updateShowtime(0, 0);
        assertEquals(400, response.getResponse().getStatus());

        response = showtimeTestService.updateShowtime(1, 1);
        assertEquals(404, response.getResponse().getStatus());

        response = showtimeTestService.updateShowtime(0, 1);
        assertEquals(400, response.getResponse().getStatus());
    }

    /**
     * Tests the flow of deleting, adding, retrieving, and updating showtime records using the showtime service.
     *
     * This test performs the following steps:
     * 1. Deletes a predefined showtime record by its ID and validates the operation's success.
     *    Ensures the response status is 200 (OK).
     * 2. Attempts to retrieve the deleted showtime by its ID, expecting a 404 (Not Found) response.
     * 3. Attempts to update a nonexistent showtime, expecting a 404 (Not Found) response.
     * 4. Adds a showtime with a predefined configuration, validates its creation by checking the response
     *    status and comparing the created showtime's properties with the expected values.
     * 5. Deletes the newly added showtime and validates the operation's success, ensuring the response status is 200 (OK).
     * 6. Attempts to delete a showtime with a non-existent ID, expecting a 404 (Not Found) response.
     *
     * @throws Exception if any error occurs during the execution of the tests, such as issues with deletion,
     *                   retrieval, addition, or updating operations.
     */
    @Test
    @Order(4)
    public void deleteShowtimeFlow() throws Exception {
        MvcResult response = showtimeTestService.deleteShowtime(showtime0.getId());
        assertEquals(200, response.getResponse().getStatus());

        response = showtimeTestService.getShowtimeByIdMvc(showtime0.getId());
        assertEquals(404, response.getResponse().getStatus());

        response = showtimeTestService.updateShowtime(0, 0);
        assertEquals(404, response.getResponse().getStatus());

        response = showtimeTestService.addShowtime(0);
        assertEquals(201, response.getResponse().getStatus());
        Showtime showtimeResponse = objectMapper.readValue(response.getResponse().getContentAsString(), Showtime.class);
        showtime0.setId(showtimeResponse.getId());
        assertEquals(showtime0.toString(), showtimeResponse.toString());

        response = showtimeTestService.deleteShowtime(showtime0.getId());
        assertEquals(200, response.getResponse().getStatus());

        response = showtimeTestService.deleteShowtime(Long.valueOf("99999999"));
        assertEquals(404, response.getResponse().getStatus());
    }
}
