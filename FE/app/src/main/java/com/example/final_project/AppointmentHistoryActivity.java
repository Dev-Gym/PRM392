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
    private String token;
    private RecyclerView rvAppointments;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        setTitle("Lịch sử người");

        rvAppointments = findViewById(R.id.rvAppointments);
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

                    // ADD THESE NEW METHODS FOR CONFIRM/CANCEL
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

        EditText edtUserId = findViewById(R.id.edtUserId);
        Button btnFilter = findViewById(R.id.btnFilter);

        btnFilter.setOnClickListener(v -> {
            String userIdStr = edtUserId.getText().toString().trim();
            int userId = -1;
            if (!userIdStr.isEmpty()) {
                try {
                    userId = Integer.parseInt(userIdStr);
                } catch (NumberFormatException e) {
                    userId = -1;
                }
            }
            getAppointments(userId);
        });

        // Mặc định load tất cả lịch sử khi vào màn hình
        getAppointments(-1);
    }

    private void getAppointments(int userId) {
        ApiService apiService = RetrofitClient.getInstance();
        Call<List<Appointment>> call;
        if (userId > 0) {
            call = apiService.getAppointmentsByUserId(userId);
        } else {
            call = apiService.getAppointments();
        }
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appointmentList.clear();
                    appointmentList.addAll(response.body());
                    appointmentAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Get appointments failed: " + response.code());
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Lỗi tải danh sách lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Log.e("API", "Get appointments error: " + t.getMessage());
                Toast.makeText(AppointmentHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(Appointment appointment) {
        // Load full appointment data trước khi show dialog
        ApiService apiService = RetrofitClient.getInstance();
        Log.d("API", "Loading appointment details for ID: " + appointment.getAppointmentId());

        apiService.getAppointment(appointment.getAppointmentId()).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Appointment fullAppointment = response.body();
                    Log.d("API", "Full appointment loaded: " + fullAppointment.getStartDate());

                    BookingDialog dialog = new BookingDialog(
                            AppointmentHistoryActivity.this,
                            fullAppointment.getExpertId(),
                            null,
                            success -> {
                                if (success) {
                                    Toast.makeText(AppointmentHistoryActivity.this,
                                            "Cập nhật lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                                    getAppointments(-1); // Refresh list
                                }
                            },
                            fullAppointment // Pass full appointment với startDate
                    );
                    dialog.show();
                } else {
                    Log.e("API", "Get appointment failed: " + response.code());
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Lỗi tải thông tin lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e("API", "Get appointment error: " + t.getMessage());
                Toast.makeText(AppointmentHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ADD NEW METHOD FOR CONFIRM APPOINTMENT
    private void showConfirmDialog(Appointment appointment) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận lịch hẹn")
                .setMessage("Bạn có chắc muốn xác nhận lịch hẹn này?")
                .setPositiveButton("Xác nhận", (dialog, which) -> confirmAppointment(appointment))
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ADD NEW METHOD FOR CANCEL APPOINTMENT
    private void showCancelDialog(Appointment appointment) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Hủy lịch hẹn")
                .setMessage("Bạn có chắc muốn hủy lịch hẹn này?")
                .setPositiveButton("Hủy lịch", (dialog, which) -> cancelAppointment(appointment))
                .setNegativeButton("Không", null)
                .show();
    }

    // ADD API CALL FOR CONFIRM APPOINTMENT
    private void confirmAppointment(Appointment appointment) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d("API", "Confirming appointmentId: " + appointment.getAppointmentId());

        apiService.confirmAppointment(appointment.getAppointmentId()).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                Log.d("API", "Confirm response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Xác nhận lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                    getAppointments(-1); // Refresh list
                } else {
                    Log.e("API", "Confirm failed: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("API", "Confirm error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("API", "Error reading errorBody", e);
                        }
                    }
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Lỗi xác nhận lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e("API", "Confirm appointment error: " + t.getMessage());
                Toast.makeText(AppointmentHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ADD API CALL FOR CANCEL APPOINTMENT
    private void cancelAppointment(Appointment appointment) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d("API", "Cancelling appointmentId: " + appointment.getAppointmentId());

        apiService.cancelAppointment(appointment.getAppointmentId()).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                Log.d("API", "Cancel response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Hủy lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                    getAppointments(-1); // Refresh list
                } else {
                    Log.e("API", "Cancel failed: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("API", "Cancel error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("API", "Error reading errorBody", e);
                        }
                    }
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Lỗi hủy lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e("API", "Cancel appointment error: " + t.getMessage());
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
        Log.d("API", "Delete appointmentId: " + appointment.getAppointmentId());

        apiService.deleteAppointment(appointment.getAppointmentId()).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                Log.d("API", "Delete response code: " + response.code());

                if (!response.isSuccessful() && response.errorBody() != null) {
                    try {
                        Log.e("API", "Delete error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("API", "Error reading errorBody", e);
                    }
                }

                if (response.isSuccessful()) {
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Xóa lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                    getAppointments(-1); // Refresh list
                } else {
                    Toast.makeText(AppointmentHistoryActivity.this,
                            "Lỗi xóa lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e("API", "Delete appointment error: " + t.getMessage());
                Toast.makeText(AppointmentHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}