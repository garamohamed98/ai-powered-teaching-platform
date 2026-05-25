package com.mohamedgara.ai_teaching_platform.exercises.dto.request.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceContent.class, name = "MULTIPLE_CHOICE"),
        @JsonSubTypes.Type(value = FillInBlankContent.class, name="FILL_IN_BLANK")
})
public sealed interface ExerciseContent
    permits MultipleChoiceContent, FillInBlankContent
{}
