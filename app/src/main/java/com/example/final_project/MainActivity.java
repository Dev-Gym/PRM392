package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private String token; // Lấy token sau khi đăng nhập hoặc hardcode demo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Lấy token thực tế sau khi đăng nhập
        token = "Bearer ...";

        Button btnDoctors = findViewById(R.id.btnDoctors);
        Button btnHistory = findViewById(R.id.btnHistory);
        Button btnSchedule = findViewById(R.id.btnSchedule);

        btnDoctors.setOnClickListener(v -> {
            Intent intent = new Intent(this, MedicalExpertActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppointmentHistoryActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScheduleActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });
    }
}