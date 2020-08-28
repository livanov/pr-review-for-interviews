package com.livanov.interview.services;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	// fake out the functionality
    public void send(String recipient, String message) {
    	System.out.println("Sending email to: " + recipient);
    	System.out.println(">>>\t" + message);
    }
}
