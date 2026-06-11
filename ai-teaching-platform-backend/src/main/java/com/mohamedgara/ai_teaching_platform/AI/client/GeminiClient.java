package com.mohamedgara.ai_teaching_platform.AI.client;

import com.mohamedgara.ai_teaching_platform.AI.config.GeminiProperties;
import com.mohamedgara.ai_teaching_platform.AI.dto.GeminiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class GeminiClient {
    private final GeminiProperties properties;
    private final RestClient restClient = RestClient.create();

    public String generateContent(String prompt){
        String endpoint =
                properties.baseUrl()
                + "/v1beta/models/"
                + properties.model()
                + ":generateContent?key="
                + properties.apiKey();

        GeminiRequest request =
                GeminiRequest.fromPrompt(prompt);

        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("generativelanguage.googleapis.com")
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", "{apiKey}")
                        .build(properties.model(), properties.apiKey())
                )
                .body(request)
                .retrieve()
                .body(String.class);
    }
}
