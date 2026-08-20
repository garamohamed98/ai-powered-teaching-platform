package com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record FillInBlankSentenceAttempt(
        @JsonProperty("sentence_id")
        UUID sentenceId,
        String answer
) {
}
