package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class RequestAlreadyAnsweredException extends StoreException{
    public RequestAlreadyAnsweredException(){
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("Request pending or already answered");
    }
}
