package com.mohamedgara.ai_teaching_platform.AI.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record GeneratedExercise(
        String title,
        String instruction,
        JsonNode exercise
) {
}
