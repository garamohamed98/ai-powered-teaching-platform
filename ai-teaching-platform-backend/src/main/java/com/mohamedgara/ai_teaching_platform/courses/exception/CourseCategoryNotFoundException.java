package com.mohamedgara.ai_teaching_platform.courses.exception;

public class CourseCategoryNotFoundException extends RuntimeException {
    public CourseCategoryNotFoundException(){super("Course category not found");}
    public CourseCategoryNotFoundException(String message) {
        super(message);
    }
}
