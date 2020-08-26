package com.livanov.interview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InterviewApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void canFetchPeople() throws Exception {
        mockMvc
                .perform(get("/people"))
                .andExpect(status().isOk());
    }

    @Test
    void uploadGradesFileTest() throws Exception {

        mockMvc
                .perform(
                        multipart("/people/grades")
                                .file("grades", fileContent("grades.csv"))
                )
                .andExpect(status().isOk());

    }

    private byte[] fileContent(String fileName) throws IOException, URISyntaxException {

        URL fileUrl = this.getClass().getClassLoader().getResource(fileName);
        URI fileUri = fileUrl.toURI();
        Path filePath = Paths.get(fileUri);
        byte[] fileBytes = Files.readAllBytes(filePath);

        return fileBytes;
    }
}
