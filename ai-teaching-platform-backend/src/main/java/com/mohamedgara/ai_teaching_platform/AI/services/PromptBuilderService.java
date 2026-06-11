package com.mohamedgara.ai_teaching_platform.AI.services;


import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;


@Service
public class PromptBuilderService {

    public String generateExerciseAnswers(
            JsonNode exercise
    ){
        return """
            You are an English language exercise assistant.
            Given this exercise JSON, fill in the correct answers for each sentence's "answers" field.
            
            Exercise:
            %s
            
            Rules:
            - Return ONLY the same JSON structure with the "answers" field filled in
            - Each "answers" value must be a string with the correct word(s)
            - Do NOT change any other field
            - Do NOT wrap the response in markdown or code blocks
            - Do NOT add any explanation or extra text
            """.formatted(exercise.toPrettyString());
    }

}
