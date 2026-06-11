package com.mohamedgara.ai_teaching_platform.exercises.dto.request.content;

import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;

public sealed interface ExerciseContent
    permits MultipleChoiceContent, FillInBlankContent
{}
