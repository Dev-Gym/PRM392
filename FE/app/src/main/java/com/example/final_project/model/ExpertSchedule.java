package com.example.final_project.model;



import com.google.gson.annotations.SerializedName;

public class ExpertSchedule {
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

    // Constructor mặc định
    public ExpertSchedule() {}

    // Constructor có tham số
    public ExpertSchedule(int scheduleId, int expertId, String dayOfWeek, String startDate, String endDate, boolean isActive) {
        this.scheduleId = scheduleId;
        this.expertId = expertId;
        this.dayOfWeek = dayOfWeek;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    // Getters
    public int getScheduleId() {
        return scheduleId;
    }

    public int getExpertId() {
        return expertId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setExpertId(int expertId) {
        this.expertId = expertId;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Helper method để lấy giờ làm việc từ startDate và endDate
    public String getWorkingHours() {
        if (startDate != null && endDate != null) {
            try {
                // Từ "1900-01-01T07:00:00" lấy "07:00"
                String startTime = extractTime(startDate);
                String endTime = extractTime(endDate);
                return startTime + " - " + endTime;
            } catch (Exception e) {
                return "Không rõ giờ";
            }
        }
        return "Không có thông tin";
    }

    // Helper method để extract time từ datetime string
    private String extractTime(String dateTimeString) {
        if (dateTimeString == null || !dateTimeString.contains("T")) {
            return "??:??";
        }

        try {
            // Lấy phần sau "T" và trước ":"
            String timePart = dateTimeString.substring(dateTimeString.indexOf('T') + 1);
            // Lấy giờ:phút (HH:mm)
            return timePart.substring(0, 5);
        } catch (Exception e) {
            return "??:??";
        }
    }

    // Method để lấy tên tiếng Việt của ngày trong tuần
    public String getDayOfWeekInVietnamese() {
        if (dayOfWeek == null) return "Không rõ";

        switch (dayOfWeek.toLowerCase()) {
            case "monday": return "Thứ Hai";
            case "tuesday": return "Thứ Ba";
            case "wednesday": return "Thứ Tư";
            case "thursday": return "Thứ Năm";
            case "friday": return "Thứ Sáu";
            case "saturday": return "Thứ Bảy";
            case "sunday": return "Chủ Nhật";
            default: return dayOfWeek;
        }
    }

    // toString method để debug
    @Override
    public String toString() {
        return "ExpertSchedule{" +
                "scheduleId=" + scheduleId +
                ", expertId=" + expertId +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", isActive=" + isActive +
                ", workingHours='" + getWorkingHours() + '\'' +
                '}';
    }
}