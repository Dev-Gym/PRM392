package com.example.final_project.model;

import com.google.gson.annotations.SerializedName;

public class MedicalHistoryConfirmRequest {
    @SerializedName("description")
    private String description;

    public MedicalHistoryConfirmRequest() {}

    public MedicalHistoryConfirmRequest(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
