package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class RequestNotFoundException extends StoreException{
    public RequestNotFoundException() {
        this.setHttpStatus(HttpStatus.NOT_FOUND);
        this.setMessage("Request was not found");
    }
}
