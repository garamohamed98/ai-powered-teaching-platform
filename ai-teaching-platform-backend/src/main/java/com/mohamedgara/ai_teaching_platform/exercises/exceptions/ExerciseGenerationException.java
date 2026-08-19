package com.mohamedgara.ai_teaching_platform.exercises.exceptions;

public class ExerciseGenerationException extends RuntimeException{
    public ExerciseGenerationException(){
        super("Failed to generate exercise ");
    }
}
