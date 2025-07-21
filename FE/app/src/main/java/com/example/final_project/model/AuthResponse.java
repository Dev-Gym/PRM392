package com.example.final_project.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("userId")
    private int userId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("userType")
    private String userType;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(int userId, String fullName, String email, String userType) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.userType = userType;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}