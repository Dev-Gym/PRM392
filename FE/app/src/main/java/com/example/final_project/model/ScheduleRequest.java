package com.example.final_project.model;

import com.google.gson.annotations.SerializedName;

public class ScheduleRequest {
    @SerializedName("expertId")
    private int expertId;

    @SerializedName("dayOfWeek")
    private String dayOfWeek;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("isActive")
    private boolean isActive;

    // Constructors
    public ScheduleRequest() {}

    public ScheduleRequest(int expertId, String dayOfWeek, String startDate, String endDate, boolean isActive) {
        this.expertId = expertId;
        this.dayOfWeek = dayOfWeek;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getExpertId() {
        return expertId;
    }

    public void setExpertId(int expertId) {
        this.expertId = expertId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "ScheduleRequest{" +
                "expertId=" + expertId +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}