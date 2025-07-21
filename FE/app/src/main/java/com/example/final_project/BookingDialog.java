package com.example.final_project;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.final_project.model.Appointment;
import com.example.final_project.model.AppointmentCreateRequest;
import com.example.final_project.model.ExpertSchedule;
import com.example.final_project.model.Facility;
import com.example.final_project.model.Schedule;
import com.example.final_project.network.ApiService;
import com.example.final_project.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookingDialog extends Dialog {
    private static final String TAG = "BookingDialog";

    private int doctorId;
    private String token;
    private BookingCallback callback;
    private Appointment appointmentToEdit;

    // UI Components
    private EditText edtPatientId, edtNote, edtHour;
    private TextView tvExpertId, tvSelectedDate, tvWorkingHours;
    private Spinner spinnerSchedule, spinnerFacility;
    private Button btnBook, btnCancel;
    private TextView tvDialogTitle;

    // Data
    private List<ExpertSchedule> availableSchedules = new ArrayList<>();
    private List<Facility> availableFacilities = new ArrayList<>();
    private ArrayAdapter<String> scheduleAdapter;
    private ArrayAdapter<String> facilityAdapter;
    private ExpertSchedule selectedSchedule = null;
    private Facility selectedFacility = null;
    private Schedule selectedScheduleDetail = null;

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
        setContentView(R.layout.dialog_booking_new);

        if (getWindow() != null) {
            getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        initViews();
        setupSpinners();
        setupForm();
        loadAvailableFacilities();
        loadAvailableSchedules();
        setupClickListeners();
        setupHourValidation();

        // Populate appointment data if editing
        if (appointmentToEdit != null) {
            populateAppointmentData(appointmentToEdit);
        }
    }

    private void initViews() {
        tvDialogTitle = findViewById(R.id.tvDialogTitle);
        tvExpertId = findViewById(R.id.tvExpertId);
        edtPatientId = findViewById(R.id.edtPatientId);
        spinnerFacility = findViewById(R.id.spinnerFacility);
        edtNote = findViewById(R.id.edtNote);
        spinnerSchedule = findViewById(R.id.spinnerSchedule);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvWorkingHours = findViewById(R.id.tvWorkingHours);
        edtHour = findViewById(R.id.edtHour);
        btnBook = findViewById(R.id.btnBook);
        btnCancel = findViewById(R.id.btnCancel);

        Log.d(TAG, "All views initialized successfully");
    }

    // ADD METHOD TO POPULATE APPOINTMENT DATA WHEN EDITING
    private void populateAppointmentData(Appointment appointment) {
        if (appointment != null) {
            Log.d(TAG, "Populating appointment data for edit mode");

            // Set patient ID và note
            edtPatientId.setText(String.valueOf(appointment.getPatientId()));
            edtNote.setText(appointment.getNote() != null ? appointment.getNote() : "");

            // Parse startDate để lấy chỉ hour
            String startDate = appointment.getStartDate(); // "2025-04-01T09:00:00"
            Log.d(TAG, "Parsing startDate: " + startDate);

            if (startDate != null && !startDate.isEmpty()) {
                try {
                    // Parse ISO datetime
                    String[] dateTimeParts = startDate.split("T");
                    if (dateTimeParts.length > 1) {
                        String timePart = dateTimeParts[1]; // "09:00:00"
                        String[] timeParts = timePart.split(":");

                        if (timeParts.length >= 2) {
                            String hourMinute = timeParts[0] + ":" + timeParts[1]; // "09:00"
                            edtHour.setText(hourMinute);
                            edtHour.setEnabled(true);
                            Log.d(TAG, "Set hour to: " + hourMinute);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing startDate: " + e.getMessage());
                }
            }

            // Update dialog title và button text for edit mode
            tvDialogTitle.setText("Sửa lịch hẹn");
            btnBook.setText("Cập nhật");

            Log.d(TAG, "Appointment data populated successfully");
        }
    }

    private void setupSpinners() {
        // Setup Schedule Spinner
        List<String> scheduleDisplayList = new ArrayList<>();
        scheduleDisplayList.add("Đang tải lịch làm việc...");

        scheduleAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, scheduleDisplayList);
        scheduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSchedule.setAdapter(scheduleAdapter);

        // Setup Facility Spinner
        List<String> facilityDisplayList = new ArrayList<>();
        facilityDisplayList.add("Đang tải cơ sở y tế...");

        facilityAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, facilityDisplayList);
        facilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFacility.setAdapter(facilityAdapter);

        // Setup spinner selection listeners
        spinnerSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Schedule spinner item selected: position " + position);
                if (position >= 0 && position < availableSchedules.size()) {
                    selectedSchedule = availableSchedules.get(position);
                    Log.d(TAG, "Selected schedule: " + selectedSchedule.getDayOfWeek());
                    loadScheduleDetail(selectedSchedule.getScheduleId());
                } else {
                    selectedSchedule = null;
                    selectedScheduleDetail = null;
                    clearDateAndWorkingHours();
                    // Don't disable hour input in edit mode if hour is already set
                    if (appointmentToEdit == null || edtHour.getText().toString().trim().isEmpty()) {
                        enableHourInput(false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSchedule = null;
                selectedScheduleDetail = null;
                clearDateAndWorkingHours();
                if (appointmentToEdit == null || edtHour.getText().toString().trim().isEmpty()) {
                    enableHourInput(false);
                }
            }
        });

        spinnerFacility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Facility spinner item selected: position " + position);
                if (position >= 0 && position < availableFacilities.size()) {
                    selectedFacility = availableFacilities.get(position);
                    Log.d(TAG, "Selected facility: " + selectedFacility.getFacilityName());
                } else {
                    selectedFacility = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFacility = null;
            }
        });
    }

    // Key updates to BookingDialog.java

    private void setupForm() {
        tvExpertId.setText(String.valueOf(doctorId));
        Log.d(TAG, "Setting up form for expertId: " + doctorId);

        // Auto-fill patient ID with current logged-in user
        int currentUserId = LoginActivity.getCurrentUserId(getContext());
        if (currentUserId != -1) {
            edtPatientId.setText(String.valueOf(currentUserId));
            edtPatientId.setEnabled(false); // Disable editing
            edtPatientId.setAlpha(0.7f); // Visual indication it's disabled
            Log.d(TAG, "Auto-filled Patient ID with current user: " + currentUserId);
        }

        if (appointmentToEdit != null) {
            btnBook.setText("Cập nhật");
            tvDialogTitle.setText("Sửa lịch hẹn");
            // Don't override patient ID for existing appointments
            edtPatientId.setText(String.valueOf(appointmentToEdit.getPatientId()));
            edtNote.setText(appointmentToEdit.getNote() != null ? appointmentToEdit.getNote() : "");

            // Enable hour input for edit mode
            enableHourInput(true);
        } else {
            btnBook.setText("Đặt lịch");
            tvDialogTitle.setText("Đặt lịch mới");
            enableHourInput(false);
        }
    }

    private void setupHourValidation() {
        edtHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateHourInput();
            }
        });
    }

    private void validateHourInput() {
        if (edtHour.getText().toString().trim().isEmpty()) {
            return;
        }

        String hourText = edtHour.getText().toString().trim();

        try {
            String[] parts = hourText.split(":");
            int inputHour = Integer.parseInt(parts[0]);
            int inputMinute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            if (inputHour < 0 || inputHour > 23) {
                edtHour.setError("Giờ phải từ 00-23");
                return;
            }
            if (inputMinute < 0 || inputMinute > 59) {
                edtHour.setError("Phút phải từ 00-59");
                return;
            }

            // Only validate working hours if schedule is selected
            if (selectedScheduleDetail != null) {
                String startTime = extractHourFromDateTime(selectedScheduleDetail.getStartDate());
                String endTime = extractHourFromDateTime(selectedScheduleDetail.getEndDate());

                String[] startParts = startTime.split(":");
                String[] endParts = endTime.split(":");

                int startHour = Integer.parseInt(startParts[0]);
                int startMinute = startParts.length > 1 ? Integer.parseInt(startParts[1]) : 0;
                int endHour = Integer.parseInt(endParts[0]);
                int endMinute = endParts.length > 1 ? Integer.parseInt(endParts[1]) : 0;

                int inputTotalMinutes = inputHour * 60 + inputMinute;
                int startTotalMinutes = startHour * 60 + startMinute;
                int endTotalMinutes = endHour * 60 + endMinute;

                if (inputTotalMinutes < startTotalMinutes || inputTotalMinutes >= endTotalMinutes) {
                    edtHour.setError("Giờ phải trong khoảng " + startTime + " - " + endTime);
                } else {
                    edtHour.setError(null);
                }
            } else {
                // Clear error if no schedule is selected yet
                edtHour.setError(null);
            }

        } catch (NumberFormatException e) {
            edtHour.setError("Định dạng giờ không hợp lệ (VD: 08:30)");
            Log.e(TAG, "Invalid hour format: " + hourText, e);
        } catch (Exception e) {
            Log.e(TAG, "Error validating hour input", e);
        }
    }

    private void enableHourInput(boolean enabled) {
        edtHour.setEnabled(enabled);
        if (!enabled) {
            // Only clear text if not in edit mode
            if (appointmentToEdit == null) {
                edtHour.setText("");
            }
            edtHour.setHint("Chọn lịch làm việc trước");
            edtHour.setError(null);
        } else {
            edtHour.setHint("Nhập giờ (VD: 08:30)");
        }
        Log.d(TAG, "Hour input " + (enabled ? "enabled" : "disabled"));
    }

    private void updateDateAndWorkingHours() {
        if (selectedSchedule == null || selectedScheduleDetail == null) return;

        String nextDate = getNextDateForDayOfWeek(selectedSchedule.getDayOfWeek());
        tvSelectedDate.setText("Ngày: " + nextDate);

        String workingHours = extractWorkingHoursFromSchedule(selectedScheduleDetail);
        tvWorkingHours.setText("Giờ làm việc: " + workingHours);

        Log.d(TAG, "Updated date: " + nextDate + ", working hours: " + workingHours);
    }

    private void clearDateAndWorkingHours() {
        tvSelectedDate.setText("Ngày: Chưa chọn");
        tvWorkingHours.setText("Giờ làm việc: Chưa chọn");
    }

    private String getNextDateForDayOfWeek(String dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        int targetDayOfWeek = convertDayOfWeekToCalendar(dayOfWeek);
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int daysToAdd = (targetDayOfWeek - currentDayOfWeek + 7) % 7;
        if (daysToAdd == 0) {
            daysToAdd = 7;
        }

        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private int convertDayOfWeekToCalendar(String dayOfWeek) {
        switch (dayOfWeek.toLowerCase()) {
            case "sunday": return Calendar.SUNDAY;
            case "monday": return Calendar.MONDAY;
            case "tuesday": return Calendar.TUESDAY;
            case "wednesday": return Calendar.WEDNESDAY;
            case "thursday": return Calendar.THURSDAY;
            case "friday": return Calendar.FRIDAY;
            case "saturday": return Calendar.SATURDAY;
            default:
                Log.w(TAG, "Unknown day of week: " + dayOfWeek + ", defaulting to Monday");
                return Calendar.MONDAY;
        }
    }

    private String extractHourFromDateTime(String dateTime) {
        try {
            if (dateTime != null && dateTime.contains("T")) {
                String timePart = dateTime.substring(dateTime.indexOf('T') + 1);
                return timePart.substring(0, 5);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting hour from: " + dateTime, e);
        }
        return "";
    }

    private String extractWorkingHoursFromSchedule(Schedule schedule) {
        try {
            String startTime = extractHourFromDateTime(schedule.getStartDate());
            String endTime = extractHourFromDateTime(schedule.getEndDate());
            return startTime + " - " + endTime;
        } catch (Exception e) {
            Log.e(TAG, "Error extracting working hours from schedule", e);
            return "Không xác định";
        }
    }

    private void loadScheduleDetail(int scheduleId) {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Loading schedule detail for scheduleId: " + scheduleId);

        apiService.getScheduleById(scheduleId).enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                Log.d(TAG, "Schedule detail API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    selectedScheduleDetail = response.body();
                    Log.d(TAG, "Loaded schedule detail: " + selectedScheduleDetail.getStartDate() +
                            " to " + selectedScheduleDetail.getEndDate());

                    updateDateAndWorkingHours();
                    enableHourInput(true);
                } else {
                    Log.e(TAG, "Failed to load schedule detail. Response code: " + response.code());
                    Toast.makeText(getContext(), "Lỗi tải thông tin lịch làm việc", Toast.LENGTH_SHORT).show();
                    clearDateAndWorkingHours();
                    if (appointmentToEdit == null) {
                        enableHourInput(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Log.e(TAG, "Error loading schedule detail", t);
                Toast.makeText(getContext(), "Lỗi kết nối khi tải lịch làm việc", Toast.LENGTH_SHORT).show();
                clearDateAndWorkingHours();
                if (appointmentToEdit == null) {
                    enableHourInput(false);
                }
            }
        });
    }

    private void loadAvailableFacilities() {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Loading facilities for expertId: " + doctorId);

        apiService.getFacilityByExpert(doctorId).enqueue(new Callback<Facility>() {
            @Override
            public void onResponse(Call<Facility> call, Response<Facility> response) {
                Log.d(TAG, "Facility API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Facility facility = response.body();
                    Log.d(TAG, "Processing facility: " + facility.getFacilityName() + ", active: " + facility.isActive());

                    availableFacilities.clear();
                    List<String> displayList = new ArrayList<>();

                    if (facility.isActive()) {
                        availableFacilities.add(facility);
                        displayList.add(facility.getDisplayName());
                        selectedFacility = facility;
                        Log.d(TAG, "Added and auto-selected active facility: " + facility.getDisplayName());
                    } else {
                        Log.w(TAG, "Facility is not active: " + facility.getFacilityName());
                        displayList.add("Cơ sở y tế không hoạt động");
                    }

                    if (displayList.isEmpty()) {
                        displayList.add("Không có cơ sở y tế");
                        Log.w(TAG, "No facilities found for expertId: " + doctorId);
                    }

                    facilityAdapter.clear();
                    facilityAdapter.addAll(displayList);
                    facilityAdapter.notifyDataSetChanged();

                    if (!availableFacilities.isEmpty()) {
                        spinnerFacility.setSelection(0);
                        spinnerFacility.setEnabled(false);
                        Log.d(TAG, "Auto-selected facility and disabled spinner");
                    }

                    if (appointmentToEdit != null && appointmentToEdit.getFacilityId() > 0) {
                        setFacilitySelection(appointmentToEdit.getFacilityId());
                    }

                    Log.d(TAG, "Loaded facility successfully");
                } else {
                    Log.e(TAG, "Failed to load facility. Response code: " + response.code());
                    updateFacilitySpinnerWithError();
                }
            }

            @Override
            public void onFailure(Call<Facility> call, Throwable t) {
                Log.e(TAG, "Error loading facility", t);
                updateFacilitySpinnerWithError();
            }
        });
    }

    private void updateFacilitySpinnerWithError() {
        facilityAdapter.clear();
        facilityAdapter.add("Lỗi tải cơ sở y tế");
        facilityAdapter.notifyDataSetChanged();
    }

    private void setFacilitySelection(int facilityId) {
        for (int i = 0; i < availableFacilities.size(); i++) {
            if (availableFacilities.get(i).getFacilityId() == facilityId) {
                spinnerFacility.setSelection(i);
                selectedFacility = availableFacilities.get(i);
                Log.d(TAG, "Set facility selection to: " + selectedFacility.getFacilityName());
                break;
            }
        }
    }

    private void loadAvailableSchedules() {
        ApiService apiService = RetrofitClient.getInstance();
        Log.d(TAG, "Loading schedules for expertId: " + doctorId);

        apiService.getExpertSchedules(doctorId).enqueue(new Callback<List<ExpertSchedule>>() {
            @Override
            public void onResponse(Call<List<ExpertSchedule>> call, Response<List<ExpertSchedule>> response) {
                Log.d(TAG, "Schedules API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    availableSchedules.clear();
                    List<String> displayList = new ArrayList<>();

                    for (ExpertSchedule schedule : response.body()) {
                        Log.d(TAG, "Processing schedule: " + schedule.getDayOfWeek() + ", active: " + schedule.isActive());
                        if (schedule.isActive()) {
                            availableSchedules.add(schedule);
                            String displayText = String.format("%s (%s)",
                                    schedule.getDayOfWeekInVietnamese(),
                                    schedule.getWorkingHours());
                            displayList.add(displayText);
                        }
                    }

                    if (displayList.isEmpty()) {
                        displayList.add("Không có lịch làm việc");
                        Log.w(TAG, "No active schedules found for expertId: " + doctorId);
                    }

                    scheduleAdapter.clear();
                    scheduleAdapter.addAll(displayList);
                    scheduleAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + availableSchedules.size() + " active schedules");
                } else {
                    Log.e(TAG, "Failed to load schedules. Response code: " + response.code());
                    updateSpinnerWithError();
                }
            }

            @Override
            public void onFailure(Call<List<ExpertSchedule>> call, Throwable t) {
                Log.e(TAG, "Error loading schedules", t);
                updateSpinnerWithError();
            }
        });
    }

    private void updateSpinnerWithError() {
        scheduleAdapter.clear();
        scheduleAdapter.add("Lỗi tải lịch làm việc");
        scheduleAdapter.notifyDataSetChanged();
    }

    private void setupClickListeners() {
        btnBook.setOnClickListener(v -> {
            Log.d(TAG, "Book button clicked");
            if (validateForm()) {
                createOrUpdateAppointment();
            }
        });

        btnCancel.setOnClickListener(v -> {
            Log.d(TAG, "Cancel button clicked");
            dismiss();
        });
    }

    private boolean validateForm() {
        // In edit mode, don't require schedule selection if hour is already set
        if (appointmentToEdit == null && selectedSchedule == null) {
            Toast.makeText(getContext(), "Vui lòng chọn lịch làm việc", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedFacility == null) {
            Toast.makeText(getContext(), "Vui lòng chọn cơ sở y tế", Toast.LENGTH_SHORT).show();
            return false;
        }

        String patientIdText = edtPatientId.getText().toString().trim();
        if (patientIdText.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập Patient ID", Toast.LENGTH_SHORT).show();
            edtPatientId.requestFocus();
            return false;
        }

        String hourText = edtHour.getText().toString().trim();
        if (hourText.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập giờ hẹn", Toast.LENGTH_SHORT).show();
            edtHour.requestFocus();
            return false;
        }

        if (edtHour.getError() != null) {
            Toast.makeText(getContext(), "Giờ hẹn không hợp lệ", Toast.LENGTH_SHORT).show();
            edtHour.requestFocus();
            return false;
        }

        try {
            Integer.parseInt(patientIdText);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Patient ID phải là số", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createOrUpdateAppointment() {
        try {
            String appointmentDate;
            String appointmentHour = edtHour.getText().toString().trim();

            if (!appointmentHour.contains(":")) {
                appointmentHour += ":00";
            }

            if (appointmentToEdit != null) {
                // For UPDATE: use existing appointment date or calculate from selected schedule
                if (selectedSchedule != null) {
                    appointmentDate = getNextDateForDayOfWeek(selectedSchedule.getDayOfWeek());
                } else {
                    // Extract date from existing appointment startDate
                    String existingStartDate = appointmentToEdit.getStartDate();
                    appointmentDate = existingStartDate.split("T")[0]; // Get date part only
                }
            } else {
                // For CREATE: calculate from selected schedule
                appointmentDate = getNextDateForDayOfWeek(selectedSchedule.getDayOfWeek());
            }

            String startDateTime = appointmentDate + "T" + appointmentHour + ":00.000Z";
            String endDateTime = appointmentDate + "T" + appointmentHour + ":00.000Z";

            if (appointmentToEdit != null) {
                // For UPDATE: use Appointment object
                Appointment appointment = new Appointment();
                appointment.setPatientId(Integer.parseInt(edtPatientId.getText().toString().trim()));
                appointment.setExpertId(doctorId);
                appointment.setFacilityId(selectedFacility.getFacilityId());
                appointment.setNote(edtNote.getText().toString().trim());
                appointment.setStartDate(startDateTime);
                appointment.setEndDate(endDateTime);

                Log.d(TAG, "Updating appointment with ID: " + appointmentToEdit.getAppointmentId());
                Log.d(TAG, "New startDate: " + startDateTime);
                updateAppointment(appointment);
            } else {
                // For CREATE: use AppointmentCreateRequest
                AppointmentCreateRequest request = new AppointmentCreateRequest();
                request.setScheduleId(selectedSchedule.getScheduleId());
                request.setPatientId(Integer.parseInt(edtPatientId.getText().toString().trim()));
                request.setExpertId(doctorId);
                request.setFacilityId(selectedFacility.getFacilityId());
                request.setNote(edtNote.getText().toString().trim());
                request.setStartDate(startDateTime);
                request.setEndDate(endDateTime);

                Log.d(TAG, "Creating appointment:");
                Log.d(TAG, "- Date: " + appointmentDate);
                Log.d(TAG, "- Hour: " + appointmentHour);
                Log.d(TAG, "- DateTime: " + startDateTime);
                Log.d(TAG, "- ScheduleId: " + request.getScheduleId());
                Log.d(TAG, "- PatientId: " + request.getPatientId());
                Log.d(TAG, "- ExpertId: " + request.getExpertId());
                Log.d(TAG, "- FacilityId: " + request.getFacilityId());

                createAppointment(request);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Number format error", e);
        } catch (Exception e) {
            Log.e(TAG, "Error creating appointment", e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createAppointment(AppointmentCreateRequest request) {
        Log.d(TAG, "Calling create appointment API");
        Log.d(TAG, "Request: " + request.toString());

        ApiService apiService = RetrofitClient.getInstance();
        apiService.createAppointment(request).enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                Log.d(TAG, "Create appointment response code: " + response.code());
                if (response.isSuccessful()) {
                    Log.d(TAG, "Appointment created successfully");
                    Toast.makeText(getContext(), "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                    callback.onBookingResult(true);
                    dismiss();
                } else {
                    Log.e(TAG, "Create appointment failed: " + response.code());
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Log.e(TAG, "Create appointment network error", t);
                callback.onBookingResult(false);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAppointment(Appointment appointment) {
        Log.d(TAG, "Calling update appointment API for ID: " + appointmentToEdit.getAppointmentId());
        ApiService apiService = RetrofitClient.getInstance();
        apiService.updateAppointment(appointmentToEdit.getAppointmentId(), appointment)
                .enqueue(new Callback<Appointment>() {
                    @Override
                    public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                        Log.d(TAG, "Update appointment response code: " + response.code());
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Appointment updated successfully");
                            Toast.makeText(getContext(), "Cập nhật lịch thành công!", Toast.LENGTH_SHORT).show();
                            callback.onBookingResult(true);
                            dismiss();
                        } else {
                            Log.e(TAG, "Update appointment failed: " + response.code());
                            handleApiError(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Appointment> call, Throwable t) {
                        Log.e(TAG, "Update appointment network error", t);
                        callback.onBookingResult(false);
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleApiError(Response<Appointment> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e(TAG, "API Error body: " + errorBody);
                Toast.makeText(getContext(), "Lỗi API: " + response.code(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading errorBody", e);
            Toast.makeText(getContext(), "Lỗi không xác định", Toast.LENGTH_SHORT).show();
        }
        callback.onBookingResult(false);
    }
}