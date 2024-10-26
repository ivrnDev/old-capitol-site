package com.econnect.barangaymanagementapp.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class EmailService {
    //Sample email credentials
    private final String fromEmail = "brgy.oldcapitolsite.gov@gmail.com";
    private final String password = "nmjoskntnlgooxqq";

    public boolean sendEmail(String toEmail, String subject, String messageText) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(messageText);

//            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public CompletableFuture<Boolean> sendEmailAsync(String toEmail, String subject, String messageText) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendEmail(toEmail, subject, messageText);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
