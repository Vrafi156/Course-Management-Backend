package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends StoreException {
    public UserNotFoundException() {
        this.setHttpStatus(HttpStatus.NOT_FOUND);
        this.setMessage("User was not found");
    }
}
