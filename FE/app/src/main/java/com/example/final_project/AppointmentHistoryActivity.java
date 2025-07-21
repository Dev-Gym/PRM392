package com.example.final_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.Appointment;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentHistoryActivity extends AppCompatActivity {
    private static final String TAG = "AppointmentHistoryActivity";

    private String token;
    private RecyclerView rvAppointments;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private EditText edtUserId;
    private Button btnFilter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        // Get current user info
        currentUserId = LoginActivity.getCurrentUserId(this);
        String currentUserName = LoginActivity.getCurrentUserName(this);

        setTitle("Lịch hẹn của " + currentUserName);

        initViews();
        setupRecyclerView();
        disableFilterControls();

        token = getIntent().getStringExtra("token");

        // Load appointments for current user automatically
        getAppointmentsForCurrentUser();
    }

    private void initViews() {
        rvAppointments = findViewById(R.id.rvAppointments);
        edtUserId = findViewById(R.id.edtUserId);
        btnFilter = findViewById(R.id.btnFilter);
    }

    private void disableFilterControls() {
        // Auto-fill with current user ID and disable editing
        edtUserId.setText(String.valueOf(currentUserId));
        edtUserId.setEnabled(false);
        edtUserId.setAlpha(0.6f);
        edtUserId.setHint("User ID hiện tại: " + currentUserId);

        // Hide filter button since we auto-load user's appointments
        btnFilter.setVisibility(android.view.View.GONE);

        Log.d(TAG, "Filter controls disabled for user ID: " + currentUserId);
    }

    private void setupRecyclerView() {
        appointmentAdapter = new AppointmentAdapter(appointmentList,
                new AppointmentAdapter.OnAppointmentActionListener() {
                    @Override
                    public void onEdit(Appointment appointment) {
                        showEditDialog(appointment);
                    }

                    @Override
                    public void onDelete(Appointment appointment) {
                        showDeleteConfirm(appointment);
                    }

                    @Override
                    public void onConfirm(Appointment appointment) {
                        showConfirmDialog(appointment);
                    }

                    @Override
                    public void onCancel(Appointment appointment) {
                        showCancelDialog(appointment);
                    }
                });
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        rvAppointments.setAdapter(appointmentAdapter);
    }

    private void getAppointmentsForCurrentUser() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin user. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Loading appointments for user ID: " + currentUserId);

        apiService.getAppointmentsByUserId(currentUserId).enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                Log.d(TAG, "API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    appointmentList.clear();
                    appointmentList.addAll(response.body());
                    appointmentAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + appointmentList.size() + " appointments for user " + currentUserId);

                    if (appointmentList.isEmpty()) {
                        Toast.makeText(AppointmentHistoryActivity.this,
                                "Bạn chưa có lịch hẹn nào", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AppointmentHistoryActivity.this,
                                "Tải được " + appointmentList.size() + " lịch hẹn", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Get appointments failed: " + response.code());
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Lỗi tải danh sách lịch hẹn: " + response.code(), Toast.LENGTH_SHORT).show();

                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading errorBody", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Log.e(TAG, "Get appointments error: " + t.getMessage(), t);
                Toast.makeText(AppointmentHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(Appointment appointment) {
        // Load full appointment data trước khi show dialog
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Loading appointment details for ID: " + appointment.getAppointmentId());

        apiService.getAppointment(appointment.getAppointmentId()).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Appointment fullAppointment = response.body();
                    Log.d(TAG, "Full appointment loaded: " + fullAppointment.getStartDate());

                    BookingDialog dialog = new BookingDialog(
                            AppointmentHistoryActivity.this,
                            fullAppointment.getExpertId(),
                            null,
                            success -> {
                                if (success) {
                                    Toast.makeText(AppointmentHistoryActivity.this,
                                            "Cập nhật lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                                    getAppointmentsForCurrentUser(); // Refresh list with current user's appointments
                                }
                            },
                            fullAppointment // Pass full appointment với startDate
                    );
                    dialog.show();
                } else {
                    Log.e(TAG, "Get appointment failed: " + response.code());
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Lỗi tải thông tin lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e(TAG, "Get appointment error: " + t.getMessage());
                Toast.makeText(AppointmentHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmDialog(Appointment appointment) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận lịch hẹn")
                .setMessage("Bạn có chắc muốn xác nhận lịch hẹn này?")
                .setPositiveButton("Xác nhận", (dialog, which) -> confirmAppointment(appointment))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showCancelDialog(Appointment appointment) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Hủy lịch hẹn")
                .setMessage("Bạn có chắc muốn hủy lịch hẹn này?")
                .setPositiveButton("Hủy lịch", (dialog, which) -> cancelAppointment(appointment))
                .setNegativeButton("Không", null)
                .show();
    }

    private void confirmAppointment(Appointment appointment) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Confirming appointmentId: " + appointment.getAppointmentId());

        apiService.confirmAppointment(appointment.getAppointmentId()).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                Log.d(TAG, "Confirm response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Xác nhận lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                    getAppointmentsForCurrentUser(); // Refresh list
                } else {
                    Log.e(TAG, "Confirm failed: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Confirm error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading errorBody", e);
                        }
                    }
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Lỗi xác nhận lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e(TAG, "Confirm appointment error: " + t.getMessage());
                Toast.makeText(AppointmentHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelAppointment(Appointment appointment) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Cancelling appointmentId: " + appointment.getAppointmentId());

        apiService.cancelAppointment(appointment.getAppointmentId()).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                Log.d(TAG, "Cancel response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Hủy lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                    getAppointmentsForCurrentUser(); // Refresh list
                } else {
                    Log.e(TAG, "Cancel failed: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Cancel error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading errorBody", e);
                        }
                    }
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Lỗi hủy lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e(TAG, "Cancel appointment error: " + t.getMessage());
                Toast.makeText(AppointmentHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirm(Appointment appointment) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa lịch này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteAppointment(appointment))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteAppointment(Appointment appointment) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Delete appointmentId: " + appointment.getAppointmentId());

        apiService.deleteAppointment(appointment.getAppointmentId()).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                Log.d(TAG, "Delete response code: " + response.code());

                if (!response.isSuccessful() && response.errorBody() != null) {
                    try {
                        Log.e(TAG, "Delete error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody", e);
                    }
                }

                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Xóa lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                    getAppointmentsForCurrentUser(); // Refresh list
                } else {
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Lỗi xóa lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e(TAG, "Delete appointment error: " + t.getMessage());
                Toast.makeText(AppointmentHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}