package com.challenge.productservice.infrastructure.entrypoint.rest;

public class RequestParamWithInvalidFormatException extends RuntimeException {

    public RequestParamWithInvalidFormatException(String paramName) {
        super(String.format("Request param '%s' has an invalid format", paramName));
    }
}
