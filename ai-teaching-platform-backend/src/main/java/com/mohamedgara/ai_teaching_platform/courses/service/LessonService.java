package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonContentUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonTitleUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.exception.LessonNotFoundException;
import com.mohamedgara.ai_teaching_platform.courses.mappers.LessonResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final LessonResponseMapper lessonResponseMapper;
    public LessonResponse updateLessonTitle(UUID lessonId, LessonTitleUpdateRequest lessonTitleUpdateRequest) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(()-> new LessonNotFoundException());

        lesson.setTitle(lessonTitleUpdateRequest.title());
        Lesson savedLesson = lessonRepository.save(lesson);

        return lessonResponseMapper.toLessonResponse(savedLesson);
    }

    public LessonResponse updateLessonContent(UUID lessonId, LessonContentUpdateRequest lessonContentUpdateRequest){
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(()->new LessonNotFoundException());

        lesson.setContent(lessonContentUpdateRequest.content());
        Lesson savedLesson = lessonRepository.save(lesson);

        return lessonResponseMapper.toLessonResponse(savedLesson);
    }
}
