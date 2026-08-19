package com.mohamedgara.ai_teaching_platform.exercises.domain;

import java.util.List;
import java.util.UUID;

public record FillInBlankSentence(
        UUID id,
        String text,
        List<String> answers
) {
}
