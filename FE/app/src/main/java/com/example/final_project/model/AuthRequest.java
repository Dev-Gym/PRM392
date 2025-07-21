package com.example.final_project.model;
import com.google.gson.annotations.SerializedName;

public class AuthRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    // Constructors
    public AuthRequest() {}

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthRequest{" +
                "email='" + email + '\'' +
                ", password='[HIDDEN]'" +
                '}';
    }
}