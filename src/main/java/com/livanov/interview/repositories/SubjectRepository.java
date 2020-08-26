package com.livanov.interview.repositories;

import com.livanov.interview.entities.Subject;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SubjectRepository extends PagingAndSortingRepository<Subject, Long> {
}
