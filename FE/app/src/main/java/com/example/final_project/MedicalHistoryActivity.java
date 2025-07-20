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
        medicalHistoryAdapter = new MedicalHistoryAdapter(medicalHistoryList,
                new MedicalHistoryAdapter.OnMedicalHistoryActionListener() {
                    @Override
                    public void onDelete(MedicalHistory history) {
                        showDeleteConfirm(history);
                    }

                    @Override
                    public void onProcessing(MedicalHistory history) {
                        showProcessingConfirm(history);
                    }

                    @Override
                    public void onCancel(MedicalHistory history) {
                        showCancelConfirm(history);
                    }

                    @Override
                    public void onCompleted(MedicalHistory history) {
                        showCompletedConfirm(history);
                    }
                });
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
            // Use method with userId parameter
            call = apiService.getMedicalHistoryByUserId(userId);
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
                    medicalHistoryList.clear();
                    medicalHistoryList.addAll(response.body());
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

    // DIALOG CONFIRMATION METHODS
    private void showDeleteConfirm(MedicalHistory history) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa bản ghi này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteMedicalHistory(history))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showProcessingConfirm(MedicalHistory history) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận xử lý")
                .setMessage("Bạn có chắc muốn xử lý bản ghi này?")
                .setPositiveButton("Xử lý", (dialog, which) -> processingMedicalHistory(history))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showCancelConfirm(MedicalHistory history) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có chắc muốn hủy bản ghi này?")
                .setPositiveButton("Hủy bản ghi", (dialog, which) -> cancelMedicalHistory(history))
                .setNegativeButton("Không", null)
                .show();
    }

    private void showCompletedConfirm(MedicalHistory history) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận hoàn thành")
                .setMessage("Bạn có chắc muốn đánh dấu bản ghi này là hoàn thành?")
                .setPositiveButton("Hoàn thành", (dialog, which) -> completedMedicalHistory(history))
                .setNegativeButton("Hủy", null)
                .show();
    }

    // API CALL METHODS - FULLY IMPLEMENTED
    private void deleteMedicalHistory(MedicalHistory history) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Deleting medical history ID: " + history.getHistoryId());

        apiService.deleteMedicalHistory(history.getHistoryId()).enqueue(new Callback<MedicalHistory>() {
            @Override
            public void onResponse(Call<MedicalHistory> call, Response<MedicalHistory> response) {
                Log.d(TAG, "Delete response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Xóa bản ghi thành công!", Toast.LENGTH_SHORT).show();
                    getMedicalHistory(-1); // Refresh list
                } else {
                    Log.e(TAG, "Delete failed: " + response.code());
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Lỗi xóa bản ghi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MedicalHistory> call, Throwable t) {
                Log.e(TAG, "Delete error: " + t.getMessage());
                Toast.makeText(MedicalHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processingMedicalHistory(MedicalHistory history) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Processing medical history ID: " + history.getHistoryId());

        apiService.processingMedicalHistory(history.getHistoryId()).enqueue(new Callback<MedicalHistory>() {
            @Override
            public void onResponse(Call<MedicalHistory> call, Response<MedicalHistory> response) {
                Log.d(TAG, "Processing response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Xử lý bản ghi thành công!", Toast.LENGTH_SHORT).show();
                    getMedicalHistory(-1); // Refresh list
                } else {
                    Log.e(TAG, "Processing failed: " + response.code());
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Lỗi xử lý bản ghi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MedicalHistory> call, Throwable t) {
                Log.e(TAG, "Processing error: " + t.getMessage());
                Toast.makeText(MedicalHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelMedicalHistory(MedicalHistory history) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Cancelling medical history ID: " + history.getHistoryId());

        apiService.cancelMedicalHistory(history.getHistoryId()).enqueue(new Callback<MedicalHistory>() {
            @Override
            public void onResponse(Call<MedicalHistory> call, Response<MedicalHistory> response) {
                Log.d(TAG, "Cancel response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Hủy bản ghi thành công!", Toast.LENGTH_SHORT).show();
                    getMedicalHistory(-1); // Refresh list
                } else {
                    Log.e(TAG, "Cancel failed: " + response.code());
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Lỗi hủy bản ghi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MedicalHistory> call, Throwable t) {
                Log.e(TAG, "Cancel error: " + t.getMessage());
                Toast.makeText(MedicalHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completedMedicalHistory(MedicalHistory history) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Completing medical history ID: " + history.getHistoryId());

        apiService.completedMedicalHistory(history.getHistoryId()).enqueue(new Callback<MedicalHistory>() {
            @Override
            public void onResponse(Call<MedicalHistory> call, Response<MedicalHistory> response) {
                Log.d(TAG, "Completed response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Đánh dấu hoàn thành thành công!", Toast.LENGTH_SHORT).show();
                    getMedicalHistory(-1); // Refresh list
                } else {
                    Log.e(TAG, "Completed failed: " + response.code());
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Lỗi đánh dấu hoàn thành: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MedicalHistory> call, Throwable t) {
                Log.e(TAG, "Completed error: " + t.getMessage());
                Toast.makeText(MedicalHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}