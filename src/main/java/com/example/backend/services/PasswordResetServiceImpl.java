package com.example.backend.services;

import com.example.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class PasswordResetServiceImpl {


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    public void generateAndSendPasswordResetToken(User user) {
        // Generate a random password reset token (you can use a library for this)
        String resetToken = generateRandomToken();

        // Store the token in the database, associating it with the user
        user.setResetToken(resetToken);
        userService.editUser(user,user.getUser_group().getgId());

        // Send an email to the user with a link to reset their password
        sendPasswordResetEmail(user.getuMail(), resetToken);
    }

    private void sendPasswordResetEmail(String toEmail, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("raftools@almadar.ly");
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click on the following link: http://10.156.35.98:9998/fraud/#/reset-password?token=" + resetToken);

        mailSender.send(message);
    }

    private String generateRandomToken() {
        // Generate a random token using a library or method of your choice
        // Example using UUID:
        return UUID.randomUUID().toString();
    }
}
