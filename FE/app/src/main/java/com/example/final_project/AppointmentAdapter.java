package com.example.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.Appointment;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private List<Appointment> appointmentList;

    public interface OnAppointmentActionListener {
        void onEdit(Appointment appointment);
        void onDelete(Appointment appointment);
        void onConfirm(Appointment appointment);  // ADD THIS
        void onCancel(Appointment appointment);   // ADD THIS
    }

    private OnAppointmentActionListener actionListener;

    public AppointmentAdapter(List<Appointment> appointmentList, OnAppointmentActionListener actionListener) {
        this.appointmentList = appointmentList;
        this.actionListener = actionListener;
    }

    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.tvDate.setText(appointment.getStartDate());
        holder.tvStatus.setText(appointment.getStatus());

        // Set click listeners for all buttons
        holder.btnEdit.setOnClickListener(v -> actionListener.onEdit(appointment));
        holder.btnDelete.setOnClickListener(v -> actionListener.onDelete(appointment));
        holder.btnConfirm.setOnClickListener(v -> actionListener.onConfirm(appointment));
        holder.btnCancel.setOnClickListener(v -> actionListener.onCancel(appointment));

        // Show/hide buttons based on appointment status
        updateButtonVisibility(holder, appointment);
    }

    private void updateButtonVisibility(AppointmentViewHolder holder, Appointment appointment) {
        String status = appointment.getStatus();

        if ("Pending".equalsIgnoreCase(status)) {
            // Pending: Show Confirm, Cancel, Edit, Delete
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

        } else if ("Confirmed".equalsIgnoreCase(status)) {
            // Confirmed: Only show Cancel, Edit, Delete (Hide Confirm)
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE);

        } else if ("Cancelled".equalsIgnoreCase(status)) {
            // Cancelled: Only show Edit and Delete (Hide Confirm and Cancel)
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

        } else if ("Deleted".equalsIgnoreCase(status) || "IsDelete".equalsIgnoreCase(status)) {
            // Deleted/IsDelete: Hide all buttons
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);

        } else {
            // Default/Unknown status: Show all buttons
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList == null ? 0 : appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus;
        Button btnEdit, btnDelete, btnConfirm, btnCancel; // UPDATED TO BUTTON TYPE

        AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}