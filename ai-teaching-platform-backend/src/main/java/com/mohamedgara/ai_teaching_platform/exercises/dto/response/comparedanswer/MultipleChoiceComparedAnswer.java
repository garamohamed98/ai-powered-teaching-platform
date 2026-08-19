package com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MultipleChoiceComparedAnswer(
        String question,
        List<String> options,
        @JsonProperty("correct_answers")
        String correctAnswer,
        @JsonProperty("submitted_answer")
        String submittedAnswer,
        @JsonProperty("is_correct")
        boolean isCorrect
) implements ComparedAnswer{
}
