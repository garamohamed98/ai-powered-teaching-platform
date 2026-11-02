package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseCategoryResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseCategory;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseCategoryResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseCategoryService {
    private final CourseCategoryRepository courseCategoryRepository;
    private final CourseCategoryResponseMapper courseCategoryResponseMapper;

    public List<CourseCategoryResponse> getCourseCategoryList() {
        List<CourseCategory> courseCategoryList = courseCategoryRepository.findAll();
        return courseCategoryResponseMapper.toCourseCategoryResponseList(courseCategoryList);
    }

}
