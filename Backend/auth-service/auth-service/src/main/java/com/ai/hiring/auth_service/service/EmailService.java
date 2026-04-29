package com.ai.hiring.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("HireAI — Password Reset OTP");
            message.setText(
                    "Hello,\n\n" +
                            "Your OTP for password reset is: " + otp + "\n\n" +
                            "This OTP is valid for 15 minutes only.\n\n" +
                            "If you did not request this, please ignore this email.\n\n" +
                            "Regards,\nHireAI Team"
            );
            mailSender.send(message);
            System.out.println("OTP email sent to: " + toEmail);
        } catch (Exception e) {
            System.out.println("Email send failed: " + e.getMessage());
            throw new RuntimeException("Failed to send email!");
        }
    }

    public void sendPasswordChangeEmail(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("HireAI — Password Changed Successfully");
            message.setText(
                    "Hello,\n\n" +
                            "Your password has been changed successfully.\n\n" +
                            "If you did not make this change, please contact us immediately.\n\n" +
                            "Regards,\nHireAI Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Email send failed: " + e.getMessage());
        }
    }
}