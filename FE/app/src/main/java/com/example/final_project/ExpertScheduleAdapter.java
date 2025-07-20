package com.example.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.model.ExpertSchedule;
import java.util.List;

public class ExpertScheduleAdapter extends RecyclerView.Adapter<ExpertScheduleAdapter.ScheduleViewHolder> {
    private List<ExpertSchedule> scheduleList;

    public ExpertScheduleAdapter(List<ExpertSchedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public void setScheduleList(List<ExpertSchedule> scheduleList) {
        this.scheduleList = scheduleList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expert_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ExpertSchedule schedule = scheduleList.get(position);

        holder.tvDayOfWeek.setText(schedule.getDayOfWeek());
        holder.tvWorkingHours.setText(schedule.getWorkingHours());
        holder.tvStatus.setText(schedule.isActive() ? "Hoạt động" : "Không hoạt động");

        // Màu sắc theo trạng thái
        if (schedule.isActive()) {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList == null ? 0 : scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek, tvWorkingHours, tvStatus;

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvWorkingHours = itemView.findViewById(R.id.tvWorkingHours);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
