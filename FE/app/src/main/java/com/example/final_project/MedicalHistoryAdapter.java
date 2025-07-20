package com.example.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.MedicalHistory;
import java.util.List;

public class MedicalHistoryAdapter extends RecyclerView.Adapter<MedicalHistoryAdapter.MedicalHistoryViewHolder> {
    private List<MedicalHistory> medicalHistoryList;

    public interface OnMedicalHistoryActionListener {
        void onDelete(MedicalHistory history);
        void onProcessing(MedicalHistory history);
        void onCancel(MedicalHistory history);
        void onCompleted(MedicalHistory history);
    }

    private OnMedicalHistoryActionListener actionListener;

    public MedicalHistoryAdapter(List<MedicalHistory> medicalHistoryList) {
        this.medicalHistoryList = medicalHistoryList;
    }

    public MedicalHistoryAdapter(List<MedicalHistory> medicalHistoryList, OnMedicalHistoryActionListener actionListener) {
        this.medicalHistoryList = medicalHistoryList;
        this.actionListener = actionListener;
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

        // Set click listeners if actionListener is available
        if (actionListener != null) {
            holder.btnDelete.setOnClickListener(v -> actionListener.onDelete(history));
            holder.btnProcessing.setOnClickListener(v -> actionListener.onProcessing(history));
            holder.btnCancel.setOnClickListener(v -> actionListener.onCancel(history));
            holder.btnCompleted.setOnClickListener(v -> actionListener.onCompleted(history));
        }

        // Show/hide buttons based on status
        updateButtonVisibility(holder, history);
    }

    private void updateButtonVisibility(MedicalHistoryViewHolder holder, MedicalHistory history) {
        String status = history.getStatus();

        // Hide all buttons first
        holder.btnDelete.setVisibility(View.GONE);
        holder.btnProcessing.setVisibility(View.GONE);
        holder.btnCompleted.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);

        if ("Completed".equalsIgnoreCase(status)) {
            // Completed: Show Delete and Cancel
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);

        } else if ("Pending".equalsIgnoreCase(status)) {
            // Pending: Show Processing (to change to Processing status), Completed (to mark as done), and Cancel
            holder.btnProcessing.setVisibility(View.VISIBLE);
            holder.btnCompleted.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.VISIBLE);

        } else if ("Processing".equalsIgnoreCase(status)) {
            // Processing: Show Completed (to mark as done) and Cancel
            holder.btnCompleted.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.GONE);

        } else if ("Cancelled".equalsIgnoreCase(status)) {
            // Cancelled: Only show Delete
            holder.btnDelete.setVisibility(View.VISIBLE);
        }
    }

    private void setStatusColor(TextView textView, String status) {
        int color;
        if ("Completed".equalsIgnoreCase(status)) {
            color = 0xFF4CAF50; // Green
        } else if ("Pending".equalsIgnoreCase(status)) {
            color = 0xFFF44336; // Red
        } else if ("Processing".equalsIgnoreCase(status)) {
            color = 0xFF2196F3; // Blue
        } else if ("Cancelled".equalsIgnoreCase(status)) {
            color = 0xFF666666; // Gray
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
        Button btnDelete, btnProcessing, btnCancel, btnCompleted;

        MedicalHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryId = itemView.findViewById(R.id.tvHistoryId);
            tvAppointmentId = itemView.findViewById(R.id.tvAppointmentId);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPayed = itemView.findViewById(R.id.tvPayed);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnProcessing = itemView.findViewById(R.id.btnProcessing);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnCompleted = itemView.findViewById(R.id.btnCompleted);
        }
    }
}