package com.demo.loan.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    // Method to send email notifications
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        emailSender.send(message);
    }

    // Method to send a loan approval email
    public void sendLoanApprovalEmail(String email, String loanDetails) {
        String subject = "Your Loan has been Approved!";
        String text = "Congratulations! Your loan has been approved. Here are the details:\n" + loanDetails;

        sendEmail(email, subject, text);
    }

    // Method to send an EMI payment reminder email
    public void sendEmiReminderEmail(String email, String emiDetails) {
        String subject = "EMI Payment Reminder";
        String text = "Dear User,\nThis is a reminder that your EMI payment is due. Please find the details below:\n" + emiDetails;

        sendEmail(email, subject, text);
    }
}
