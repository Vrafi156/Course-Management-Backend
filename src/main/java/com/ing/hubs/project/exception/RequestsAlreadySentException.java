package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class RequestsAlreadySentException extends StoreException{
    public RequestsAlreadySentException(){
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("Request already sent to this course");
    }
}
