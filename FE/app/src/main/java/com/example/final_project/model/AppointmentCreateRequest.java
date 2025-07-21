package com.example.final_project.model;


import com.google.gson.annotations.SerializedName;

public class AppointmentCreateRequest {
    @SerializedName("scheduleId")
    private int scheduleId;

    @SerializedName("patientId")
    private int patientId;

    @SerializedName("expertId")
    private int expertId;

    @SerializedName("facilityId")
    private int facilityId;

    @SerializedName("note")
    private String note;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    // Constructors
    public AppointmentCreateRequest() {}

    public AppointmentCreateRequest(int scheduleId, int patientId, int expertId, int facilityId,
                                    String note, String startDate, String endDate) {
        this.scheduleId = scheduleId;
        this.patientId = patientId;
        this.expertId = expertId;
        this.facilityId = facilityId;
        this.note = note;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getExpertId() {
        return expertId;
    }

    public void setExpertId(int expertId) {
        this.expertId = expertId;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "AppointmentCreateRequest{" +
                "scheduleId=" + scheduleId +
                ", patientId=" + patientId +
                ", expertId=" + expertId +
                ", facilityId=" + facilityId +
                ", note='" + note + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
