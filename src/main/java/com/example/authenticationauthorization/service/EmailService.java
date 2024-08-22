package com.example.authenticationauthorization.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private static final String FROM_EMAIL = "example@example.com";
    private static final String TOKEN_URL_RESET = "http://localhost:8200/api/auth/reset-password?token=";
    private static final String TOKEN_URL_VERYFY = "http://localhost:8200/api/auth/verify?token=";

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String verificationLink = TOKEN_URL_VERYFY + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject("Please verify your email address");
        message.setText("To complete your registration, please click the following link: " + verificationLink);

        javaMailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = TOKEN_URL_RESET + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetUrl);
        javaMailSender.send(message);
    }

    public void sendWarningLoginFromDifferentDevice(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject("Someone try to login your account");
        message.setText("Someone is trying to login to your account in different device ");

        javaMailSender.send(message);
    }

}
