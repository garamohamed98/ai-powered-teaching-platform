package com.mohamedgara.ai_teaching_platform.AI.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

@ConfigurationProperties(prefix = "gemini")
public record GeminiProperties(
        @Name("api-key")
        String apiKey,
        @Name("model")
        String model,
        @Name("base-url")
        String baseUrl
) {}
