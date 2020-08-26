package com.livanov.interview.repositories;

import com.livanov.interview.entities.Person;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {
}
