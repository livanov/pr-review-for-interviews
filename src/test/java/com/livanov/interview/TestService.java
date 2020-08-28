package com.livanov.interview;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.livanov.interview.repositories.PersonRepository;

@Service
public class TestService {
	
	@Autowired
	private PersonRepository personRepository;

	@Transactional
	public int countGrades() {	
    	 return StreamSupport.stream(personRepository.findAll().spliterator(), false)
    	 			.map( p -> p.getGrades().size())
    	 			.collect(Collectors.summingInt(Integer::intValue));
    }
}
