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
    private EditText edtExpertId;
    private TextView tvSelectedDate, tvStartTime, tvEndTime;
    private CheckBox cbIsActive;
    private Button btnSelectDate, btnSelectStartTime, btnSelectEndTime, btnCreate, btnCancel;

    // Data
    private String selectedDate = "";
    private String startTime = "";
    private String endTime = "";
    private CreateScheduleCallback callback;
    private Schedule scheduleToEdit = null;
    private boolean isEditMode = false;

    public interface CreateScheduleCallback {
        void onScheduleCreated(boolean success);
    }

    // Constructor for create mode
    public CreateScheduleDialog(Context context, CreateScheduleCallback callback) {
        super(context);
        this.callback = callback;
        this.isEditMode = false;
    }

    // Constructor for edit mode
    public CreateScheduleDialog(Context context, CreateScheduleCallback callback, Schedule scheduleToEdit) {
        super(context);
        this.callback = callback;
        this.scheduleToEdit = scheduleToEdit;
        this.isEditMode = true;
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

        // If edit mode, populate data
        if (isEditMode && scheduleToEdit != null) {
            populateEditData();
        }
    }


    private void initViews() {
        edtExpertId = findViewById(R.id.edtExpertId);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        cbIsActive = findViewById(R.id.cbIsActive);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime);
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);

        // Auto-fill Expert ID with current logged-in user if they are MedicalExpert
        String userType = LoginActivity.getCurrentUserType(getContext());
        int currentUserId = LoginActivity.getCurrentUserId(getContext());

        if ("MedicalExpert".equals(userType) && currentUserId != -1) {
            edtExpertId.setText(String.valueOf(currentUserId));
            edtExpertId.setEnabled(false); // Disable editing
            edtExpertId.setAlpha(0.7f); // Visual indication it's disabled
            Log.d(TAG, "Auto-filled Expert ID with current user: " + currentUserId);
        } else {
            // Default for other user types or if no user is logged in
            edtExpertId.setText("3");
        }

        cbIsActive.setChecked(true);

        // Update UI based on mode
        TextView tvTitle = findViewById(R.id.tvDialogTitle);
        if (isEditMode) {
            tvTitle.setText("Sửa Lịch Làm Việc");
            btnCreate.setText("Cập Nhật");


            // Disable Expert ID editing in edit mode
            edtExpertId.setEnabled(false);
            edtExpertId.setAlpha(0.6f);

            // Disable date selection in edit mode
            btnSelectDate.setEnabled(false);
            btnSelectDate.setAlpha(0.6f);
            btnSelectDate.setText("Ngày không thể thay đổi");

            // Disable active status changing in edit mode
            cbIsActive.setEnabled(false);
            cbIsActive.setAlpha(0.6f);

            Log.d(TAG, "Edit mode: Disabled Expert ID, Date selection, and Active status");

        } else {
            tvTitle.setText("Tạo Lịch Làm Việc Mới");
            btnCreate.setText("Tạo Lịch");
        }
    }

    private void setupClickListeners() {

        btnSelectDate.setOnClickListener(v -> {
            // Only allow date selection in create mode
            if (!isEditMode) {
                showDatePickerDialog();
            }
        });

        btnSelectStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        btnSelectEndTime.setOnClickListener(v -> showTimePickerDialog(false));


        btnCreate.setOnClickListener(v -> {
            if (isEditMode) {
                updateSchedule();
            } else {
                createSchedule();
            }
        });



        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void populateEditData() {
        if (scheduleToEdit == null) return;

        Log.d(TAG, "Populating edit data for schedule: " + scheduleToEdit.getScheduleId());

        // Set Expert ID
        edtExpertId.setText(String.valueOf(scheduleToEdit.getExpertId()));

        // Set Active status
        cbIsActive.setChecked(scheduleToEdit.isActive());

        // Extract date and times from startDate and endDate
        if (scheduleToEdit.getStartDate() != null && scheduleToEdit.getEndDate() != null) {
            try {
                String startDateTime = scheduleToEdit.getStartDate();
                String endDateTime = scheduleToEdit.getEndDate();

                // Extract date part
                if (startDateTime.contains("T")) {
                    selectedDate = startDateTime.split("T")[0];
                    tvSelectedDate.setText("Ngày đã chọn: " + selectedDate);
                    btnSelectDate.setText("Đổi ngày");
                }

                // Extract start time
                if (startDateTime.contains("T")) {
                    String timeWithSeconds = startDateTime.split("T")[1];
                    startTime = timeWithSeconds.substring(0, 5);
                    tvStartTime.setText("Giờ bắt đầu: " + startTime);
                    btnSelectStartTime.setText("Đổi giờ bắt đầu");
                }

                // Extract end time
                if (endDateTime.contains("T")) {
                    String timeWithSeconds = endDateTime.split("T")[1];
                    endTime = timeWithSeconds.substring(0, 5);
                    tvEndTime.setText("Giờ kết thúc: " + endTime);
                    btnSelectEndTime.setText("Đổi giờ kết thúc");
                }

                Log.d(TAG, "Populated - Date: " + selectedDate + ", Start: " + startTime + ", End: " + endTime);

            } catch (Exception e) {
                Log.e(TAG, "Error parsing schedule dates", e);
                Toast.makeText(getContext(), "Lỗi đọc dữ liệu lịch", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);

                    tvSelectedDate.setText("Ngày đã chọn: " + selectedDate);
                    btnSelectDate.setText("Đổi ngày");

                    Log.d(TAG, "Selected date: " + selectedDate);
                },
                year, month, day
        );

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
                hour, minute, true
        );

        timePickerDialog.setTitle(isStartTime ? "Chọn giờ bắt đầu" : "Chọn giờ kết thúc");
        timePickerDialog.show();
    }

    private void createSchedule() {
        if (!validateForm()) return;

        try {
            int expertId = Integer.parseInt(edtExpertId.getText().toString().trim());
            boolean isActive = cbIsActive.isChecked();

            String startDateTime = selectedDate + "T" + startTime + ":00.000Z";
            String endDateTime = selectedDate + "T" + endTime + ":00.000Z";
            String dayOfWeek = getDayOfWeekFromDate(selectedDate);

            ScheduleRequest request = new ScheduleRequest();
            request.setExpertId(expertId);
            request.setDayOfWeek(dayOfWeek);
            request.setStartDate(startDateTime);
            request.setEndDate(endDateTime);
            request.setActive(isActive);

            Log.d(TAG, "Creating schedule: " + request.toString());
            createScheduleWithRequest(request);

        } catch (Exception e) {
            Log.e(TAG, "Error creating schedule", e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSchedule() {
        if (!validateForm()) return;

        try {
            int expertId = Integer.parseInt(edtExpertId.getText().toString().trim());
            boolean isActive = cbIsActive.isChecked();

            String startDateTime = selectedDate + "T" + startTime + ":00.000Z";
            String endDateTime = selectedDate + "T" + endTime + ":00.000Z";
            String dayOfWeek = getDayOfWeekFromDate(selectedDate);

            ScheduleRequest request = new ScheduleRequest();
            request.setExpertId(expertId);
            request.setDayOfWeek(dayOfWeek);
            request.setStartDate(startDateTime);
            request.setEndDate(endDateTime);
            request.setActive(isActive);

            Log.d(TAG, "Updating schedule ID: " + scheduleToEdit.getScheduleId());
            updateScheduleWithRequest(scheduleToEdit.getScheduleId(), request);

        } catch (Exception e) {
            Log.e(TAG, "Error updating schedule", e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm() {
        String expertIdText = edtExpertId.getText().toString().trim();
        if (expertIdText.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập Expert ID", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Integer.parseInt(expertIdText);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Expert ID phải là số", Toast.LENGTH_SHORT).show();
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
            return false;
        }
    }

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
            return "Monday";
        }
    }

    private void createScheduleWithRequest(ScheduleRequest request) {
        ApiService apiService = RetrofitClient.getInstance();

        apiService.createScheduleWithRequest(request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String responseMessage = response.body();
                    Log.d(TAG, "Create success response: " + responseMessage);
                    Log.d(TAG, "Response code: " + response.code());
                    Log.d(TAG, "Response headers: " + response.headers());

                    // Clean up the response message by removing quotes if present
                    if (responseMessage != null) {
                        responseMessage = responseMessage.replace("\"", "").trim();
                        Log.d(TAG, "Cleaned response message: " + responseMessage);
                    }

                    // Display the actual message from server
                    if (responseMessage != null && !responseMessage.isEmpty()) {
                        Toast.makeText(getContext(), responseMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Tạo lịch thành công!", Toast.LENGTH_SHORT).show();
                    }

                    callback.onScheduleCreated(true);
                    dismiss();
                } else {
                    // Handle error response with detailed message
                    String errorMessage = "Lỗi tạo lịch";

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);

                            // Remove quotes from error message if present
                            errorBody = errorBody.replace("\"", "").trim();

                            // Check if error message contains duplicate schedule message
                            if (errorBody.contains("đã tồn tại lịch này rồi")) {
                                errorMessage = "Lịch làm việc cho ngày này đã tồn tại. Vui lòng chọn ngày khác.";
                            } else if (errorBody.contains("not found expert id")) {
                                errorMessage = "Không tìm thấy chuyên gia với ID này.";
                            } else {
                                // Use the actual error message from server
                                errorMessage = errorBody.isEmpty() ? "Lỗi tạo lịch: " + response.code() : errorBody;
                            }
                        } else {
                            errorMessage = "Lỗi tạo lịch: " + response.code();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                        errorMessage = "Lỗi tạo lịch: " + response.code();
                    }

                    Log.e(TAG, "Create failed: " + response.code());
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();

                    callback.onScheduleCreated(false);
                }
            }

            @Override

            public void onFailure(Call<String> call, Throwable t) {

                Log.e(TAG, "Create error", t);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onScheduleCreated(false);
            }
        });
    }

    private void updateScheduleWithRequest(int scheduleId, ScheduleRequest request) {
        ApiService apiService = RetrofitClient.getInstance();
        apiService.updateScheduleWithRequest(scheduleId, request).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật lịch thành công!", Toast.LENGTH_SHORT).show();
                    callback.onScheduleCreated(true);
                    dismiss();
                } else {

                    // Handle error response with detailed message
                    String errorMessage = "Lỗi cập nhật";

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Update error body: " + errorBody);

                            // Check if error message contains duplicate schedule message
                            if (errorBody.contains("đã tồn tại lịch này rồi")) {
                                errorMessage = "Lịch làm việc cho ngày này đã tồn tại. Vui lòng chọn ngày khác.";
                            } else if (errorBody.contains("not found expert id")) {
                                errorMessage = "Không tìm thấy chuyên gia với ID này.";
                            } else {
                                errorMessage = "Lỗi cập nhật: " + response.code();
                            }
                        } else {
                            errorMessage = "Lỗi cập nhật: " + response.code();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                        errorMessage = "Lỗi cập nhật: " + response.code();
                    }

                    Log.e(TAG, "Update failed: " + response.code());
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();

                    callback.onScheduleCreated(false);
                }
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Log.e(TAG, "Update error", t);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onScheduleCreated(false);
            }
        });
    }
}