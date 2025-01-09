package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;
public class CourseGradeNotFoundException extends StoreException{
    public CourseGradeNotFoundException() {
        this.setHttpStatus(HttpStatus.NOT_FOUND);
        this.setMessage("Link between Student and Course not found");
    }
}
