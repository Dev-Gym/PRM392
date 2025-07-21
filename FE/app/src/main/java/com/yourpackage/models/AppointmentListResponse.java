package com.yourpackage.models;

import java.util.List;

public class AppointmentListResponse {
    private List<AppointmentResponse> appointments;

    public AppointmentListResponse() {
    }

    public AppointmentListResponse(List<AppointmentResponse> appointments) {
        this.appointments = appointments;
    }

    public List<AppointmentResponse> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentResponse> appointments) {
        this.appointments = appointments;
    }
}