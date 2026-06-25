package com.mohamedgara.ai_teaching_platform.AI.services;


import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;


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
            - Each "answer" value must be a string or an array of strings with the correct word
            - Each ""answers" value must be an array of strings with the correct words
            - Do NOT change any other field
            - Do NOT wrap the response in markdown or code blocks
            - Do NOT add any explanation or extra text
            - Pay special attention to arrays/collections and singular vs plural field names if it's plural it mostly should be an array.
            """.formatted(exercise.toPrettyString());
    }
}
