package com.example.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.MedicalExpert;
import java.util.List;

public class MedicalExpertAdapter extends RecyclerView.Adapter<MedicalExpertAdapter.MedicalExpertViewHolder> {
    private List<MedicalExpert> expertList;
    private OnDoctorClickListener listener;
    private OnScheduleClickListener scheduleListener;

    public interface OnDoctorClickListener {
        void onDoctorClick(MedicalExpert doctor);
    }

    public interface OnScheduleClickListener {
        void onScheduleClick(MedicalExpert doctor);
    }

    public MedicalExpertAdapter(List<MedicalExpert> expertList, OnDoctorClickListener listener, OnScheduleClickListener scheduleListener) {
        this.expertList = expertList;
        this.listener = listener;
        this.scheduleListener = scheduleListener;
    }

    @NonNull
    @Override
    public MedicalExpertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medical_expert, parent, false);
        return new MedicalExpertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalExpertViewHolder holder, int position) {
        MedicalExpert doctor = expertList.get(position);
        holder.tvName.setText(doctor.getName());
        holder.tvSpecialty.setText(doctor.getSpecialty());

        // Click vào item để xem chi tiết doctor
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorClick(doctor);
            }
        });

        // Click vào button để xem lịch
        holder.btnViewSchedule.setOnClickListener(v -> {
            if (scheduleListener != null) {
                android.util.Log.d("DEBUG", "Button clicked for doctor: " + doctor.getName() + " (ID: " + doctor.getId() + ")");
                scheduleListener.onScheduleClick(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expertList == null ? 0 : expertList.size();
    }

    static class MedicalExpertViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialty;
        Button btnViewSchedule;

        MedicalExpertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            btnViewSchedule = itemView.findViewById(R.id.btnViewSchedule);
            android.util.Log.d("DEBUG", "Button found: " + (btnViewSchedule != null));
        }
    }
}