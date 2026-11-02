package com.mohamedgara.ai_teaching_platform.courses.controller;

import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseCategoryResponse;
import com.mohamedgara.ai_teaching_platform.courses.service.CourseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/course-category")
@RequiredArgsConstructor
public class CourseCategoryController {

    private final CourseCategoryService courseCategoryService;

    @GetMapping
    public ResponseEntity<List<CourseCategoryResponse>> getCategoryList(){
        List<CourseCategoryResponse> courseCategoryResponseList = courseCategoryService.getCourseCategoryList();
        return ResponseEntity.ok(courseCategoryResponseList);
    }
}
