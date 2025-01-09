package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends StoreException {
    public UserAlreadyExistsException() {
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("User already exists");
    }
}
