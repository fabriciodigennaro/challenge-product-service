package com.challenge.productservice.infrastructure.entrypoint.rest;

import com.challenge.productservice.infrastructure.entrypoint.rest.response.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
       RuntimeException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Problem handle(RuntimeException exception) {
        return new Problem("An unexpected error occurred");
    }

    @ExceptionHandler({
        MissingServletRequestParameterException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Problem handle(MissingServletRequestParameterException exception) {
        String message = String.format("Required request parameter '%s' is not present", exception.getParameterName());
        return new Problem(message);
    }

    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Problem handle(MethodArgumentTypeMismatchException exception) {
        String message = String.format("Parameter '%s' has an invalid type", exception.getParameter().getParameter().getName());
        return new Problem(message);
    }

}
