package com.livanov.interview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.livanov.interview.repositories.PersonRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class InterviewApplicationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private TestService testService;
    
    @Test
    void canFetchPeople() throws Exception {
        mockMvc
                .perform(get("/people"))
                .andExpect(status().isOk());
    }

    @Test
    void uploadGradesFileTest() throws Exception {
    	
    	byte[] fileContent = TestUtils.getFileContent("grades.csv");
    	
    	long before = testService.countGrades();

        mockMvc
                .perform(
                        multipart("/people/grades")
                                .file("grades", fileContent)
                )
                .andExpect(status().isOk());

        long after = testService.countGrades();
        // can improve to use line count
        assertNotEquals(before, after);
    }
    
    /**
     * Adds a bad records and verifies that records have not been updated
     * @throws Exception
     */
    @Test
    void uploadGradesFileFailureTest() throws Exception {
    	
    	byte[] fileContent = TestUtils.getFileContent("grades.csv");
    	fileContent = new StringBuffer(new String(fileContent))
    			.append("\n1,-5,-10")
    			.toString().getBytes();

    	long before = testService.countGrades();

    	try {
	        mockMvc
	                .perform(
	                        multipart("/people/grades")
	                                .file("grades", fileContent)
	                )
	                // needs a common exception handler that translates the runtime exceptions to error statuses
	                .andExpect(status().is5xxServerError());
    	} catch(Exception e) {
    		e.printStackTrace();
    	}

        long after = testService.countGrades();
        assertEquals(before, after);
    }
}
