package com.stepx.stepx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    
    public void sendEmail(String toEmail, String subject, Map<String, Object> model) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom("stepx@shop.es");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        
        String content = getEmailContent(model);
        helper.setText(content, true);
        
        javaMailSender.send(message);
    }
    
    private String getEmailContent(Map<String, Object> model) {
        try {
            InputStream inputStream = new ClassPathResource("templates/email.html").getInputStream();
            Reader reader = new InputStreamReader(inputStream);
            Template template = Mustache.compiler().compile(reader);
            return template.execute(model);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template", e);
        }
    }
}