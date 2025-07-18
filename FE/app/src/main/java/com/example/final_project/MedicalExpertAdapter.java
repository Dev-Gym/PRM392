package com.example.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.MedicalExpert;
import java.util.List;

public class MedicalExpertAdapter extends RecyclerView.Adapter<MedicalExpertAdapter.MedicalExpertViewHolder> {
    private List<MedicalExpert> expertList;
    private OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onDoctorClick(MedicalExpert doctor);
    }

    public MedicalExpertAdapter(List<MedicalExpert> expertList, OnDoctorClickListener listener) {
        this.expertList = expertList;
        this.listener = listener;
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
        holder.itemView.setOnClickListener(v -> listener.onDoctorClick(doctor));
    }

    @Override
    public int getItemCount() {
        return expertList == null ? 0 : expertList.size();
    }

    static class MedicalExpertViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialty;

        MedicalExpertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
        }
    }
}