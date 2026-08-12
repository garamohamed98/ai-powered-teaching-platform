package com.mohamedgara.ai_teaching_platform.courses.mappers;

import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonResponse;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonSummaryResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonResponseMapper {
    @Mapping(target = "courseId", source = "course.id")
    LessonResponse toLessonResponse(Lesson lesson);
    @Mapping(target = "courseId", source = "course.id")
    LessonSummaryResponse toLessonResponseSummary(Lesson lesson);

    List<LessonSummaryResponse> toLessonResponseSummaryList(List<Lesson> lessonList);
}
