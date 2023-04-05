package com.w2m.superhero.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SuperheroExistsException extends RuntimeException {
    public SuperheroExistsException(String exception) {
        super(exception);
    }
}

