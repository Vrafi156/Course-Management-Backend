package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class InvalidInfoException extends StoreException{
    public InvalidInfoException(String message){
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage(message);
    }
}
