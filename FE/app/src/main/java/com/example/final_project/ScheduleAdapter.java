package com.example.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.Schedule;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private List<Schedule> scheduleList;
    private int selectedPosition = -1; // Vị trí item được chọn
    private OnScheduleClickListener onScheduleClickListener;

    // Interface cho click listener
    public interface OnScheduleClickListener {
        void onScheduleClick(Schedule schedule, int position);
        void onScheduleLongClick(Schedule schedule, int position);
    }

    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public ScheduleAdapter(List<Schedule> scheduleList, OnScheduleClickListener listener) {
        this.scheduleList = scheduleList;
        this.onScheduleClickListener = listener;
    }

    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
        notifyDataSetChanged();
    }

    public void setOnScheduleClickListener(OnScheduleClickListener listener) {
        this.onScheduleClickListener = listener;
    }

    // Method để set/get selected item
    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;

        // Notify changes for both old and new selected items
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public Schedule getSelectedSchedule() {
        if (selectedPosition >= 0 && selectedPosition < scheduleList.size()) {
            return scheduleList.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);

        // Set selection state
        boolean isSelected = position == selectedPosition;
        updateSelectionUI(holder, isSelected);

        // Check if this is new API format or legacy format
        if (schedule.getDayOfWeek() != null && !schedule.getDayOfWeek().isEmpty()) {
            // New API format - use Schedule fields
            holder.tvScheduleId.setText("Schedule ID: " + schedule.getScheduleId());
            holder.tvExpertId.setText("Expert ID: " + schedule.getExpertId());
            holder.tvDayOfWeek.setText("Ngày: " + getDayOfWeekInVietnamese(schedule.getDayOfWeek()));
            holder.tvWorkingHours.setText("Giờ làm việc: " + extractWorkingHours(schedule));

            // Status với màu sắc và text đặc biệt nếu được chọn
            String statusText = schedule.isActive() ? "Hoạt động" : "Không hoạt động";
            if (isSelected) {
                statusText += " (Đã chọn)";
            }
            holder.tvStatus.setText("Trạng thái: " + statusText);

            // Set status color
            if (schedule.isActive()) {
                holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else {
                holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            }

            // Hide legacy fields
            if (holder.tvDate != null) holder.tvDate.setVisibility(View.GONE);
            if (holder.tvTime != null) holder.tvTime.setVisibility(View.GONE);
        } else {
            // Legacy format - use old fields
            holder.tvScheduleId.setText("ID: " + schedule.getId());
            holder.tvExpertId.setText("Doctor ID: " + schedule.getDoctorId());

            if (holder.tvDate != null) {
                String dateText = "Ngày: " + (schedule.getDate() != null ? schedule.getDate() : "Không có");
                if (isSelected) dateText += " (Đã chọn)";
                holder.tvDate.setText(dateText);
                holder.tvDate.setVisibility(View.VISIBLE);
            }

            if (holder.tvTime != null) {
                holder.tvTime.setText("Giờ: " + (schedule.getTime() != null ? schedule.getTime() : "Không có"));
                holder.tvTime.setVisibility(View.VISIBLE);
            }

            // Hide new fields
            holder.tvDayOfWeek.setVisibility(View.GONE);
            holder.tvWorkingHours.setVisibility(View.GONE);
            holder.tvStatus.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (onScheduleClickListener != null) {
                setSelectedPosition(position);
                onScheduleClickListener.onScheduleClick(schedule, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onScheduleClickListener != null) {
                onScheduleClickListener.onScheduleLongClick(schedule, position);
                return true;
            }
            return false;
        });
    }

    private void updateSelectionUI(ScheduleViewHolder holder, boolean isSelected) {
        // Thay đổi background và elevation của CardView
        if (holder.itemView instanceof CardView) {
            CardView cardView = (CardView) holder.itemView;
            if (isSelected) {
                // Selected state - tăng elevation và đổi màu nền
                cardView.setCardElevation(16f); // Tăng elevation nhiều hơn để nổi bật
                cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));

                // Thêm hiệu ứng "border" bằng cách thay đổi corner radius
                cardView.setRadius(12f); // Tăng corner radius khi selected
            } else {
                // Normal state
                cardView.setCardElevation(4f); // Elevation bình thường
                cardView.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
                cardView.setRadius(8f); // Corner radius bình thường
            }
        } else {
            // Fallback cho view khác CardView
            if (isSelected) {
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
                // Thêm padding để tạo hiệu ứng "border"
                holder.itemView.setPadding(8, 8, 8, 8);
            } else {
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
                holder.itemView.setPadding(16, 16, 16, 16); // Padding bình thường
            }
        }
    }
    private String getDayOfWeekInVietnamese(String dayOfWeek) {
        if (dayOfWeek == null) return "Không rõ";

        switch (dayOfWeek.toLowerCase()) {
            case "monday": return "Thứ Hai";
            case "tuesday": return "Thứ Ba";
            case "wednesday": return "Thứ Tư";
            case "thursday": return "Thứ Năm";
            case "friday": return "Thứ Sáu";
            case "saturday": return "Thứ Bảy";
            case "sunday": return "Chủ Nhật";
            default: return dayOfWeek;
        }
    }

    private String extractWorkingHours(Schedule schedule) {
        try {
            if (schedule.getStartDate() != null && schedule.getEndDate() != null) {
                String startTime = extractTime(schedule.getStartDate());
                String endTime = extractTime(schedule.getEndDate());
                return startTime + " - " + endTime;
            }
        } catch (Exception e) {
            // If extraction fails, return default
        }
        return "Không xác định";
    }

    private String extractTime(String dateTimeString) {
        if (dateTimeString == null || !dateTimeString.contains("T")) {
            return "??:??";
        }

        try {
            String timePart = dateTimeString.substring(dateTimeString.indexOf('T') + 1);
            return timePart.substring(0, 5); // Get HH:mm
        } catch (Exception e) {
            return "??:??";
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList == null ? 0 : scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvScheduleId, tvExpertId, tvDayOfWeek, tvWorkingHours, tvStatus;
        TextView tvDate, tvTime; // Legacy fields

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvScheduleId = itemView.findViewById(R.id.tvScheduleId);
            tvExpertId = itemView.findViewById(R.id.tvExpertId);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvWorkingHours = itemView.findViewById(R.id.tvWorkingHours);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            // Legacy fields - có thể null nếu layout không có
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}