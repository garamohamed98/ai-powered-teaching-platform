package com.mohamedgara.ai_teaching_platform.exercises.exceptions;

public class NoLessonReferenceException extends RuntimeException{
    public NoLessonReferenceException(){
        super("No reference content found for exercise generation");
    }
}
