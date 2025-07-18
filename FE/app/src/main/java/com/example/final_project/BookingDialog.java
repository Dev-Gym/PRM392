package com.example.final_project;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.final_project.model.Appointment;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;
import android.widget.TextView;

public class BookingDialog extends Dialog {
    private int doctorId;
    private String token;
    private BookingCallback callback;
    private Appointment appointmentToEdit; // null nếu là tạo mới

    public interface BookingCallback {
        void onBookingResult(boolean success);
    }

    public BookingDialog(Context context, int doctorId, String token, BookingCallback callback) {
        this(context, doctorId, token, callback, null);
    }

    public BookingDialog(Context context, int doctorId, String token, BookingCallback callback,
            Appointment appointmentToEdit) {
        super(context);
        this.doctorId = doctorId;
        this.token = token;
        this.callback = callback;
        this.appointmentToEdit = appointmentToEdit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_booking);

        // Set width dialog là MATCH_PARENT
        if (getWindow() != null) {
            getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText edtReq = findViewById(R.id.edtReq);
        EditText edtDate = findViewById(R.id.edtDate);
        EditText edtStatus = findViewById(R.id.edtStatus);
        Button btnBook = findViewById(R.id.btnBook);
        Button btnCancel = findViewById(R.id.btnCancel);
        TextView tvDialogTitle = findViewById(R.id.tvDialogTitle);

        if (appointmentToEdit != null) {
            btnBook.setText("Lưu");
            tvDialogTitle.setText("Sửa lịch hẹn");
            edtReq.setEnabled(false);
            edtReq.setFocusable(false);
            if (appointmentToEdit.getReq() != null && !appointmentToEdit.getReq().isEmpty())
                edtReq.setText(appointmentToEdit.getReq());
            else
                edtReq.setText("Không rõ người đặt");
            if (appointmentToEdit.getStartDate() != null && appointmentToEdit.getStartDate().length() >= 10)
                edtDate.setText(appointmentToEdit.getStartDate().substring(0, 10));
            if (appointmentToEdit.getStatus() != null)
                edtStatus.setText(appointmentToEdit.getStatus());
        } else {
            btnBook.setText("Đặt lịch");
            tvDialogTitle.setText("Đặt lịch mới");
            edtReq.setEnabled(true);
            edtReq.setFocusable(true);
            edtReq.setText("");
        }

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String req = edtReq.getText().toString().trim();
                String dateInput = edtDate.getText().toString().trim(); // "19-07-2025"
                String[] parts = dateInput.split("-");
                String date = parts.length == 3 ? (parts[2] + "-" + parts[1] + "-" + parts[0]) : dateInput; // "2025-07-19"
                String status = edtStatus.getText().toString();
                Appointment appointment = new Appointment();
                appointment.setPatientId(1); // TODO: Lấy patientId thực tế
                appointment.setExpertId(doctorId);
                appointment.setFacilityId(1); // TODO: Lấy facilityId thực tế nếu cần
                appointment.setNote(""); // TODO: Cho phép nhập note nếu muốn
                appointment.setStartDate(date + "T00:00:00.000Z");
                appointment.setEndDate(date + "T00:00:00.000Z");
                appointment.setStatus(status);
                appointment.setReq(req);
                Log.d("API", "Appointment JSON: " + new com.google.gson.Gson().toJson(appointment));
                if (appointmentToEdit != null) {
                    appointment.setReq(appointmentToEdit.getReq()); // Giữ nguyên người đặt lịch khi update
                    updateAppointment(appointment);
                } else {
                    createAppointment(appointment);
                }
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void createAppointment(Appointment appointment) {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.createAppointment(appointment).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                Log.d("API", "Response code: " + response.code());
                if (response.isSuccessful()) {
                    Log.d("API", "Response body: " + new com.google.gson.Gson().toJson(response.body()));
                    if (response.body() != null) {
                        callback.onBookingResult(true);
                        dismiss();
                    } else {
                        Log.e("API", "Response body is null!");
                        callback.onBookingResult(false);
                    }
                } else {
                    Log.e("API", "Create appointment failed: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("API", "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("API", "Error reading errorBody", e);
                        }
                    }
                    callback.onBookingResult(false);
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e("API", "Create appointment error: " + t.getMessage());
                callback.onBookingResult(false);
            }
        });
    }

    private void updateAppointment(Appointment appointment) {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.updateAppointment(appointmentToEdit.getAppointmentId(), appointment)
                .enqueue(new Callback<Appointment>() {
                    @Override
                    public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                        Log.d("API", "Response code: " + response.code());
                        if (response.isSuccessful()) {
                            Log.d("API", "Response body: " + new com.google.gson.Gson().toJson(response.body()));
                            if (response.body() != null) {
                                callback.onBookingResult(true);
                                dismiss();
                            } else {
                                Log.e("API", "Response body is null!");
                                callback.onBookingResult(false);
                            }
                        } else {
                            Log.e("API", "Update appointment failed: " + response.code());
                            if (response.errorBody() != null) {
                                try {
                                    Log.e("API", "Error body: " + response.errorBody().string());
                                } catch (Exception e) {
                                    Log.e("API", "Error reading errorBody", e);
                                }
                            }
                            callback.onBookingResult(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Appointment> call, Throwable t) {
                        Log.e("API", "Update appointment error: " + t.getMessage());
                        callback.onBookingResult(false);
                    }
                });
    }
}