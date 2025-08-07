package com.example.serverchillrate.services;

import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;

public interface EmailService {
    void SendSimpleMessage(String to, String subject, String message) throws MailException;
}
