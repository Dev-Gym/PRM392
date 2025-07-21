package com.yourpackage.models;

public class MedicalHistoryResponse {
    // TODO: Thêm các trường phù hợp với response thực tế
    private int id;
    private String historyDetails;

    public MedicalHistoryResponse() {
    }

    public MedicalHistoryResponse(int id, String historyDetails) {
        this.id = id;
        this.historyDetails = historyDetails;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHistoryDetails() {
        return historyDetails;
    }

    public void setHistoryDetails(String historyDetails) {
        this.historyDetails = historyDetails;
    }
}