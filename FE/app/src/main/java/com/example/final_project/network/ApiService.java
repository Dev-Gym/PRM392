package com.example.final_project.network;

import com.example.final_project.model.*;
import com.example.final_project.model.AppointmentCreateRequest;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Authentication
    @POST("api/Auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);
    @GET("api/MedicalHistory")
    Call<List<MedicalHistory>> getMedicalHistoryByUserId(@Query("userId") int userId);
    // Medical History
    @GET("api/MedicalHistory")
    Call<List<MedicalHistory>> getMedicalHistory();
    @PUT("api/MedicalHistory/delete/{MedicalHistoryId}")
    Call<MedicalHistory> deleteMedicalHistory(@Path("MedicalHistoryId") int historyId);

    @PUT("api/MedicalHistory/processing/{MedicalHistoryId}")
    Call<MedicalHistory> processingMedicalHistory(@Path("MedicalHistoryId") int historyId);

    @PUT("api/MedicalHistory/cancelled/{MedicalHistoryId}")
    Call<MedicalHistory> cancelMedicalHistory(@Path("MedicalHistoryId") int historyId);
    @PUT("api/MedicalHistory/completed/{MedicalHistoryId}")
    Call<MedicalHistory> completedMedicalHistory(@Path("MedicalHistoryId") int historyId, @Body MedicalHistoryConfirmRequest request);
    // Appointments
    @GET("api/Appointments")
    Call<List<Appointment>> getAppointments();

    // GET single appointment by ID - THÊM METHOD NÀY
    @GET("api/Appointments/{id}")
    Call<Appointment> getAppointment(@Path("id") int appointmentId);

    // CREATE appointment with AppointmentCreateRequest
    @POST("api/Appointments")
    Call<Appointment> createAppointment(@Body AppointmentCreateRequest request);

    @GET("api/Appointments")
    Call<List<Appointment>> getAppointmentsByUserId(@Query("userId") int userId);

    @PUT("api/Appointments/{id}")
    Call<Appointment> updateAppointment(@Path("id") int id, @Body Appointment appointment);
    @PUT("api/Appointments/confirm/{appointmentId}")
    Call<Appointment> confirmAppointment(@Path("appointmentId") int appointmentId);

    @PUT("api/Appointments/cancelled/{appointmentId}")
    Call<Appointment> cancelAppointment(@Path("appointmentId") int appointmentId);
    @PUT("api/Appointments/delete/{id}")
    Call<Appointment> deleteAppointment(@Path("id") int id);
    @GET("api/Appointments/unvaliable/{expertId}")
    Call<List<Appointment>> getUnavailableAppointments(@Path("expertId") int expertId);

    // Medical Experts
    @GET("api/User/get-all-experts")
    Call<List<User>> getAllExperts();

    // Users
    @GET("api/User")
    Call<List<User>> getUsers();

    @POST("api/User")
    Call<User> createUser(@Body User user);

    @PUT("api/User/{id}")
    Call<User> updateUser(@Path("id") int id, @Body User user);

    @DELETE("api/User/{id}")
    Call<Void> deleteUser(@Path("id") int id);

    // Schedules
    @GET("api/Schedule")
    Call<List<Schedule>> getSchedules();

    @POST("api/Schedule")
    Call<Schedule> createScheduleWithRequest(@Body ScheduleRequest request);

    @PUT("api/Schedule/{id}")
    Call<Schedule> updateScheduleWithRequest(@Path("id") int id, @Body ScheduleRequest request);

    @PUT("api/Schedule/delete/{id}")
    Call<Schedule> deleteSchedule(@Path("id") int id);

    @GET("api/Schedule/{id}")
    Call<Schedule> getScheduleById(@Path("id") int scheduleId);

    // Expert Schedules
    @GET("api/Schedule")
    Call<List<ExpertSchedule>> getExpertSchedules(@Query("expertId") int expertId);

    // Facilities by Expert - Returns single Facility object
    @GET("api/Facility/expert/{expertId}")
    Call<Facility> getFacilityByExpert(@Path("expertId") int expertId);
}