package com.example.final_project;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.final_project.model.Schedule;
import com.example.final_project.model.ScheduleRequest;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateScheduleDialog extends Dialog {
    private static final String TAG = "CreateScheduleDialog";

    // UI Components
    private EditText edtExpertId, edtHour;
    private TextView tvSelectedDate, tvStartTime, tvEndTime;
    private CheckBox cbIsActive;
    private Button btnSelectDate, btnSelectStartTime, btnSelectEndTime, btnCreate, btnCancel;

    // Data
    private String selectedDate = "";
    private String startTime = "";
    private String endTime = "";
    private CreateScheduleCallback callback;

    public interface CreateScheduleCallback {
        void onScheduleCreated(boolean success);
    }

    public CreateScheduleDialog(Context context, CreateScheduleCallback callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_schedule);

        if (getWindow() != null) {
            getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        edtExpertId = findViewById(R.id.edtExpertId);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        edtHour = findViewById(R.id.edtHour);
        cbIsActive = findViewById(R.id.cbIsActive);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime);
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);

        // Set default values
        edtExpertId.setText("3"); // Default expert ID
        cbIsActive.setChecked(true); // Default to active
    }

    private void setupClickListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());
        btnSelectStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        btnSelectEndTime.setOnClickListener(v -> showTimePickerDialog(false));
        btnCreate.setOnClickListener(v -> createSchedule());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                getContext(),
                (android.widget.DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Format: yyyy-MM-dd
                    selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);

                    tvSelectedDate.setText("Ngày đã chọn: " + selectedDate);
                    btnSelectDate.setText("Đổi ngày");

                    Log.d(TAG, "Selected date: " + selectedDate);
                },
                year,
                month,
                day
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.setTitle("Chọn ngày làm việc");
        datePickerDialog.show();
    }

    private void showTimePickerDialog(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);

                    if (isStartTime) {
                        startTime = time;
                        tvStartTime.setText("Giờ bắt đầu: " + time);
                        btnSelectStartTime.setText("Đổi giờ bắt đầu");
                    } else {
                        endTime = time;
                        tvEndTime.setText("Giờ kết thúc: " + time);
                        btnSelectEndTime.setText("Đổi giờ kết thúc");
                    }

                    Log.d(TAG, (isStartTime ? "Start" : "End") + " time selected: " + time);
                },
                hour,
                minute,
                true // 24 hour format
        );

        timePickerDialog.setTitle(isStartTime ? "Chọn giờ bắt đầu" : "Chọn giờ kết thúc");
        timePickerDialog.show();
    }

    private void createSchedule() {
        if (!validateForm()) {
            return;
        }

        try {
            int expertId = Integer.parseInt(edtExpertId.getText().toString().trim());
            boolean isActive = cbIsActive.isChecked();

            // Create datetime strings using selected date and times
            String startDateTime = selectedDate + "T" + startTime + ":00.000Z";
            String endDateTime = selectedDate + "T" + endTime + ":00.000Z";

            // Convert selected date to day of week for API
            String dayOfWeek = getDayOfWeekFromDate(selectedDate);

            // Create ScheduleRequest
            ScheduleRequest request = new ScheduleRequest();
            request.setExpertId(expertId);
            request.setDayOfWeek(dayOfWeek);
            request.setStartDate(startDateTime);
            request.setEndDate(endDateTime);
            request.setActive(isActive);

            Log.d(TAG, "Creating schedule:");
            Log.d(TAG, "- Selected Date: " + selectedDate);
            Log.d(TAG, "- Day of Week: " + dayOfWeek);
            Log.d(TAG, "- Start Time: " + startTime);
            Log.d(TAG, "- End Time: " + endTime);
            Log.d(TAG, "- Start DateTime: " + startDateTime);
            Log.d(TAG, "- End DateTime: " + endDateTime);
            Log.d(TAG, "- ExpertId: " + request.getExpertId());

            createScheduleWithRequest(request);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Expert ID phải là số", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error creating schedule", e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm() {
        String expertIdText = edtExpertId.getText().toString().trim();
        if (expertIdText.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập Expert ID", Toast.LENGTH_SHORT).show();
            edtExpertId.requestFocus();
            return false;
        }

        try {
            Integer.parseInt(expertIdText);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Expert ID phải là số", Toast.LENGTH_SHORT).show();
            edtExpertId.requestFocus();
            return false;
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ngày làm việc", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (startTime.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn giờ bắt đầu", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endTime.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn giờ kết thúc", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate that end time is after start time
        if (!isEndTimeAfterStartTime()) {
            Toast.makeText(getContext(), "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isEndTimeAfterStartTime() {
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            int startTotalMinutes = startHour * 60 + startMinute;
            int endTotalMinutes = endHour * 60 + endMinute;

            return endTotalMinutes > startTotalMinutes;
        } catch (Exception e) {
            Log.e(TAG, "Error comparing times", e);
            return false;
        }
    }

    // Helper method to convert date to day of week
    private String getDayOfWeekFromDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(dateString));

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            switch (dayOfWeek) {
                case Calendar.SUNDAY: return "Sunday";
                case Calendar.MONDAY: return "Monday";
                case Calendar.TUESDAY: return "Tuesday";
                case Calendar.WEDNESDAY: return "Wednesday";
                case Calendar.THURSDAY: return "Thursday";
                case Calendar.FRIDAY: return "Friday";
                case Calendar.SATURDAY: return "Saturday";
                default: return "Monday";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + dateString, e);
            return "Monday"; // Default fallback
        }
    }

    private void createScheduleWithRequest(ScheduleRequest request) {
        Log.d(TAG, "Calling create schedule API with ScheduleRequest");
        Log.d(TAG, "Request: " + request.toString());

        ApiService apiService = RetrofitClient.getInstance();
        apiService.createScheduleWithRequest(request).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                Log.d(TAG, "Create schedule response code: " + response.code());
                if (response.isSuccessful()) {
                    Log.d(TAG, "Schedule created successfully");
                    Toast.makeText(getContext(), "Tạo lịch thành công!", Toast.LENGTH_SHORT).show();
                    callback.onScheduleCreated(true);
                    dismiss();
                } else {
                    Log.e(TAG, "Create schedule failed: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Toast.makeText(getContext(), "Lỗi tạo lịch: " + response.code(), Toast.LENGTH_SHORT).show();
                    callback.onScheduleCreated(false);
                }
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Log.e(TAG, "Create schedule network error", t);
                callback.onScheduleCreated(false);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}