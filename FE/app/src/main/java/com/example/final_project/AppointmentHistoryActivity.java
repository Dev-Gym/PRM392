package com.example.final_project;

import android.os.Bundle;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.Button;

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
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Log.e("API", "Get appointments error: " + t.getMessage());
            }
        });
    }

    private void showEditDialog(Appointment appointment) {
        BookingDialog dialog = new BookingDialog(this, appointment.getExpertId(), null, success -> {
            if (success)
                getAppointments(-1);
        }, appointment); // <-- phải truyền appointment vào đây!
        dialog.show();
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
                    getAppointments(-1);
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e("API", "Delete appointment error: " + t.getMessage());
            }
        });
    }
}