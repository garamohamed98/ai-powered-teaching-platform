package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonContentUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonTitleUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.exception.LessonNotFoundException;
import com.mohamedgara.ai_teaching_platform.courses.mappers.LessonResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.projection.LessonTitle;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final LessonResponseMapper lessonResponseMapper;
    private final CourseService courseService;
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

    public LessonResponse getLessonById(UUID lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(()->new LessonNotFoundException());

        return lessonResponseMapper.toLessonResponse(lesson);
    }

    public void deleteLesson(UUID lessonId){
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(()->new LessonNotFoundException());
        lessonRepository.delete(lesson);
    }

    public boolean lessonExists(UUID lessonId){
        return lessonRepository.existsById(lessonId);
    }

    public Map<UUID, String> getLessonIdAndTitleListByCourseId(UUID courseId){
        if(!courseService.courseExists(courseId)){
            return Map.of();
        }
        List<LessonTitle> result = lessonRepository.findLessonIdAndTitleListByCourseId(courseId);

        return result.stream()
                .collect(Collectors.toMap(
                        lesson -> lesson.id(),
                        lesson -> lesson.title()
                ));
    }
}
