package com.ing.hubs.project.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class StoreException extends RuntimeException {
    private HttpStatus httpStatus;
    private String message;
}
