package com.example.final_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private String token;
    private RecyclerView rvSchedules;
    private ScheduleAdapter scheduleAdapter;
    private List<Schedule> scheduleList = new ArrayList<>();
    private Button btnAddSchedule, btnEditSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        rvSchedules = findViewById(R.id.rvSchedules);
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        rvSchedules.setAdapter(scheduleAdapter);

        btnAddSchedule = findViewById(R.id.btnAddSchedule);
        btnEditSchedule = findViewById(R.id.btnEditSchedule);

        token = getIntent().getStringExtra("token");
        getSchedules();

        btnAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Demo: Thêm mới schedule mẫu
                Schedule newSchedule = new Schedule();
                newSchedule.setDoctorId(1);
                newSchedule.setDate("2024-07-01");
                newSchedule.setTime("08:00");
                createSchedule(newSchedule);
            }
        });

        btnEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Demo: Sửa schedule đầu tiên nếu có
                if (!scheduleList.isEmpty()) {
                    Schedule edit = scheduleList.get(0);
                    edit.setTime("09:00");
                    updateSchedule(edit.getId(), edit);
                }
            }
        });
    }

    private void getSchedules() {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.getSchedules(token).enqueue(new Callback<List<Schedule>>() {
            @Override
            public void onResponse(Call<List<Schedule>> call, Response<List<Schedule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    scheduleList.clear();
                    scheduleList.addAll(response.body());
                    scheduleAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Get schedules failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Schedule>> call, Throwable t) {
                Log.e("API", "Get schedules error: " + t.getMessage());
            }
        });
    }

    private void createSchedule(Schedule newSchedule) {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.createSchedule(token, newSchedule).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                if (response.isSuccessful() && response.body() != null) {
                    scheduleList.add(response.body());
                    scheduleAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Create schedule failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Log.e("API", "Create schedule error: " + t.getMessage());
            }
        });
    }

    private void updateSchedule(int id, Schedule updatedSchedule) {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.updateSchedule(token, id, updatedSchedule).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < scheduleList.size(); i++) {
                        if (scheduleList.get(i).getId() == id) {
                            scheduleList.set(i, response.body());
                            break;
                        }
                    }
                    scheduleAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Update schedule failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Log.e("API", "Update schedule error: " + t.getMessage());
            }
        });
    }

    private void deleteSchedule(int id) {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.deleteSchedule(token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < scheduleList.size(); i++) {
                        if (scheduleList.get(i).getId() == id) {
                            scheduleList.remove(i);
                            break;
                        }
                    }
                    scheduleAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Delete schedule failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Delete schedule error: " + t.getMessage());
            }
        });
    }
}