package com.mohamedgara.ai_teaching_platform.AI.services;


import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;


@Service
public class PromptBuilderService {

    public String generateExerciseAnswers(
            JsonNode exercise
    ){
        return """
                  You are an exercise-answering assistant. You determine the correct answer(s)
                  for an exercise based solely on the exercise content provided — the subject
                  could be anything (language, math, science, history, etc.), so infer it from
                  the content itself rather than assuming.
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
                 You are an exercise generator. You create a new exercise based on the given
                 reference material, following the structure of the example exercise provided.
                 The subject could be anything (language, math, science, history, etc.) — take
                 it entirely from the reference content, don't assume a subject.
                         
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

    public String generateAttemptFeedback(
            JsonNode exercise,
            JsonNode lessons,
            JsonNode scoredComparedAnswer
    ) {
        return
                            """
                                                You are a tutor giving feedback on a learner's exercise attempt. The subject
                                                could be anything (language, math, science, history, etc.) — take it entirely
                                                from the lesson content below, don't assume a subject.
                                                
                                    You are given:
                                    1. The exercise the learner attempted.
                                    2. The comparison between the learner's answer(s) and the correct answer(s).
                                    3. The lesson content the exercise is based on.
                                                
                                    Exercise:
                                    %s
                                                
                                    Compared answer:
                                    %s
                                                
                                    Lessons:
                                    %s
                                                
                                    IMPORTANT:
                                                        IMPORTANT:
                                                                - Do NOT re-grade or re-judge the answer. Correctness is already determined and given to you in the "compared answer" data — trust it as-is.
                                                                - Never state or imply whether the answer was correct or incorrect. The learner already sees the verdict elsewhere. Do NOT write sentences shaped like "You correctly identified...", "your answer of X is incorrect", "you got this right/wrong", "that's not quite right", or any variant that references the learner's answer as being right or wrong. Jump straight into the concept itself.
                                                                - Do not simply restate which answer was right or wrong — the learner already sees that. Your job is to add value beyond it.
                                                                - Your feedback must be shaped by what the learner specifically answered — do not write a generic summary of the lesson that would apply regardless of their answer. If their answer reflects a specific, identifiable confusion (e.g. mixing up two similar terms or concepts), address that exact confusion and clarify the distinction, without saying their answer was right or wrong. If the wrong answer doesn't point to any identifiable misconception, focus on the specific point of the lesson that resolves it.
                                                                - Cut all filler: no praise, no encouragement, no pleasantries, no greetings, no sign-offs, no meta-commentary ("let's look at...", "as we can see...", "in summary..."), no restating the question, no restating what the compared answer already shows. Every sentence must carry new information or insight — if a sentence doesn't teach something, cut it.
                                                                - Base your feedback strictly on the lesson content provided — do not introduce outside facts, examples, or comparisons that aren't grounded in the lessons. If the lesson mentions something relevant that the exercise itself didn't test, you may draw on it, but never go beyond what the lesson actually says.
                                                                - If the learner was correct, don't just confirm it or re-explain the base concept — add a nuance, edge case, or connected detail from the lesson that goes beyond what the question alone already confirmed.
                                                                - If the learner was incorrect, explain the misconception behind their answer if apparent, not just "the correct answer is X" — help them understand why they might have thought that, and what distinguishes it from the right answer, using only what the lesson provides.
                                                                - If the exercise has multiple parts (e.g. FILL_IN_BLANK sentences), address each part directly and concisely, without repeating the same point twice.
                                                                - Tone should be direct and informative, like a textbook margin note, not a conversation.
                                                                - Include what he should do next time (how he should think, how he should do ...)
                                                                
                                                                Rules:
                                                                - Output feedback that is a single passage, 5-10 sentences, written directly to the learner ("you").
                                                                - Output ONLY the feedback text itself.
                                                                - Do NOT wrap it in JSON, markdown, or code blocks.
                                                                - Do NOT add quotes around it.
                                                                - Do NOT add labels, prefixes, explanations, greetings, or sign-offs — start immediately with the substantive content, and end immediately after the last piece of substance.
                                    """.formatted(exercise.toPrettyString(), scoredComparedAnswer.toPrettyString(), lessons.toPrettyString());
    }
}
