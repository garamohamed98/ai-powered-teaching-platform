package com.mohamedgara.ai_teaching_platform.AI.services;


import com.mohamedgara.ai_teaching_platform.AI.client.GeminiClient;
import com.mohamedgara.ai_teaching_platform.AI.exceptions.GeminiParseFailException;
import com.mohamedgara.ai_teaching_platform.AI.parsers.GeminiResponseParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@RequiredArgsConstructor
public class ExerciseGeneratorService {

    private final PromptBuilderService promptBuilderService;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;
    private final GeminiResponseParser geminiResponseParser;

    public JsonNode generateExerciseAnswer(JsonNode exercise){
        String prompt = promptBuilderService.generateExerciseAnswers(exercise);
        String aiGenerated = geminiClient.generateContent(prompt);
        try{
            return geminiResponseParser.parse(aiGenerated);
        }catch (Exception e){
            throw new GeminiParseFailException();
        }

    }
}
