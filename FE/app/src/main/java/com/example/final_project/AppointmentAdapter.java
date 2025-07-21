package com.example.final_project;

import android.content.Context;
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
    private Context context;

    public interface OnAppointmentActionListener {
        void onEdit(Appointment appointment);
        void onDelete(Appointment appointment);
        void onConfirm(Appointment appointment);
        void onCancel(Appointment appointment);
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
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        // Bind all data to views
        holder.tvAppointmentId.setText("ID: " + appointment.getAppointmentId());
        holder.tvPatientId.setText("Patient ID: " + appointment.getPatientId());
        holder.tvExpertId.setText("Expert ID: " + appointment.getExpertId());
        holder.tvDate.setText(appointment.getStartDate());
        holder.tvStatus.setText("Status: " + appointment.getStatus());

        // Handle Note display - NEW CODE
        String note = appointment.getNote();
        if (note != null && !note.trim().isEmpty()) {
            holder.tvNote.setText("Ghi chú: " + note);
            holder.tvNote.setVisibility(View.VISIBLE);
        } else {
            holder.tvNote.setText("Ghi chú: Không có");
            holder.tvNote.setVisibility(View.GONE); // Hide if no note
        }

        // Set click listeners for all buttons
        holder.btnEdit.setOnClickListener(v -> actionListener.onEdit(appointment));
        holder.btnDelete.setOnClickListener(v -> actionListener.onDelete(appointment));
        holder.btnConfirm.setOnClickListener(v -> actionListener.onConfirm(appointment));
        holder.btnCancel.setOnClickListener(v -> actionListener.onCancel(appointment));

        // Show/hide buttons based on appointment status and user type
        updateButtonVisibility(holder, appointment);
    }

    private void updateButtonVisibility(AppointmentViewHolder holder, Appointment appointment) {
        String status = appointment.getStatus();

        // Get current user type
        String currentUserType = LoginActivity.getCurrentUserType(context);
        boolean isPatient = "Patient".equals(currentUserType);

        // Log for debugging
        android.util.Log.d("AppointmentAdapter", "UserType: " + currentUserType + ", Status: " + status);

        if ("Pending".equalsIgnoreCase(status)) {
            // Pending: Show different buttons based on user type
            if (isPatient) {
                // Patient can only Edit and Cancel their pending appointments
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnEdit.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.GONE);
            } else {
                // MedicalExpert/Admin can Confirm, Cancel, Edit
                holder.btnConfirm.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.GONE);
            }

        } else if ("Confirmed".equalsIgnoreCase(status)) {
            // Confirmed: Different permissions based on user type
            if (isPatient) {
                // Patient can only cancel confirmed appointments
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.GONE);
            } else {
                // MedicalExpert/Admin can cancel
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.GONE);
            }

        } else if ("Cancelled".equalsIgnoreCase(status)) {
            // Cancelled: Different permissions based on user type
            if (isPatient) {
                // Patient can delete their cancelled appointments
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.GONE);
            } else {
                // MedicalExpert/Admin can delete
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.VISIBLE);
            }

        } else if ("Deleted".equalsIgnoreCase(status) || "IsDelete".equalsIgnoreCase(status)) {
            // Deleted/IsDelete: Hide all buttons for everyone
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);

        } else if ("Completed".equalsIgnoreCase(status)) {
            // Completed: Hide all buttons for everyone
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);

        } else {
            // Default/Unknown status: Show delete based on user type
            if (isPatient) {
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.VISIBLE);
            } else {
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnEdit.setVisibility(View.GONE);
                holder.btnDelete.setVisibility(View.VISIBLE);
            }
        }

        // Update button text based on user type for better UX
        if (isPatient) {
            holder.btnCancel.setText("Hủy lịch");
            holder.btnEdit.setText("Sửa");
            holder.btnDelete.setText("Xóa");
        } else {
            holder.btnConfirm.setText("Xác nhận");
            holder.btnCancel.setText("Từ chối");
            holder.btnEdit.setText("Chỉnh sửa");
            holder.btnDelete.setText("Xóa");
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList == null ? 0 : appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppointmentId, tvPatientId, tvExpertId, tvDate, tvStatus, tvNote; // Added tvNote
        Button btnEdit, btnDelete, btnConfirm, btnCancel;

        AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppointmentId = itemView.findViewById(R.id.tvAppointmentId);
            tvPatientId = itemView.findViewById(R.id.tvPatientId);
            tvExpertId = itemView.findViewById(R.id.tvExpertId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvNote = itemView.findViewById(R.id.tvNote); // NEW - Add this line
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}