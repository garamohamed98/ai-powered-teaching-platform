package com.mohamedgara.ai_teaching_platform.exercises.domain;

import java.util.List;

public record MultipleChoiceContent(
        String question,
        List<String> options,
        String correctAnswer
) implements ExerciseContent{}
