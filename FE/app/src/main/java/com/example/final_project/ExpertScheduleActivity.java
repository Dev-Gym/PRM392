package com.example.final_project;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.ExpertSchedule;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.TextView;

public class ExpertScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ExpertScheduleActivity";

    private int expertId;
    private String expertName;
    private RecyclerView rvSchedules;
    private ExpertScheduleAdapter scheduleAdapter;
    private List<ExpertSchedule> scheduleList = new ArrayList<>();
    private TextView tvExpertName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_schedule);

        initViews();
        getIntentData();
        setupRecyclerView();
        loadExpertSchedules();
    }

    private void initViews() {
        tvExpertName = findViewById(R.id.tvExpertName);
        rvSchedules = findViewById(R.id.rvSchedules);
    }

    private void getIntentData() {
        expertId = getIntent().getIntExtra("expertId", -1);
        expertName = getIntent().getStringExtra("expertName");

        Log.d(TAG, "ExpertId: " + expertId + ", ExpertName: " + expertName);

        if (expertName != null) {
            tvExpertName.setText("Lịch làm việc của " + expertName);
            setTitle("Lịch làm việc - " + expertName);
        } else {
            tvExpertName.setText("Lịch làm việc");
            setTitle("Lịch làm việc");
        }
    }

    private void setupRecyclerView() {
        scheduleAdapter = new ExpertScheduleAdapter(scheduleList);
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        rvSchedules.setAdapter(scheduleAdapter);
    }

    private void loadExpertSchedules() {
        if (expertId == -1) {
            Log.e(TAG, "Invalid expertId");
            return;
        }

        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Loading schedules for expertId: " + expertId);

        apiService.getExpertSchedules(expertId).enqueue(new Callback<List<ExpertSchedule>>() {
            @Override
            public void onResponse(Call<List<ExpertSchedule>> call, Response<List<ExpertSchedule>> response) {
                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    scheduleList.clear();
                    scheduleList.addAll(response.body());
                    scheduleAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + scheduleList.size() + " schedules");
                    for (ExpertSchedule schedule : scheduleList) {
                        Log.d(TAG, "Schedule: " + schedule.getDayOfWeek() + " - " + schedule.getWorkingHours());
                    }
                } else {
                    Log.e(TAG, "Get expert schedules failed: " + response.code());
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
            public void onFailure(Call<List<ExpertSchedule>> call, Throwable t) {
                Log.e(TAG, "Get expert schedules error: " + t.getMessage(), t);
            }
        });
    }
}
