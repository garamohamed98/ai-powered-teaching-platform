package com.mohamedgara.ai_teaching_platform.AI.client;

import com.mohamedgara.ai_teaching_platform.AI.config.GeminiProperties;
import com.mohamedgara.ai_teaching_platform.AI.dto.GeminiRequest;
import com.mohamedgara.ai_teaching_platform.AI.exceptions.AIRateLimitException;
import com.mohamedgara.ai_teaching_platform.AI.exceptions.AiQuotaExceededException;
import com.mohamedgara.ai_teaching_platform.AI.exceptions.AiRequestException;
import com.mohamedgara.ai_teaching_platform.AI.exceptions.AiServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpStatusCode;

@Component
@RequiredArgsConstructor
public class GeminiClient {
    private final GeminiProperties properties;
    private final RestClient restClient = RestClient.create();

    public String generateContent(String prompt){

        GeminiRequest request =
                GeminiRequest.fromPrompt(prompt);

        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(properties.baseUrl())
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", "{apiKey}")
                        .build(properties.model(), properties.apiKey())
                )
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 429,
                        (req, res) -> { throw new AIRateLimitException(); })
                .onStatus(status -> status.value() == 503,
                        (req, res) -> { throw new AiServiceUnavailableException(); })
                .onStatus(status -> status.value() == 402,
                        (req, res) -> { throw new AiQuotaExceededException(); })
                .onStatus(HttpStatusCode::is4xxClientError,
                        (req, res) -> { throw new AiRequestException(); })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> { throw new AiServiceUnavailableException(); })
                .body(String.class);
    }
}
