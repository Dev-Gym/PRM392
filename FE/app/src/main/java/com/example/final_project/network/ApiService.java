package com.example.final_project.network;

import com.example.final_project.model.*;
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

    // Medical History
    @GET("api/MedicalHistory")
    Call<List<MedicalHistory>> getMedicalHistory();

    // Appointments
    @GET("api/Appointments")
    Call<List<Appointment>> getAppointments();

    @POST("api/Appointments")
    Call<Appointment> createAppointment(@Body Appointment appointment);

    @GET("api/Appointments")
    Call<List<Appointment>> getAppointmentsByUserId(@Query("userId") int userId);

    @PUT("api/Appointments/{id}")
    Call<Appointment> updateAppointment(@Path("id") int id, @Body Appointment appointment);

    @PUT("api/Appointments/delete/{id}")
    Call<Appointment> deleteAppointment(@Path("id") int id);

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
    Call<Schedule> createSchedule(@Body Schedule schedule);

    @PUT("api/Schedule/{id}")
    Call<Schedule> updateSchedule(@Path("id") int id, @Body Schedule schedule);

    @DELETE("api/Schedule/{id}")
    Call<Void> deleteSchedule(@Path("id") int id);

    // Expert Schedules - NEW API
    @GET("api/Schedule")
        Call<List<ExpertSchedule>> getExpertSchedules(@Query("expertId") int expertId);
}