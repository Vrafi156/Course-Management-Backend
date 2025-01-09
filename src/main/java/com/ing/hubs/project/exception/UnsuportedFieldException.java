package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class UnsuportedFieldException extends  StoreException {
    public UnsuportedFieldException(String message){
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage(message);
    }
}
