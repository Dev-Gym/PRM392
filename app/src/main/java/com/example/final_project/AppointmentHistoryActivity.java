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

public class AppointmentHistoryActivity extends AppCompatActivity {
    private String token;
    private RecyclerView rvAppointments;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        rvAppointments = findViewById(R.id.rvAppointments); // Sửa lại đúng id RecyclerView
        appointmentAdapter = new AppointmentAdapter(appointmentList);
        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        rvAppointments.setAdapter(appointmentAdapter);

        token = getIntent().getStringExtra("token");
        getAppointments();
    }

    private void getAppointments() {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.getAppointments(token).enqueue(new Callback<List<Appointment>>() {
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
}