package com.example.final_project.model;
import com.google.gson.annotations.SerializedName;

public class Schedule {
    @SerializedName("scheduleId")
    private int scheduleId;

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

    // Legacy fields (keep for backward compatibility)
    private int id;
    private int doctorId;
    private String date;
    private String time;

    // Constructors
    public Schedule() {}

    // Getters and Setters for new fields
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

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

    // Legacy getters and setters (keep for backward compatibility)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.scheduleId = id; // Map to new field
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
        this.expertId = doctorId; // Map to new field
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId +
                ", expertId=" + expertId +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}