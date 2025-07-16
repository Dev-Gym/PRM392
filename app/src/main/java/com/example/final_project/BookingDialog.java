package com.example.final_project;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.final_project.model.Appointment;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDialog extends Dialog {
    private int doctorId;
    private String token;
    private BookingCallback callback;

    public interface BookingCallback {
        void onBookingResult(boolean success);
    }

    public BookingDialog(Context context, int doctorId, String token, BookingCallback callback) {
        super(context);
        this.doctorId = doctorId;
        this.token = token;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_booking);

        EditText edtDate = findViewById(R.id.edtDate);
        EditText edtStatus = findViewById(R.id.edtStatus);
        Button btnBook = findViewById(R.id.btnBook);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = edtDate.getText().toString();
                String status = edtStatus.getText().toString();
                Appointment appointment = new Appointment();
                appointment.setDoctorId(doctorId);
                appointment.setDate(date);
                appointment.setStatus(status);
                appointment.setPatientId(1); // TODO: Lấy patientId thực tế
                createAppointment(appointment);
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void createAppointment(Appointment appointment) {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.createAppointment(token, appointment).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onBookingResult(true);
                    dismiss();
                } else {
                    callback.onBookingResult(false);
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                callback.onBookingResult(false);
            }
        });
    }
}