package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class NoScheduleFoundException extends StoreException{
    public NoScheduleFoundException(){
        this.setHttpStatus(HttpStatus.BAD_REQUEST);
        this.setMessage("No Schedule for Course");
    }
}
