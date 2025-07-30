package com.example.serverchillrate.config;

import com.example.serverchillrate.models.MailData;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;
/*
Конфигурация Simple Mail Transfer Protocol
значения host port и особенности подключения:
https://help.mail.ru/mail/mailer/settings/
Время ответа от сервиса - не более 1 секунды
Время на подключение к сервису - не более 1 секунды
Значение username  и password берём из переменных окружения (создать класс MailParams)
*/
@Configuration
public class SMTPConfig {
    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender=new JavaMailSenderImpl();
        mailSender.setHost("smtp.mail.ru");
        mailSender.setPort(465);
        mailSender.setUsername(System.getenv("SPRING_EMAIL"));
        mailSender.setPassword(System.getenv("SPRING_EMAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        /*
        props.put("mail.debug", "true");
         вывод отладочной информации (выключен)
         */
        props.put("mail.smtp.ssl.protocols","TLSv1.2");
        props.put("mail.smtp.ssl.enable","true");
        props.put("mail.smtp.ssl.trust","smtp.mail.ru");
        // props.put("mail.smtp.timeout", "1000");
        // props.put("mail.smtp.connectiontimeout", "1000");
        return mailSender;
    }
    @Bean
    public MailData mailData(){
        return MailData.builder().
                name(System.getenv("SPRING_EMAIL")).
                password(System.getenv("SPRING_EMAIL_PASSWORD")).build();
    }
}
