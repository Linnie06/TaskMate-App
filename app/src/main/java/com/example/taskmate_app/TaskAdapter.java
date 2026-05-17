package com.example.taskmate_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.text.TextUtils;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.VH> {

    public interface TaskChangeListener {
        void onTasksChanged(List<Task> updated);
        void onTaskEdit(Task task, int position);
    }

    private List<Task> allTasks;
    private List<Task> filteredTasks;
    private SharedPrefHelper helper;
    private TaskChangeListener listener;
    private FilterDialog.FilterOptions currentFilter;

    public TaskAdapter(List<Task> tasks, SharedPrefHelper helper, TaskChangeListener listener) {
        this.allTasks = new ArrayList<>(tasks);
        this.filteredTasks = new ArrayList<>(tasks);
        this.helper = helper;
        this.listener = listener;
        this.currentFilter = new FilterDialog.FilterOptions(); // Show all by default
    }

    public void applyFilter(FilterDialog.FilterOptions filter) {
        this.currentFilter = filter;
        filteredTasks.clear();
        for (Task task : allTasks) {
            boolean showTask = true;

            // Priority filter
            String priority = (task.priority == null) ? "" : task.priority.trim().toLowerCase();
            if (priority.equals("high") && !filter.showHigh) showTask = false;
            else if (priority.equals("medium") && !filter.showMedium) showTask = false;
            else if (priority.equals("low") && !filter.showLow) showTask = false;

            // Completion filter
            if (task.completed && !filter.showCompleted) showTask = false;
            if (!task.completed && !filter.showNotCompleted) showTask = false;

            if (showTask) filteredTasks.add(task);
        }
        notifyDataSetChanged();
    }

    public void clearFilter() {
        currentFilter = new FilterDialog.FilterOptions();
        filteredTasks.clear();
        filteredTasks.addAll(allTasks);
        notifyDataSetChanged();
    }

    public void updateTasks(List<Task> newTasks) {
        this.allTasks = new ArrayList<>(newTasks);
        applyFilter(currentFilter); // reapply current filter safely
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Task t = filteredTasks.get(position);

        holder.tvTitle.setText(t.title);
        holder.tvPriority.setText(t.priority);

        if (!TextUtils.isEmpty(t.description)) {
            holder.tvDesc.setText(t.description);
            holder.tvDesc.setVisibility(View.VISIBLE);
        } else {
            holder.tvDesc.setVisibility(View.GONE);
        }

        // Handle date/time display
        boolean hasDate = !TextUtils.isEmpty(t.dueDate);
        boolean hasTime = !TextUtils.isEmpty(t.dueTime);

        if (hasDate || hasTime) {
            holder.layoutDateTime.setVisibility(View.VISIBLE);

            // ✅ Format date as DD/MM/YYYY
            if (hasDate) {
                String[] parts = t.dueDate.split("-");
                if (parts.length == 3) {
                    String day = parts[2].length() == 1 ? "0" + parts[2] : parts[2];
                    String month = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                    holder.tvDate.setText(day + "/" + month + "/" + parts[0]);
                } else {
                    holder.tvDate.setText(t.dueDate);
                }
                holder.tvDate.setVisibility(View.VISIBLE);
            } else {
                holder.tvDate.setVisibility(View.GONE);
            }

            // Format time as hh:mm AM/PM
            if (hasTime) {
                try {
                    String[] timeParts = t.dueTime.split(":");
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);
                    String amPm = (hour >= 12) ? "PM" : "AM";
                    int displayHour = (hour == 0) ? 12 : (hour > 12 ? hour - 12 : hour);
                    holder.tvTime.setText(String.format("%02d:%02d %s", displayHour, minute, amPm));
                } catch (Exception e) {
                    holder.tvTime.setText(t.dueTime);
                }
                holder.tvTime.setVisibility(View.VISIBLE);
            } else {
                holder.tvTime.setVisibility(View.GONE);
            }
        } else {
            holder.layoutDateTime.setVisibility(View.GONE);
        }

        // Checkbox logic
        holder.cbDone.setOnCheckedChangeListener(null);
        holder.cbDone.setChecked(t.completed);
        holder.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            t.completed = isChecked;
            helper.saveTasks(allTasks);
            if (listener != null) listener.onTasksChanged(allTasks);
            applyFilter(currentFilter);
        });

        // Edit task
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int originalPosition = allTasks.indexOf(t);
                listener.onTaskEdit(t, originalPosition);
            }
        });

        // Delete task
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete \"" + t.title + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        allTasks.remove(t);
                        helper.saveTasks(allTasks);
                        if (listener != null) listener.onTasksChanged(allTasks);
                        applyFilter(currentFilter);
                        Toast.makeText(v.getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return filteredTasks.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPriority, tvDesc, tvDate, tvTime;
        CheckBox cbDone;
        LinearLayout layoutDateTime;
        Button btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvPriority = itemView.findViewById(R.id.tvTaskPriority);
            tvDesc = itemView.findViewById(R.id.tvTaskDesc);
            tvDate = itemView.findViewById(R.id.tvTaskDate);
            tvTime = itemView.findViewById(R.id.tvTaskTime);
            cbDone = itemView.findViewById(R.id.cbDone);
            layoutDateTime = itemView.findViewById(R.id.layoutDateTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
