package com.livanov.interview.entities;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class Grade {

    @ManyToOne
    private Subject subject;

    private Double value;

    protected Grade() {
        // Required by Hibernate
    }

    public Grade(Subject subject, Double value) {
        this.subject = subject;
        this.value = value;
    }

    public Subject getSubject() {
        return subject;
    }

    public Double getValue() {
        return value;
    }
}
