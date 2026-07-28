package com.mohamedgara.ai_teaching_platform.courses.exception;

public class LessonNotFoundException extends RuntimeException {

    public LessonNotFoundException(){super("Lesson not Found");}
    public LessonNotFoundException(String message){super(message);}
}
