package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class CourseOverlapingException extends StoreException {
    public CourseOverlapingException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("Course overlaps with another");
    }

}


