package com.mohamedgara.ai_teaching_platform.exercises.dto.request.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MultipleChoiceContent(
        @NotBlank
        String question,
        @NotEmpty
        @Size(min = 2, max = 10)
        List<@NotBlank String> options,
        @NotBlank
        @JsonProperty("correct_answer")
        String correctAnswer
) implements ExerciseContent {}
