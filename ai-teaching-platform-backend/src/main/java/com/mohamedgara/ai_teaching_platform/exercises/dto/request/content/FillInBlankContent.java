package com.mohamedgara.ai_teaching_platform.exercises.dto.request.content;

import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FillInBlankContent(
        @NotNull
        ExerciseType type,
        @NotEmpty
        @Valid
        List<FillInBlankSentence> sentences
) implements  ExerciseContent{}
