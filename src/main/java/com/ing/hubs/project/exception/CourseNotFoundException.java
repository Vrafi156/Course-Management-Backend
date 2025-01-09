package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class CourseNotFoundException extends StoreException{
    public CourseNotFoundException() {
        this.setHttpStatus(HttpStatus.NOT_FOUND);
        this.setMessage("Course was not found");
    }
}
