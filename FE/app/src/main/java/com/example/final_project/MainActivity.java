package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

    private TextView tvWelcome, tvUserInfo;
    private Button btnDoctors, btnHistory, btnSchedule, btnAllSchedules, btnLogout;

    // User info
    private String currentUserType;
    private String currentUserName;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is logged in
        if (!LoginActivity.isUserLoggedIn(this)) {
            goToLoginActivity();
            return;
        }

        apiService = RetrofitClient.getInstance();

        initViews();
        setupUserInfo();
        setupUIBasedOnUserType(); // NEW: Setup UI based on user type
        setupClickListeners();

        // Test API calls
      //  testApiCalls();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserInfo = findViewById(R.id.tvUserInfo);
        btnDoctors = findViewById(R.id.btnDoctors);
        btnHistory = findViewById(R.id.btnHistory);
        btnSchedule = findViewById(R.id.btnSchedule);
        btnAllSchedules = findViewById(R.id.btnAllSchedules);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupUserInfo() {
        currentUserName = LoginActivity.getCurrentUserName(this);
        currentUserType = LoginActivity.getCurrentUserType(this);
        String userEmail = LoginActivity.getCurrentUserEmail(this);
        currentUserId = LoginActivity.getCurrentUserId(this);

        tvWelcome.setText("Chào mừng, " + currentUserName + "!");
        tvUserInfo.setText("ID: " + currentUserId + " | " + currentUserType + "\n" + userEmail);

        Log.d(TAG, "User info - ID: " + currentUserId + ", Name: " + currentUserName + ", Type: " + currentUserType);
    }

    /**
     * NEW METHOD: Setup UI components based on user type
     */
    private void setupUIBasedOnUserType() {
        Log.d(TAG, "Setting up UI for user type: " + currentUserType);

        if ("Patient".equals(currentUserType)) {
            // PATIENT: Disable "Xem Tất Cả Lịch Làm Việc" button
            btnAllSchedules.setEnabled(false);
            btnAllSchedules.setVisibility(View.GONE); // Hide completely
            Log.d(TAG, "Patient: Disabled All Schedules button");

            // Patient can see all other buttons
            btnDoctors.setEnabled(true);
            btnHistory.setEnabled(true);
            btnSchedule.setEnabled(true);
            btnLogout.setEnabled(true);

            // Update button text for Patient context
            btnSchedule.setText("Lịch Cá Nhân (Khám bệnh)");

        } else if ("MedicalExpert".equals(currentUserType)) {
            // MEDICAL EXPERT: Disable "Xem Danh Sách Bác Sĩ" button
            btnDoctors.setEnabled(false);
            btnDoctors.setVisibility(View.GONE); // Hide completely
            Log.d(TAG, "MedicalExpert: Disabled Doctors button");

            // Medical Expert can see all other buttons
            btnHistory.setEnabled(true);
            btnSchedule.setEnabled(true);
            btnAllSchedules.setEnabled(true);
            btnLogout.setEnabled(true);

            // Update button text for MedicalExpert context
            btnHistory.setText("Quản Lý Lịch Hẹn");
            btnSchedule.setText("Lịch Cá Nhân (Khám bệnh)");
            btnAllSchedules.setText("Quản Lý Lịch Làm Việc");

        } else if ("Admin".equals(currentUserType)) {
            // ADMIN: Can see all buttons
            btnDoctors.setEnabled(true);
            btnHistory.setEnabled(true);
            btnSchedule.setEnabled(true);
            btnAllSchedules.setEnabled(true);
            btnLogout.setEnabled(true);

            // Update button text for Admin context
            btnHistory.setText("Quản Lý Tất Cả Lịch Hẹn");
            btnSchedule.setText("Lịch Sử Y Tế");
            btnAllSchedules.setText("Quản Lý Lịch Làm Việc");

            Log.d(TAG, "Admin: All buttons enabled");

        } else {
            // UNKNOWN USER TYPE: Enable all buttons as fallback
            Log.w(TAG, "Unknown user type: " + currentUserType + ". Enabling all buttons.");
            btnDoctors.setEnabled(true);
            btnHistory.setEnabled(true);
            btnSchedule.setEnabled(true);
            btnAllSchedules.setEnabled(true);
            btnLogout.setEnabled(true);
        }

        // Visual feedback for disabled buttons
        updateButtonAppearance();
    }

    /**
     * Update button appearance based on enabled/disabled state
     */
    private void updateButtonAppearance() {
        // For disabled/hidden buttons, also set visual feedback
        if (!btnDoctors.isEnabled() || btnDoctors.getVisibility() == View.GONE) {
            btnDoctors.setAlpha(0.5f);
        } else {
            btnDoctors.setAlpha(1.0f);
        }

        if (!btnAllSchedules.isEnabled() || btnAllSchedules.getVisibility() == View.GONE) {
            btnAllSchedules.setAlpha(0.5f);
        } else {
            btnAllSchedules.setAlpha(1.0f);
        }

        // Always ensure logout is visible for all user types
        btnLogout.setVisibility(View.VISIBLE);
        btnLogout.setAlpha(1.0f);
    }

    private void setupClickListeners() {
        btnDoctors.setOnClickListener(v -> {
            if (btnDoctors.isEnabled()) {
                Intent intent = new Intent(MainActivity.this, MedicalExpertActivity.class);
                intent.putExtra("token", "Bearer test-token");
                startActivity(intent);
            } else {
                showAccessDeniedMessage("Chức năng này chỉ dành cho bệnh nhân");
            }
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AppointmentHistoryActivity.class);
            intent.putExtra("token", "Bearer test-token");
            startActivity(intent);
        });

        // Medical History button
        btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MedicalHistoryActivity.class);
            startActivity(intent);
        });

        // All Schedules button
        btnAllSchedules.setOnClickListener(v -> {
            if (btnAllSchedules.isEnabled()) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                intent.putExtra("token", "Bearer test-token");
                startActivity(intent);
            } else {
                showAccessDeniedMessage("Chức năng này chỉ dành cho nhân viên y tế và quản trị viên");
            }
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        LoginActivity.logout(this);
                        goToLoginActivity();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    /**
     * Show access denied message for disabled features
     */
    private void showAccessDeniedMessage(String message) {
        Toast.makeText(this, "Không có quyền truy cập: " + message, Toast.LENGTH_LONG).show();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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