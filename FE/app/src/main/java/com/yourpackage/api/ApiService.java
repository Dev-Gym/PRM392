package com.yourpackage.api;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.yourpackage.models.MedicalHistoryResponse;
import com.yourpackage.models.AppointmentListResponse;
import com.yourpackage.models.AppointmentRequest;
import com.yourpackage.models.AppointmentResponse;

public class ApiService {
    private static final String TAG = "ApiService";
    private static final String BASE_URL = "http://10.0.2.2:5000/api/"; // For emulator
    // private static final String BASE_URL = "http://192.168.1.100:5000/api/"; //
    // For real device

    private OkHttpClient client;
    private Gson gson;

    public ApiService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
    }

    // Get Medical History
    public void getMedicalHistory(int userId, ApiCallback<MedicalHistoryResponse> callback) {
        String url = BASE_URL + "MedicalHistory/" + userId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to get medical history", e);
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Medical history response: " + responseBody);

                    if (response.isSuccessful()) {
                        MedicalHistoryResponse medicalHistory = gson.fromJson(responseBody,
                                MedicalHistoryResponse.class);
                        callback.onSuccess(medicalHistory);
                    } else {
                        callback.onError("HTTP " + response.code() + ": " + responseBody);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing medical history response", e);
                    callback.onError("Error parsing response: " + e.getMessage());
                }
            }
        });
    }

    // Get User Appointments
    public void getUserAppointments(int userId, ApiCallback<AppointmentListResponse> callback) {
        String url = BASE_URL + "Appointments/my?userId=" + userId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to get appointments", e);
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Appointments response: " + responseBody);

                    if (response.isSuccessful()) {
                        AppointmentListResponse appointments = gson.fromJson(responseBody,
                                AppointmentListResponse.class);
                        callback.onSuccess(appointments);
                    } else {
                        callback.onError("HTTP " + response.code() + ": " + responseBody);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing appointments response", e);
                    callback.onError("Error parsing response: " + e.getMessage());
                }
            }
        });
    }

    // Create Appointment
    public void createAppointment(AppointmentRequest appointmentRequest, ApiCallback<AppointmentResponse> callback) {
        String url = BASE_URL + "Appointments";

        String jsonBody = gson.toJson(appointmentRequest);
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to create appointment", e);
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Create appointment response: " + responseBody);

                    if (response.isSuccessful()) {
                        AppointmentResponse appointment = gson.fromJson(responseBody, AppointmentResponse.class);
                        callback.onSuccess(appointment);
                    } else {
                        callback.onError("HTTP " + response.code() + ": " + responseBody);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing create appointment response", e);
                    callback.onError("Error parsing response: " + e.getMessage());
                }
            }
        });
    }

    // Logging Interceptor
    private static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Log.d(TAG, "Request: " + request.method() + " " + request.url());

            Response response = chain.proceed(request);
            Log.d(TAG, "Response: " + response.code() + " for " + request.url());

            return response;
        }
    }

    // Callback interface
    public interface ApiCallback<T> {
        void onSuccess(T result);

        void onError(String error);
    }
}