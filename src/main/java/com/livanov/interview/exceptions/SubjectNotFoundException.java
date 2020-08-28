package com.livanov.interview.exceptions;

public class SubjectNotFoundException extends RuntimeException {

    public SubjectNotFoundException(long id) {
        super("No subject found for id: " + id);
    }
}
