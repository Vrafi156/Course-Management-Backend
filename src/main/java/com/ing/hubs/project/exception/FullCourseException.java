package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class FullCourseException extends StoreException{
    public FullCourseException(){
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("Course full");
    }
}
