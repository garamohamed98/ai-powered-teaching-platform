package com.mohamedgara.ai_teaching_platform.exercises.dto.request.content;

import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record FillInBlankContent(
        @NotEmpty
        @Valid
        List<FillInBlankSentence> sentences
) implements  ExerciseContent{
        @Override
        public ExerciseType getType() { return ExerciseType.FILL_IN_BLANK; }
}
