package com.mohamedgara.ai_teaching_platform.exercises.domain;

import java.util.List;

public record FillInBlankContent(
        List<FillInBlankSentence> sentences
) implements ExerciseContent {
}
