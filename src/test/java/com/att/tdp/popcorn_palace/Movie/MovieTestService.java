package com.att.tdp.popcorn_palace.Movie;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class MovieTestService {
    @Autowired
    private MockMvc mockMvc;
    private Map<String, List<Movie>> updatedMovies;
    private List<String> movieTitles;
    private JsonNode moviesData;
    private ObjectMapper objectMapper;
    private String resourcesRoot;

    public MovieTestService() {
        try {
            objectMapper = new ObjectMapper();
            resourcesRoot = Paths.get("").toAbsolutePath() +
                    "\\src\\test\\java\\com\\att\\tdp\\popcorn_palace\\resources\\";
            File file = new File(resourcesRoot, "DataTest.json");
            JsonNode data = objectMapper.readTree(file);
            moviesData = data.get("movies");
            movieTitles = new ArrayList<>();
            moviesData.fieldNames().forEachRemaining(movieTitles::add);
            updatedMovies = new HashMap<>();
            for (String movieTitle : movieTitles) {
                JsonNode jsonUpdates = moviesData.get(movieTitle).get("updates");
                List<Movie> movies = new ArrayList<>();
                for (int i = 0; i < jsonUpdates.size(); i++) {
                    Movie movie = objectMapper.treeToValue(jsonUpdates.get(i), Movie.class);
                    movies.add(movie);
                }

                updatedMovies.put(movieTitle, movies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Movie> getAllMovies() throws Exception {
        MvcResult result = mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<Movie> movies = objectMapper.readValue(jsonResponse, new TypeReference<List<Movie>>() {});
        return movies;
    }

    public MvcResult addMovie(String movieTitle) throws Exception {
        Movie movie = objectMapper.treeToValue(moviesData.get(movieTitle).get("content"), Movie.class);
        String movieString = objectMapper.writeValueAsString(movie);

        MvcResult result = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieString))
                .andReturn();

        return result;

    }

    public MvcResult updateMovie(String movieTitle, int updateNum) throws Exception {
        Movie movie = updatedMovies.get(movieTitle).get(updateNum);
        String movieString = objectMapper.writeValueAsString(movie);

        MvcResult result = mockMvc.perform(post("/movies/update/" + movieTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieString))
                .andReturn();

        return result;

    }

    public MvcResult deleteMovie(String movieTitle) throws Exception {
        MvcResult result = mockMvc.perform(delete("/movies/" + movieTitle))
                .andReturn();

        return result;
    }
}
