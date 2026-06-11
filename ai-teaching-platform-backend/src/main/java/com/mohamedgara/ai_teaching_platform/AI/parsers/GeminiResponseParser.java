package com.mohamedgara.ai_teaching_platform.AI.parsers;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Component
@RequiredArgsConstructor
public class GeminiResponseParser {
    private final ObjectMapper objectMapper;

    public JsonNode parse(String response) {
            JsonNode root = objectMapper.readTree(response);

            String generatedText = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asString();

            return objectMapper.readTree(generatedText);

    }
}
