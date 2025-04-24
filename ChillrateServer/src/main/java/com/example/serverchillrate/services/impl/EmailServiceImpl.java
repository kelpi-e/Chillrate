package com.example.serverchillrate.services.impl;

import com.example.serverchillrate.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    @Override
    public void SendSimpleMessage(String to, String subject, String text){
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setFrom(System.getenv("SPRING_EMAIL"));
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }
}
