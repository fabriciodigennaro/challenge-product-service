package com.challenge.productservice.infrastructure.entrypoint.rest;

import com.challenge.productservice.infrastructure.entrypoint.rest.response.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
       RuntimeException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Problem handle(RuntimeException exception) {
        return new Problem("Internal server error, please try later");
    }

}
