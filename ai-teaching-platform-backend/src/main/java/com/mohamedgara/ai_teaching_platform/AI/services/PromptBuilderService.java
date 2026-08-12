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
            Given the exercise JSON below, provide the correct answer(s).
            
            IMPORTANT:
            
            The exercise can be one of two types:
            1. MULTIPLE_CHOICE
            - Use the existing "correct_answer" field.
            - "correct_answer" MUST be a single string.
            - Do NOT create an "answers" field.
            - Do NOT change the "options" array.
            
            2. FILL_IN_BLANK
            - Each sentence has an existing "answers" field.
            - "answers" MUST be an array of strings.
            - Multiple strings are allowed because more than one answer may be correct.
            - Do NOT create a "correct_answer" field
            
            Exercise:
            %s
            
            Rules:
            - Detect the exercise type from the provided JSON structure.
            - Preserve the exact existing JSON structure.
            - Do NOT add new fields.
            - Do NOT remove fields.
            - Do NOT rename fields.
            - Only fill or update the answer field appropriate for the exercise type.
            - For MULTIPLE_CHOICE, only update "correct_answer".
            - For FILL_IN_BLANK, only update the existing "answers" arrays.
            - Do NOT modify questions, sentences, options, title, instructions, or any other fields.
            - Return ONLY valid JSON.
            - Do NOT wrap the response in markdown or code blocks.
            - Do NOT provide explanations or additional text.
            
            """.formatted(exercise.toPrettyString());
    }

    public String generateExercise(JsonNode reference, JsonNode exerciseExampleReference) {
        return """
                 You are an English language exercise generator.
                         
                 Using this reference lesson content and this example exercise structure, generate a new exercise.
                         
                 Reference:
                 %s
                         
                 Example exercise structure:
                 %s
                         
                 Rules:
                - Return ONLY a single JSON object in exactly this format:
                 {
                   "title": "string",
                   "instruction": "string",
                   "exercise": { ... same structure as the example exercise ... }
                 }
                   "title": a short string, the exercise title
                   "instruction": a string, the instruction shown to the learner
                   "exercise": the exercise JSON itself, following the same structure as the example
                 - Do NOT wrap the response in markdown or code blocks
                 - Do NOT add any explanation or extra text outside the JSON object
                 - Return ONLY valid JSON.
                 - The response MUST contain exactly these top-level fields:
                    "title", "instruction", "exercise".
                 - The "exercise" object MUST contain exactly the same fields as the example.
                 - Do NOT add any fields.
                 - Do NOT remove any fields.
                 - Do NOT rename any fields.
                 - Use "correct_answer" exactly as written.
                 - "correct_answer" MUST be a single string.
                 - "options" MUST be an array of strings.
                 - The response MUST start with { and end with }.
                 - There MUST be no characters before or after the JSON object.
                 - Do NOT add markdown.
                 - Do NOT add ```json.
                 - Do NOT add explanations.
                 - Do NOT add comments.
                 - Do NOT add symbols such as *, #, or backticks.
                 """.formatted(reference.toPrettyString(), exerciseExampleReference.toPrettyString());
    }
}
