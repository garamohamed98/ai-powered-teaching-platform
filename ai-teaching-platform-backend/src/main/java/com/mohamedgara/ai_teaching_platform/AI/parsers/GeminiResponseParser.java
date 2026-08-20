package com.mohamedgara.ai_teaching_platform.AI.parsers;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
@RequiredArgsConstructor
public class GeminiResponseParser {
    private final ObjectMapper objectMapper;

    public String parse(String response ) throws JsonProcessingException{
        JsonNode root = objectMapper.readTree(response);

        return root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();
    }

    public JsonNode parseToJsonNode(String response) throws JsonProcessingException {

            return objectMapper.readTree(parse(response));

    }
}
