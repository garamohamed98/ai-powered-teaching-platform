package com.mohamedgara.ai_teaching_platform.courses.exceptions;

public class CourseNotFoundException extends RuntimeException{

    public CourseNotFoundException(){
        super("Service not Found");
    }
    public CourseNotFoundException(String message){
        super(message);
    }
}
