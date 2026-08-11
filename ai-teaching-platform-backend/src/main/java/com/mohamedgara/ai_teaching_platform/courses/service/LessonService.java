package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.LessonInfo;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonContentUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonTitleUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.exception.LessonNotFoundException;
import com.mohamedgara.ai_teaching_platform.courses.mappers.LessonResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.projection.LessonTitle;
import com.mohamedgara.ai_teaching_platform.courses.projection.LessonTitleAndContent;
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

    public boolean lessonListExists(List<UUID> lessonIdList){
        return lessonRepository.countExistingLessons(lessonIdList) == lessonIdList.size();
    }

    public Map<UUID, String> getLessonSummaryByCourseId(UUID courseId){
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

    public List<LessonInfo> getCourseLessonInfoList(UUID courseId){
        if(!courseService.courseExists(courseId)){
            return List.of();
        }
        List<LessonTitleAndContent> result = lessonRepository.findLessonTitleAndContentByCourseId(courseId);

        return result.stream()
                .map(lesson->{
                    return new LessonInfo(
                            lesson.id(),
                            lesson.title(),
                            lesson.content()
                    );
                }).toList();
    }

    public List<LessonInfo> getLessonInfoList(List<UUID> lessonIdList){
        List<LessonTitleAndContent> result = lessonRepository.findLessonTitleAndContentByLessonIdList(lessonIdList);

        return result.stream()
                .map(lesson->{
                    return new LessonInfo(
                            lesson.id(),
                            lesson.title(),
                            lesson.content()
                    );
                }).toList();
    }
}
