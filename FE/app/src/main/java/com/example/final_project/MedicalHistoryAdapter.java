package com.example.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.MedicalHistory;
import java.util.List;

public class MedicalHistoryAdapter extends RecyclerView.Adapter<MedicalHistoryAdapter.MedicalHistoryViewHolder> {
    private List<MedicalHistory> medicalHistoryList;

    public MedicalHistoryAdapter(List<MedicalHistory> medicalHistoryList) {
        this.medicalHistoryList = medicalHistoryList;
    }

    public void setMedicalHistoryList(List<MedicalHistory> medicalHistoryList) {
        this.medicalHistoryList = medicalHistoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicalHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medical_history, parent, false);
        return new MedicalHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalHistoryViewHolder holder, int position) {
        MedicalHistory history = medicalHistoryList.get(position);

        // Bind data to views
        holder.tvHistoryId.setText("History ID: " + history.getHistoryId());
        holder.tvAppointmentId.setText("Appointment ID: " + history.getAppointmentId());
        holder.tvDescription.setText("Mô tả: " + (history.getDescription() != null ? history.getDescription() : "Không có"));
        holder.tvStatus.setText("Trạng thái: " + history.getStatus());
        holder.tvPayed.setText("Thanh toán: " + (history.isPayed() ? "Đã thanh toán" : "Chưa thanh toán"));
        holder.tvCreatedAt.setText("Tạo lúc: " + formatDateTime(history.getCreatedAt()));

        // Set status color
        setStatusColor(holder.tvStatus, history.getStatus());

        // Set payment color
        setPaymentColor(holder.tvPayed, history.isPayed());
    }

    private void setStatusColor(TextView textView, String status) {
        int color;
        if ("Completed".equalsIgnoreCase(status)) {
            color = 0xFF4CAF50; // Green
        } else if ("Pending".equalsIgnoreCase(status)) {
            color = 0xFFF44336; // Red
        } else {
            color = 0xFF666666; // Gray
        }
        textView.setTextColor(color);
    }

    private void setPaymentColor(TextView textView, boolean isPayed) {
        int color = isPayed ? 0xFF4CAF50 : 0xFFF44336; // Green if paid, Red if not
        textView.setTextColor(color);
    }

    private String formatDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "Không xác định";
        }
        try {
            // Format from "2025-07-15T17:39:15.957" to readable format
            String[] parts = dateTime.split("T");
            if (parts.length > 1) {
                String datePart = parts[0]; // "2025-07-15"
                String timePart = parts[1].substring(0, 8); // "17:39:15"
                return datePart + " " + timePart;
            }
            return dateTime;
        } catch (Exception e) {
            return dateTime;
        }
    }

    @Override
    public int getItemCount() {
        return medicalHistoryList == null ? 0 : medicalHistoryList.size();
    }

    static class MedicalHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryId, tvAppointmentId, tvDescription, tvStatus, tvPayed, tvCreatedAt;

        MedicalHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryId = itemView.findViewById(R.id.tvHistoryId);
            tvAppointmentId = itemView.findViewById(R.id.tvAppointmentId);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPayed = itemView.findViewById(R.id.tvPayed);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        }
    }
}