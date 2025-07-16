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

public interface ApiService {
    @POST("api/Auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @GET("api/User")
    Call<List<User>> getUsers(@Header("Authorization") String token);

    @POST("api/User")
    Call<User> createUser(@Header("Authorization") String token, @Body User user);

    @PUT("api/User/{id}")
    Call<User> updateUser(@Header("Authorization") String token, @Path("id") int id, @Body User user);

    @DELETE("api/User/{id}")
    Call<Void> deleteUser(@Header("Authorization") String token, @Path("id") int id);

    @GET("api/Appointments")
    Call<List<Appointment>> getAppointments(@Header("Authorization") String token);

    @POST("api/Appointments")
    Call<Appointment> createAppointment(@Header("Authorization") String token, @Body Appointment appointment);

    @PUT("api/Appointments/{id}")
    Call<Appointment> updateAppointment(@Header("Authorization") String token, @Path("id") int id,
            @Body Appointment appointment);

    @DELETE("api/Appointments/{id}")
    Call<Void> deleteAppointment(@Header("Authorization") String token, @Path("id") int id);

    @GET("api/Schedule")
    Call<List<Schedule>> getSchedules(@Header("Authorization") String token);

    @POST("api/Schedule")
    Call<Schedule> createSchedule(@Header("Authorization") String token, @Body Schedule schedule);

    @PUT("api/Schedule/{id}")
    Call<Schedule> updateSchedule(@Header("Authorization") String token, @Path("id") int id, @Body Schedule schedule);

    @DELETE("api/Schedule/{id}")
    Call<Void> deleteSchedule(@Header("Authorization") String token, @Path("id") int id);

    @GET("api/MedicalExpert")
    Call<List<MedicalExpert>> getMedicalExperts(@Header("Authorization") String token);
}