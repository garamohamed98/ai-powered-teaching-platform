package com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FillInBlankSentenceComparedAnswer(
        String text,
        List<String> answers,
        @JsonProperty("submitted_answer")
        String submittedAnswer,
        @JsonProperty("is_correct")
        boolean isCorrect
) {
}
