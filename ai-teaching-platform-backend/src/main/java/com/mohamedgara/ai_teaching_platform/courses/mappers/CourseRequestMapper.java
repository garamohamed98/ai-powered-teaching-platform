package com.mohamedgara.ai_teaching_platform.courses.mappers;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.CourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface CourseRequestMapper {
    Course toCourse(CourseRequest courseRequest);
}
