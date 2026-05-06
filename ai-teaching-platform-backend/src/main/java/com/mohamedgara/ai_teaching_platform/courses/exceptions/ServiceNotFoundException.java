package com.mohamedgara.ai_teaching_platform.courses.exceptions;

public class ServiceNotFoundException extends RuntimeException{

    public ServiceNotFoundException(){
        super("Service not Found");
    }
    public ServiceNotFoundException(String message){
        super(message);
    }
}
