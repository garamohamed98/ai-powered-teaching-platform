package com.mohamedgara.ai_teaching_platform.exercises.dto.response.comparedanswer;

import java.util.List;

public record FillInBlankComparedAnswer (
        List<FillInBlankSentenceComparedAnswer> sentences
) implements ComparedAnswer{
}
