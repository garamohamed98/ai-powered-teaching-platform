package com.mohamedgara.ai_teaching_platform.courses.mappers;

import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseResponseMapper {
    @Mapping(target = "category.id", source = "category.id")
    @Mapping(target = "category.name", source = "category.name")
    @Mapping(target = "category.slug", source = "category.slug")
    CourseResponse toCourseResponse(Course course);

    List<CourseResponse> toCourseListResponse(List<Course> courseList);
}
