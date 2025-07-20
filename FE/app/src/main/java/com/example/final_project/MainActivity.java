package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_project.model.*;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getInstance();

        Button btnDoctors = findViewById(R.id.btnDoctors);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnSchedule = findViewById(R.id.btnSchedule); // ADD THIS LINE

        btnDoctors.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicalExpertActivity.class);
            intent.putExtra("token", "Bearer test-token");
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AppointmentHistoryActivity.class);
            intent.putExtra("token", "Bearer test-token");
            startActivity(intent);
        });

        // ADD CLICK LISTENER FOR MEDICAL HISTORY
        btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicalHistoryActivity.class);
            startActivity(intent);
        });

        // Test API calls
        testApiCalls();
    }

    private void testApiCalls() {
        // Test get medical history
        Log.d(TAG, "Testing Medical History API...");
        apiService.getMedicalHistory().enqueue(new Callback<List<MedicalHistory>>() {
            @Override
            public void onResponse(Call<List<MedicalHistory>> call, Response<List<MedicalHistory>> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Medical history loaded successfully: " + response.body().size() + " records");
                        Toast.makeText(MainActivity.this,
                                        "Medical history loaded: " + response.body().size() + " records", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Log.e(TAG, "Medical history failed: " + response.code());
                        Toast.makeText(MainActivity.this, "Medical history failed: " + response.code(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<MedicalHistory>> call, Throwable t) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Error loading medical history: " + t.getMessage());
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });

        // Test get appointments
        Log.d(TAG, "Testing Appointments API...");
        apiService.getAppointments().enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Appointments loaded successfully: " + response.body().size() + " appointments");
                        Toast.makeText(MainActivity.this,
                                        "Appointments loaded: " + response.body().size() + " appointments", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Log.e(TAG, "Appointments failed: " + response.code());
                        Toast.makeText(MainActivity.this, "Appointments failed: " + response.code(), Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Error loading appointments: " + t.getMessage());
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}