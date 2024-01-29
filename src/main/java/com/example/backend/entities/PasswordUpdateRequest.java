package com.example.backend.entities;

public class PasswordUpdateRequest {

    private String token;
    private String currentPassword;
    private String newPassword;

    public PasswordUpdateRequest() {
    }

    public PasswordUpdateRequest(String token, String currentPassword, String newPassword) {
        this.token = token;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
