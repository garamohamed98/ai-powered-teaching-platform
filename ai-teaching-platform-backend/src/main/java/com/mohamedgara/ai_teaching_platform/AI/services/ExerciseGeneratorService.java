package com.mohamedgara.ai_teaching_platform.AI.services;


import com.mohamedgara.ai_teaching_platform.AI.client.GeminiClient;
import com.mohamedgara.ai_teaching_platform.AI.dto.GeneratedExercise;
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
            return geminiResponseParser.parseToJsonNode(aiGenerated);
        }catch (Exception e){
            throw new GeminiParseFailException();
        }

    }
    public GeneratedExercise generateExercise(JsonNode reference, JsonNode exerciseExampleReference){
        String prompt = promptBuilderService.generateExercise(reference, exerciseExampleReference);
        String aiGenerated = geminiClient.generateContent(prompt);
        try{
            JsonNode parsedResponse =  geminiResponseParser.parseToJsonNode(aiGenerated);
            return objectMapper.convertValue(parsedResponse, GeneratedExercise.class);
        }catch (Exception e){
            throw new GeminiParseFailException();
        }

    }

    public String generateAttemptFeedBack(JsonNode exercise, JsonNode lessons, JsonNode scoredComparedAnswer){
        String prompt = promptBuilderService.generateAttemptFeedback(exercise, lessons, scoredComparedAnswer);
        String aiGenerated = geminiClient.generateContent(prompt);
        try {
            return geminiResponseParser.parse(aiGenerated);
        }catch (Exception e){
            throw new GeminiParseFailException();
        }
    }
}
