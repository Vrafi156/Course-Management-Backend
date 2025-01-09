package com.ing.hubs.project.exception;
import com.ing.hubs.project.entity.Course;
import org.springframework.http.HttpStatus;

public class CourseAlreadyExistsException extends StoreException {
    public CourseAlreadyExistsException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("Course already registered");
    }


}
