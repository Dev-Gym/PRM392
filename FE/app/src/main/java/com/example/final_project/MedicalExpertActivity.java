package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.MedicalExpert;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.final_project.model.User;

public class MedicalExpertActivity extends AppCompatActivity {
    private String token;
    private RecyclerView rvDoctors;
    private MedicalExpertAdapter doctorAdapter;
    private List<MedicalExpert> doctorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_expert);

        rvDoctors = findViewById(R.id.rvDoctors);
        doctorAdapter = new MedicalExpertAdapter(doctorList, doctor -> {
            // Khi click vào bác sĩ, mở DoctorDetailActivity và truyền doctorId
            Intent intent = new Intent(MedicalExpertActivity.this, DoctorDetailActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("doctorId", doctor.getId()); // sẽ sửa lại bên dưới khi map
            startActivity(intent);
        });
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        rvDoctors.setAdapter(doctorAdapter);

        token = getIntent().getStringExtra("token");
        getDoctors();
    }

    private void getDoctors() {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.getAllExperts().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    doctorList.clear();
                    for (User user : response.body()) {
                        // Map User sang MedicalExpert đúng trường mới
                        MedicalExpert expert = new MedicalExpert();
                        expert.setId(user.getUserId());
                        expert.setName(user.getFullName());
                        expert.setSpecialty(user.getUserType()); // hoặc trường khác nếu có
                        doctorList.add(expert);
                    }
                    doctorAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Get doctors failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("API", "Get doctors error: " + t.getMessage());
            }
        });
    }
}