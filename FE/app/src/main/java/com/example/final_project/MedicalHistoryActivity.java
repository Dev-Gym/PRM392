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
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        // Get current user info
        currentUserId = LoginActivity.getCurrentUserId(this);
        String currentUserName = LoginActivity.getCurrentUserName(this);

        setTitle("Lịch Cá Nhân - " + currentUserName);

        initViews();
        setupRecyclerView();
        disableFilterControls();

        // Load medical history for current user automatically
        getMedicalHistoryForCurrentUser();
    }

    private void initViews() {
        rvMedicalHistory = findViewById(R.id.rvAppointments); // Reuse same ID from layout
        edtUserId = findViewById(R.id.edtUserId);
        btnFilter = findViewById(R.id.btnFilter);

        // Update title
        findViewById(R.id.tvTitleHistory).setVisibility(android.view.View.VISIBLE);
        ((android.widget.TextView) findViewById(R.id.tvTitleHistory)).setText("Lịch Cá Nhân");
    }

    private void disableFilterControls() {
        // Auto-fill with current user ID and disable editing
        edtUserId.setText(String.valueOf(currentUserId));
        edtUserId.setEnabled(false);
        edtUserId.setAlpha(0.6f);
        edtUserId.setHint("User ID hiện tại: " + currentUserId);

        // Hide filter button since we auto-load user's medical history
        btnFilter.setVisibility(android.view.View.GONE);

        Log.d(TAG, "Filter controls disabled for user ID: " + currentUserId);
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

    private void getMedicalHistoryForCurrentUser() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin user. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Loading medical history for user ID: " + currentUserId);

        apiService.getMedicalHistoryByUserId(currentUserId).enqueue(new Callback<List<MedicalHistory>>() {
            @Override
            public void onResponse(Call<List<MedicalHistory>> call, Response<List<MedicalHistory>> response) {
                Log.d(TAG, "API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    medicalHistoryList.clear();
                    medicalHistoryList.addAll(response.body());
                    medicalHistoryAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + medicalHistoryList.size() + " medical history records for user " + currentUserId);

                    if (medicalHistoryList.isEmpty()) {
                        Toast.makeText(MedicalHistoryActivity.this,
                                "Bạn chưa có lịch sử khám bệnh nào", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MedicalHistoryActivity.this,
                                "Tải được " + medicalHistoryList.size() + " bản ghi", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Get medical history failed: " + response.code());
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();

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
            public void onFailure(Call<List<MedicalHistory>> call, Throwable t) {
                Log.e(TAG, "Get medical history error: " + t.getMessage(), t);
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
        // Create custom dialog với EditText để nhập description
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Hoàn thành khám bệnh");
        builder.setMessage("Nhập mô tả kết quả khám:");

        // Create EditText for description input
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("Nhập mô tả kết quả khám...");
        input.setMinLines(3);
        input.setMaxLines(5);

        // Set padding for better UI
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        builder.setView(input);

        builder.setPositiveButton("Hoàn thành", (dialog, which) -> {
            String description = input.getText().toString().trim();
            if (description.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
                return;
            }
            completedMedicalHistory(history, description);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Focus on EditText and show keyboard
        input.requestFocus();
        android.view.inputmethod.InputMethodManager imm =
                (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(input, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        }
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
                    getMedicalHistoryForCurrentUser(); // Refresh list with current user's data
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
                    getMedicalHistoryForCurrentUser(); // Refresh list
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
                    getMedicalHistoryForCurrentUser(); // Refresh list
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

    private void completedMedicalHistory(MedicalHistory history, String description) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Completing medical history ID: " + history.getHistoryId() + " with description: " + description);

        // Create request object with description
        com.example.final_project.model.MedicalHistoryConfirmRequest request =
                new com.example.final_project.model.MedicalHistoryConfirmRequest(description);

        apiService.completedMedicalHistory(history.getHistoryId(), request).enqueue(new Callback<MedicalHistory>() {
            @Override
            public void onResponse(Call<MedicalHistory> call, Response<MedicalHistory> response) {
                Log.d(TAG, "Completed response code: " + response.code());

                if (response.isSuccessful()) {
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Hoàn thành khám bệnh thành công!", Toast.LENGTH_SHORT).show();
                    getMedicalHistoryForCurrentUser(); // Refresh list
                } else {
                    Log.e(TAG, "Completed failed: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Toast.makeText(MedicalHistoryActivity.this,
                            "Lỗi hoàn thành: " + response.code(), Toast.LENGTH_SHORT).show();
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