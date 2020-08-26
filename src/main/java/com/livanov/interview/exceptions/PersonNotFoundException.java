package com.livanov.interview.exceptions;

public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(long id) {
        super("No person found for id: " + id);
    }
}
