package com.livanov.interview.controllers;

import com.livanov.interview.entities.Grade;
import com.livanov.interview.entities.Person;
import com.livanov.interview.entities.Subject;
import com.livanov.interview.exceptions.PersonNotFoundException;
import com.livanov.interview.exceptions.SubjectNotFoundException;
import com.livanov.interview.repositories.PersonRepository;
import com.livanov.interview.repositories.SubjectRepository;
import com.livanov.interview.services.EmailService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

@RestController
@RequestMapping("people")
public class PersonController {

    private final PersonRepository personRepository;
    private final SubjectRepository subjectRepository;
    private final EmailService emailService;
    
    // TODO convert to a property since it can be applicable to other parts of the system as well 
    // 		and it is easier to  change and manage from a properties file
    private final String adminEmail = "admin@my-system.com";

    public PersonController(
    		PersonRepository personRepository,
    		SubjectRepository subjectRepository,
    		EmailService emailService) {
    	
        this.personRepository = personRepository;
        this.subjectRepository = subjectRepository;
        this.emailService = emailService;
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
    @Transactional
    public void bulkGradesChange(@RequestParam("grades") MultipartFile file) throws IOException {

        byte[] fileBytes = file.getBytes();
        String fileContent = new String(fileBytes, Charset.defaultCharset());
        
        Map<Long, Collection<Pair<Long, Double>>> data = parseFile(fileContent);
        
        // you may want to pre-validate before making transactional changes
        data.forEach((id, subjectGradePair) -> {
        	Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        	subjectGradePair.forEach( g -> {
        		// I am only assuming that you wanted to fail if the subject is not found as is the case with a person that is not found
    				// rather than quietly ignoring it without any notification to the user
        		// not as fast as fetching all ids in a single call. Can be re-written if needed for a higher throughput system
        		long subjectId = g.getFirst();
        		Subject subject = subjectRepository.findById(subjectId).orElseThrow( () -> new SubjectNotFoundException(subjectId));
        		person.addGrade(new Grade(subject, g.getSecond()));        		
        		personRepository.save(person);
        		System.out.println("Added grade: " + g);
        	});
        });

        // internationalize the messages if needed
        // since we are fail first, we don't send additional details. 
        // If needed, partial uploads / merge can be supported and a status email sent out
        emailService.send(adminEmail, "File upload successfully processed.");
    }

    private Map<Long, Collection<Pair<Long, Double>>> parseFile(String fileContent) {
    	
    	Map<Long, Collection<Pair<Long,Double>>> data = new HashMap<>();    	
        String[] lines = fileContent.split("\\n");

        // skip the headers
        for (int i = 1; i < lines.length; i++) {

        	String currentLine = lines[i];
            String[] pieces = currentLine.split(",");
            
            // clearly shows what is getting parsed from the file content
            long userId = Long.parseLong(pieces[0]);
            long subjectId = Long.parseLong(pieces[1]);
            double grade = Double.parseDouble(pieces[2]);
            
            data.putIfAbsent(userId, new ArrayList<>());
            
            // if we can have multiple grades per subject in the file it is better to use a collection
            // the data model for a Grade->Subject is @ManyToOne as well
            data.get(userId).add(Pair.of(subjectId, grade));
        }
        
        return data;
    }
}
