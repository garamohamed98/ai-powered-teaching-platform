package com.mohamedgara.ai_teaching_platform.exercises.dto.response.content;

import java.util.List;

public record MultipleChoiceContent(
        String question,
        List<String> options,
        String correctAnswer
) implements ExerciseContent {}
