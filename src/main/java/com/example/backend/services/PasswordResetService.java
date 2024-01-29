package com.example.backend.services;

import com.example.backend.entities.User;

public interface PasswordResetService {

    public void generateAndSendPasswordResetToken(User user);
    public void sendPasswordResetEmail(String toEmail, String resetToken);
    public String generateRandomToken();
}
