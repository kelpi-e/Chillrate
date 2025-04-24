package com.example.serverchillrate.services;

public interface EmailService {
    void SendSimpleMessage(String to, String subject, String message);
}
