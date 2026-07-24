package com.mohamedgara.ai_teaching_platform.courses.controller;

import com.mohamedgara.ai_teaching_platform.courses.service.CourseService;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.CourseTitleUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateCourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest){
        CourseResponse courseResponse = courseService.createCourse(createCourseRequest);
        URI location  = URI.create("/api/course/"+ courseResponse.id());
        return ResponseEntity.created(location).body(courseResponse);
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getCourseList(){
        List<CourseResponse> courseResponseList = courseService.getCourseList();
        return ResponseEntity.ok(courseResponseList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id){
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{courseId}/title")
    public ResponseEntity<CourseResponse> updateCourseTitle(@PathVariable UUID courseId, @RequestBody CourseTitleUpdateRequest courseTitleUpdateRequest){
        CourseResponse courseResponse = courseService.updateCourseTitle(courseId,courseTitleUpdateRequest);
        return ResponseEntity.ok(courseResponse);
    }
}
