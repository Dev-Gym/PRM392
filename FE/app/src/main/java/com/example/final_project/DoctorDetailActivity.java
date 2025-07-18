package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_project.model.MedicalExpert;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.final_project.model.User;

public class DoctorDetailActivity extends AppCompatActivity {
    private int doctorId;
    private String token;
    private TextView tvName, tvSpecialty;
    private Button btnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        tvName = findViewById(R.id.tvDoctorName);
        tvSpecialty = findViewById(R.id.tvDoctorSpecialty);
        btnBook = findViewById(R.id.btnBookAppointment);

        doctorId = getIntent().getIntExtra("doctorId", -1);
        token = getIntent().getStringExtra("token");

        loadDoctorDetail();

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingDialog dialog = new BookingDialog(DoctorDetailActivity.this, doctorId, token,
                        new BookingDialog.BookingCallback() {
                            @Override
                            public void onBookingResult(boolean success) {
                                if (success) {
                                    Toast.makeText(DoctorDetailActivity.this, "Đặt lịch thành công!",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(DoctorDetailActivity.this,
                                            AppointmentHistoryActivity.class);
                                    intent.putExtra("token", token);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(DoctorDetailActivity.this, "Đặt lịch thất bại!", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                dialog.show();
            }
        });
    }

    private void loadDoctorDetail() {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.getAllExperts().enqueue(new Callback<java.util.List<User>>() {
            @Override
            public void onResponse(Call<java.util.List<User>> call,
                    Response<java.util.List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (User user : response.body()) {
                        if (user.getUserId() == doctorId) {
                            tvName.setText(user.getFullName());
                            tvSpecialty.setText(user.getUserType());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<java.util.List<User>> call, Throwable t) {
            }
        });
    }
}