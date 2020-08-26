package com.livanov.interview.controllers;

import com.livanov.interview.entities.Grade;
import com.livanov.interview.entities.Person;
import com.livanov.interview.exceptions.PersonNotFoundException;
import com.livanov.interview.repositories.PersonRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("people")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public Iterable<Person> getPeople(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {

        PageRequest pageRequest = PageRequest.of(page, size);

        return personRepository.findAll(pageRequest);
    }

    @GetMapping("{id}")
    public Person getPerson(@PathVariable long id) {
        return personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    }

    @GetMapping("{id}/grades")
    public List<Grade> getPersonGrades(@PathVariable long id) {
        return personRepository.findById(id)
                .map(Person::getGrades)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }
}
