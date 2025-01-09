package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends StoreException{
    public InternalServerErrorException(){
        this.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        this.setMessage("Internal Server error, please try again");
    }
}
