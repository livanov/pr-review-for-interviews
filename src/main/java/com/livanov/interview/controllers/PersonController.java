package com.livanov.interview.controllers;

import com.livanov.interview.entities.Grade;
import com.livanov.interview.entities.Person;
import com.livanov.interview.entities.Subject;
import com.livanov.interview.exceptions.PersonNotFoundException;
import com.livanov.interview.repositories.PersonRepository;
import com.livanov.interview.repositories.SubjectRepository;
import com.livanov.interview.services.EmailService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping("people")
public class PersonController {

    private final PersonRepository personRepository;
    private final SubjectRepository subjectRepository;

    public PersonController(PersonRepository personRepository, SubjectRepository subjectRepository) {
        this.personRepository = personRepository;
        this.subjectRepository = subjectRepository;
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


    @PostMapping("grades")
    public void bulkGradesChange(@RequestParam("grades") MultipartFile file) throws IOException {

        byte[] fileBytes = file.getBytes();
        String fileContent = new String(fileBytes, Charset.defaultCharset());

        Map<Long, Map<Long, Double>> data = new HashMap<>();
        parseFile(fileContent, data);

        data.forEach((id, gradeMap) -> {

            Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));

            Map<Long, Subject> subjects = StreamSupport.stream(subjectRepository.findAllById(gradeMap.keySet()).spliterator(), false).collect(toMap(Subject::getId, x -> x));

            gradeMap.forEach((subjectId, grade) -> {
                Subject subject = subjects.get(subjectId);

                person.addGrade(new Grade(subject, grade));

                personRepository.save(person);
            });
        });

        EmailService emailService = new EmailService();
        emailService.send("admin@my-system.com", "File upload successfully processed.");
    }

    private void parseFile(String file, Map<Long, Map<Long, Double>> data) {
        String[] lines = file.split("\\n");

        String currentLine;
        for (int i = 0; i < lines.length; i++) {
            if (i == 0) {
                continue;
            }

            currentLine = lines[i];
            String[] pieces = currentLine.split(",");

            data.putIfAbsent(Long.parseLong(pieces[0]), new HashMap<>());

            data.get(Long.parseLong(pieces[0])).put(Long.parseLong(pieces[1]), Double.parseDouble(pieces[2]));
        }
    }
}
