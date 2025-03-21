package com.att.tdp.popcorn_palace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonLoader {
    public static JsonNode read(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File jsonFile = new File(filePath);
            return objectMapper.readTree(jsonFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}