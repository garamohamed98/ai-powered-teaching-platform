package com.mohamedgara.ai_teaching_platform.courses.controller;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonContentUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonTitleUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonResponse;
import com.mohamedgara.ai_teaching_platform.courses.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PatchMapping("/{lessonId}/title")
    public ResponseEntity<LessonResponse> updateLessonTitle(
            @PathVariable UUID lessonId,
            @Valid @RequestBody LessonTitleUpdateRequest lessonTitleUpdateRequest
            ){
        LessonResponse lessonResponse = lessonService.updateLessonTitle(lessonId, lessonTitleUpdateRequest);
        return ResponseEntity.ok(lessonResponse);
    }

    @PatchMapping("/{lessonId}/content")
    public ResponseEntity<LessonResponse> updateLessonContent(
            @PathVariable UUID lessonId,
            @Valid @RequestBody LessonContentUpdateRequest lessonContentUpdateRequest
    ){
        LessonResponse lessonResponse = lessonService.updateLessonContent(lessonId,lessonContentUpdateRequest);
        return ResponseEntity.ok(lessonResponse);
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonResponse> getLesson(
            @PathVariable UUID lessonId
    ){
        LessonResponse lessonResponse = lessonService.getLessonById(lessonId);
        return ResponseEntity.ok(lessonResponse);
    }

}
