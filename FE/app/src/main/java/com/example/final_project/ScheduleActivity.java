package com.example.final_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.Schedule;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";

    private String token;
    private RecyclerView rvSchedules;
    private ScheduleAdapter scheduleAdapter;
    private List<Schedule> scheduleList = new ArrayList<>();
    private Button btnAddSchedule, btnEditSchedule, btnDeleteSchedule;
    private Schedule selectedSchedule = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        setTitle("Tất Cả Lịch Làm Việc");

        initViews();
        setupRecyclerView();
        setupClickListeners();

        token = getIntent().getStringExtra("token");

        // Load all schedules when activity opens
        getSchedules();
    }

    private void initViews() {
        rvSchedules = findViewById(R.id.rvSchedules);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
        btnEditSchedule = findViewById(R.id.btnEditSchedule);
        btnDeleteSchedule = findViewById(R.id.btnDeleteSchedule); // New delete button
    }

    private void setupRecyclerView() {
        scheduleAdapter = new ScheduleAdapter(scheduleList, new ScheduleAdapter.OnScheduleClickListener() {
            @Override
            public void onScheduleClick(Schedule schedule, int position) {
                selectedSchedule = schedule;
                Log.d(TAG, "Selected schedule: " + schedule.getScheduleId() + " at position " + position);
                Toast.makeText(ScheduleActivity.this,
                        "Đã chọn lịch: " + schedule.getScheduleId(),
                        Toast.LENGTH_SHORT).show();
                updateButtonStates();
            }

            @Override
            public void onScheduleLongClick(Schedule schedule, int position) {
                selectedSchedule = schedule;
                scheduleAdapter.setSelectedPosition(position);

                // Show options dialog on long click
                showScheduleOptionsDialog(schedule);
            }
        });

        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        rvSchedules.setAdapter(scheduleAdapter);
    }

    private void setupClickListeners() {
        btnAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show create schedule dialog - sử dụng ScheduleRequest DTO
                showCreateScheduleDialog();
            }
        });

        btnEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSchedule != null) {
                    // TODO: Implement edit dialog
                    Toast.makeText(ScheduleActivity.this,
                            "Sửa lịch ID: " + selectedSchedule.getScheduleId(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScheduleActivity.this,
                            "Vui lòng chọn một lịch để sửa",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDeleteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSchedule != null) {
                    showDeleteConfirmDialog(selectedSchedule);
                } else {
                    Toast.makeText(ScheduleActivity.this,
                            "Vui lòng chọn một lịch để xóa",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateButtonStates() {
        boolean hasSelection = selectedSchedule != null;
        btnEditSchedule.setEnabled(hasSelection);
        btnDeleteSchedule.setEnabled(hasSelection);

        // Change button appearance
        btnEditSchedule.setAlpha(hasSelection ? 1.0f : 0.5f);
        btnDeleteSchedule.setAlpha(hasSelection ? 1.0f : 0.5f);
    }

    private void showScheduleOptionsDialog(Schedule schedule) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Tùy chọn lịch làm việc");
        builder.setMessage("Lịch ID: " + schedule.getScheduleId() +
                "\nNgày: " + getDayOfWeekInVietnamese(schedule.getDayOfWeek()) +
                "\nTrạng thái: " + (schedule.isActive() ? "Hoạt động" : "Không hoạt động"));

        builder.setPositiveButton("Sửa", (dialog, which) -> {
            // TODO: Implement edit functionality
            Toast.makeText(this, "Chức năng sửa đang phát triển", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Xóa", (dialog, which) -> {
            showDeleteConfirmDialog(schedule);
        });

        builder.setNeutralButton("Hủy", null);
        builder.show();
    }

    private void showDeleteConfirmDialog(Schedule schedule) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa lịch làm việc này?\n\n" +
                        "ID: " + schedule.getScheduleId() +
                        "\nNgày: " + getDayOfWeekInVietnamese(schedule.getDayOfWeek()))
                .setPositiveButton("Xóa", (dialog, which) -> deleteSchedule(schedule.getScheduleId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String getDayOfWeekInVietnamese(String dayOfWeek) {
        if (dayOfWeek == null) return "Không rõ";

        switch (dayOfWeek.toLowerCase()) {
            case "monday": return "Thứ Hai";
            case "tuesday": return "Thứ Ba";
            case "wednesday": return "Thứ Tư";
            case "thursday": return "Thứ Năm";
            case "friday": return "Thứ Sáu";
            case "saturday": return "Thứ Bảy";
            case "sunday": return "Chủ Nhật";
            default: return dayOfWeek;
        }
    }

    private void showCreateScheduleDialog() {
        CreateScheduleDialog dialog = new CreateScheduleDialog(this,
                new CreateScheduleDialog.CreateScheduleCallback() {
                    @Override
                    public void onScheduleCreated(boolean success) {
                        if (success) {
                            Log.d(TAG, "Schedule created successfully, refreshing list");
                            getSchedules(); // Refresh the list
                            clearSelection(); // Clear selection after refresh
                        }
                    }
                });
        dialog.show();
    }

    private void clearSelection() {
        selectedSchedule = null;
        scheduleAdapter.setSelectedPosition(-1);
        updateButtonStates();
    }

    private void getSchedules() {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Loading all schedules...");

        // Call API without expertId parameter to get all schedules
        apiService.getSchedules().enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(Call<List<Schedule>> call, Response<List<Schedule>> response) {
                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    scheduleList.clear();
                    scheduleList.addAll(response.body());
                    scheduleAdapter.notifyDataSetChanged();
                    clearSelection(); // Clear selection when data refreshes

                    Log.d(TAG, "Loaded " + scheduleList.size() + " schedules");
                    Toast.makeText(ScheduleActivity.this,
                            "Tải được " + scheduleList.size() + " lịch làm việc",
                            Toast.LENGTH_SHORT).show();

                    // Log schedule details for debugging
                    for (Schedule schedule : scheduleList) {
                        Log.d(TAG, "Schedule: ID=" + schedule.getScheduleId() +
                                ", ExpertID=" + schedule.getExpertId() +
                                ", DayOfWeek=" + schedule.getDayOfWeek() +
                                ", StartDate=" + schedule.getStartDate() +
                                ", EndDate=" + schedule.getEndDate() +
                                ", Active=" + schedule.isActive());
                    }
                } else {
                    Log.e(TAG, "Get schedules failed: " + response.code());
                    Toast.makeText(ScheduleActivity.this,
                            "Lỗi tải lịch làm việc: " + response.code(),
                            Toast.LENGTH_SHORT).show();

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
            public void onFailure(Call<List<Schedule>> call, Throwable t) {
                Log.e(TAG, "Get schedules error: " + t.getMessage(), t);
                Toast.makeText(ScheduleActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSchedule(int id, Schedule updatedSchedule) {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.updateSchedule(id, updatedSchedule).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < scheduleList.size(); i++) {
                        if (scheduleList.get(i).getScheduleId() == id) {
                            scheduleList.set(i, response.body());
                            break;
                        }
                    }
                    scheduleAdapter.notifyDataSetChanged();
                    Toast.makeText(ScheduleActivity.this, "Cập nhật lịch thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Update schedule failed: " + response.code());
                    Toast.makeText(ScheduleActivity.this, "Lỗi cập nhật lịch: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Log.e(TAG, "Update schedule error: " + t.getMessage());
                Toast.makeText(ScheduleActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSchedule(int id) {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.deleteSchedule(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Remove from list and update UI
                    for (int i = 0; i < scheduleList.size(); i++) {
                        if (scheduleList.get(i).getScheduleId() == id) {
                            scheduleList.remove(i);
                            break;
                        }
                    }
                    scheduleAdapter.notifyDataSetChanged();
                    clearSelection(); // Clear selection after delete
                    Toast.makeText(ScheduleActivity.this, "Xóa lịch thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Delete schedule failed: " + response.code());
                    Toast.makeText(ScheduleActivity.this, "Lỗi xóa lịch: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Delete schedule error: " + t.getMessage());
                Toast.makeText(ScheduleActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}