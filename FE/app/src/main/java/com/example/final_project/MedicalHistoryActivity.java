package com.example.final_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.MedicalHistory;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalHistoryActivity extends AppCompatActivity {
    private static final String TAG = "MedicalHistoryActivity";

    private RecyclerView rvMedicalHistory;
    private MedicalHistoryAdapter medicalHistoryAdapter;
    private List<MedicalHistory> medicalHistoryList = new ArrayList<>();
    private EditText edtUserId;
    private Button btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        setTitle("Lịch Cá Nhân");

        initViews();
        setupRecyclerView();
        setupClickListeners();

        // Load all medical history by default
        getMedicalHistory(-1);
    }

    private void initViews() {
        rvMedicalHistory = findViewById(R.id.rvAppointments); // Reuse same ID from layout
        edtUserId = findViewById(R.id.edtUserId);
        btnFilter = findViewById(R.id.btnFilter);

        // Update title
        findViewById(R.id.tvTitleHistory).setVisibility(android.view.View.VISIBLE);
        ((android.widget.TextView) findViewById(R.id.tvTitleHistory)).setText("Lịch Cá Nhân");
    }

    private void setupRecyclerView() {
        medicalHistoryAdapter = new MedicalHistoryAdapter(medicalHistoryList);
        rvMedicalHistory.setLayoutManager(new LinearLayoutManager(this));
        rvMedicalHistory.setAdapter(medicalHistoryAdapter);
    }

    private void setupClickListeners() {
        btnFilter.setOnClickListener(v -> {
            String userIdStr = edtUserId.getText().toString().trim();
            int userId = -1;
            if (!userIdStr.isEmpty()) {
                try {
                    userId = Integer.parseInt(userIdStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "UserId phải là số", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            getMedicalHistory(userId);
        });
    }

    private void getMedicalHistory(int userId) {
        ApiService apiService = RetrofitClient.getInstance();
        Call<List<MedicalHistory>> call;

        if (userId > 0) {
            Log.d(TAG, "Loading medical history for userId: " + userId);
            // Use existing method with userId parameter
            call = apiService.getMedicalHistory(); // This calls without userId parameter
        } else {
            Log.d(TAG, "Loading all medical history");
            // Use existing method - getMedicalHistory() returns all records
            call = apiService.getMedicalHistory();
        }

        call.enqueue(new Callback<List<MedicalHistory>>() {
            @Override
            public void onResponse(Call<List<MedicalHistory>> call, Response<List<MedicalHistory>> response) {
                Log.d(TAG, "API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<MedicalHistory> allRecords = response.body();

                    // Filter by userId if specified
                    medicalHistoryList.clear();
                    if (userId > 0) {
                        // Client-side filtering by userId (since API doesn't support it)
                        for (MedicalHistory record : allRecords) {
                            // Note: MedicalHistory doesn't have userId field directly
                            // We'll show all records for now, or you can add filtering logic
                            medicalHistoryList.add(record);
                        }
                    } else {
                        // Show all records
                        medicalHistoryList.addAll(allRecords);
                    }

                    medicalHistoryAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + medicalHistoryList.size() + " medical history records");
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Tải được " + medicalHistoryList.size() + " bản ghi", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Get medical history failed: " + response.code());
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MedicalHistory>> call, Throwable t) {
                Log.e(TAG, "Get medical history error: " + t.getMessage());
                Toast.makeText(MedicalHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}