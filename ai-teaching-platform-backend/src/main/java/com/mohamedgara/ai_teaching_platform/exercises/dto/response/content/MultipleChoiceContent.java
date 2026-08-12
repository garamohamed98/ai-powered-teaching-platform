package com.mohamedgara.ai_teaching_platform.exercises.dto.response.content;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MultipleChoiceContent(
        String question,
        List<String> options,
        @JsonProperty("correct_answer")
        String correctAnswer
) implements ExerciseContent {}
