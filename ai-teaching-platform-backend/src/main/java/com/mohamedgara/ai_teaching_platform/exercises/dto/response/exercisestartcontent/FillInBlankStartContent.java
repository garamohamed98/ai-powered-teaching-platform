package com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisestartcontent;

import java.util.List;

public record FillInBlankStartContent(
        List<FillInBlankStartSentence> sentences
) implements ExerciseStartContent {
}
