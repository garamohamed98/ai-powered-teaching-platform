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

    public JsonNode parse(String response) throws JsonProcessingException {
            JsonNode root = objectMapper.readTree(response);

            String generatedText = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return objectMapper.readTree(generatedText);

    }
}
