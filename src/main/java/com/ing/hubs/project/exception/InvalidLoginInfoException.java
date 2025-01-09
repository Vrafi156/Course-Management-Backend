package com.ing.hubs.project.exception;

import org.springframework.http.HttpStatus;

public class InvalidLoginInfoException extends StoreException{
    public InvalidLoginInfoException(){
        this.setHttpStatus(HttpStatus.FORBIDDEN);
        this.setMessage("Invalid login details, please try again");
    }
}
