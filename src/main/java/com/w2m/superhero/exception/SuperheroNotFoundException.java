package com.w2m.superhero.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SuperheroNotFoundException extends RuntimeException {
    public SuperheroNotFoundException(String exception) {
        super(exception);
    }
}

