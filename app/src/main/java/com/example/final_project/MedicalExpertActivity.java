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
            intent.putExtra("doctorId", doctor.getId());
            startActivity(intent);
        });
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        rvDoctors.setAdapter(doctorAdapter);

        token = getIntent().getStringExtra("token");
        getDoctors();
    }

    private void getDoctors() {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.getMedicalExperts(token).enqueue(new Callback<List<MedicalExpert>>() {
            @Override
            public void onResponse(Call<List<MedicalExpert>> call, Response<List<MedicalExpert>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    doctorList.clear();
                    doctorList.addAll(response.body());
                    doctorAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Get doctors failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MedicalExpert>> call, Throwable t) {
                Log.e("API", "Get doctors error: " + t.getMessage());
            }
        });
    }
}