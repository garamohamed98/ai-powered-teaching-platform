package com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisestartcontent;

import java.util.List;

public record MultipleChoiceStartContent(
        String question,
        List<String> options
) implements ExerciseStartContent {
}
