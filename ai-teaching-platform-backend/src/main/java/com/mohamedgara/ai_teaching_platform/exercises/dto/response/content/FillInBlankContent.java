package com.mohamedgara.ai_teaching_platform.exercises.dto.response.content;


import java.util.List;

public record FillInBlankContent(

        List<FillInBlankSentence> sentences
) implements ExerciseContent {}
