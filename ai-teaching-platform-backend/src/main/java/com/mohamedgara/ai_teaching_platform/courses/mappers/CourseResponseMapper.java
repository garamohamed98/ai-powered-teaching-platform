package com.mohamedgara.ai_teaching_platform.courses.mappers;

import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseResponseMapper {
    CourseResponse toCourseResponse(Course course);
    List<CourseResponse> toCourseListResponse(List<Course> courseList);
}
