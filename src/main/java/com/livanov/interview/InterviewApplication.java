package com.livanov.interview;

import com.livanov.interview.entities.Person;
import com.livanov.interview.entities.Subject;
import com.livanov.interview.repositories.PersonRepository;
import com.livanov.interview.repositories.SubjectRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static java.util.Arrays.asList;

@SpringBootApplication
public class InterviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewApplication.class, args);
    }

    @Bean
    ApplicationRunner seedData(SubjectRepository subjectRepository, PersonRepository personRepository) {
        return args -> {

            subjectRepository.save(new Subject("Geography"));
            subjectRepository.save(new Subject("Mathematics"));

            List<Person> people = asList(
                    new Person("Peter"),
                    new Person("George"),
                    new Person("Steven"),
                    new Person("John"),
                    new Person("Aaron")
            );

            personRepository.saveAll(people);
        };
    }
}
