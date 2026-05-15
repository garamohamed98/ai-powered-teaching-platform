package com.mohamedgara.ai_teaching_platform.courses;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CourseContentUpdateRequest;
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

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable UUID id){
        CourseResponse courseResponse = courseService.getCourse(id);
        return ResponseEntity.ok(courseResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id){
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{courseId}/content")
    public ResponseEntity<CourseResponse> updateCourseContentContent(@PathVariable UUID courseId, @RequestBody CourseContentUpdateRequest courseContentUpdateRequest){
        CourseResponse courseResponse = courseService.updateCourseContent(courseId,courseContentUpdateRequest);
        return ResponseEntity.ok(courseResponse);
    }
}
