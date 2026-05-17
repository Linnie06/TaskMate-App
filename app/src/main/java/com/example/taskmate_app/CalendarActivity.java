package com.example.taskmate_app;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView tvSelectedDate, tvTasksForDate;
    private SharedPrefHelper prefHelper;
    private List<Task> allTasks;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Initialize views safely
        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvTasksForDate = findViewById(R.id.tvTasksForDate);

        prefHelper = new SharedPrefHelper(this);
        allTasks = prefHelper.loadTasks();

        // Show today's tasks initially
        String today = sdf.format(new Date(calendarView.getDate()));
        tvSelectedDate.setText("Selected Date: " + today);
        showTasksForDate(today);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    year, (month + 1), dayOfMonth);
            tvSelectedDate.setText("Selected Date: " + selectedDate);
            showTasksForDate(selectedDate);
        });
    }

    private void showTasksForDate(String selectedDate) {
        if (allTasks == null) {
            tvTasksForDate.setText("No tasks found.");
            return;
        }

        List<Task> tasksForDate = new ArrayList<>();
        for (Task t : allTasks) {
            if (t.dueDate != null && t.dueDate.equals(selectedDate)) {
                tasksForDate.add(t);
            }
        }

        if (tasksForDate.isEmpty()) {
            tvTasksForDate.setText("No tasks on " + selectedDate);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Tasks on ").append(selectedDate).append(":\n\n");
            for (Task t : tasksForDate) {
                sb.append("• ").append(t.title);
                if (t.priority != null && !t.priority.isEmpty()) {
                    sb.append(" (").append(t.priority).append(")");
                }
                if (t.dueTime != null && !t.dueTime.isEmpty()) {
                    sb.append(" at ").append(t.dueTime);
                }
                sb.append("\n");
            }
            tvTasksForDate.setText(sb.toString());
        }
    }
}
