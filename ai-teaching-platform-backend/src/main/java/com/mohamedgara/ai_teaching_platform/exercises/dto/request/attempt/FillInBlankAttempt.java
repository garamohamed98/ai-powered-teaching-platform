package com.mohamedgara.ai_teaching_platform.exercises.dto.request.attempt;

import java.util.List;

public record FillInBlankAttempt(
        List<FillInBlankSentenceAttempt> sentences
) implements Attempt {
}
