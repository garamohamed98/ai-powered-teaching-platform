package com.mohamedgara.ai_teaching_platform.courses.mappers;

import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseCategoryResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseCategory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseCategoryResponseMapper {
    CourseCategoryResponse toCourseCategoryResponse(CourseCategory courseCategory);
    List<CourseCategoryResponse> toCourseCategoryResponseList(List<CourseCategory> courseCategoryList);
}
