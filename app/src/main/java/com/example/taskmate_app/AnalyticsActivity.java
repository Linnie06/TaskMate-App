package com.example.taskmate_app;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {

    SharedPrefHelper helper;
    PieChart pieCompleted, piePriority;
    TextView tvTotalTasks, tvCompletionRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setContentView
        helper = new SharedPrefHelper(this);
        if (helper.isDarkMode()) {
            setTheme(R.style.Theme_TaskMate_app_Dark);
        } else {
            setTheme(R.style.Theme_TaskMate_app);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        pieCompleted = findViewById(R.id.pieCompleted);
        piePriority = findViewById(R.id.piePriority);
        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvCompletionRate = findViewById(R.id.tvCompletionRate);

        List<Task> tasks = helper.loadTasks();

        int done = 0, notdone = 0;
        int high = 0, medium = 0, low = 0;
        int total = tasks.size();

        for (Task t : tasks) {
            if (t.completed) done++; else notdone++;

            if ("High".equalsIgnoreCase(t.priority)) high++;
            else if ("Low".equalsIgnoreCase(t.priority)) low++;
            else medium++;
        }

        // 🔹 Calculate completion rate
        int completionRate = total > 0 ? (done * 100 / total) : 0;

        // 🔹 Update statistics
        tvTotalTasks.setText("Total: " + total);
        tvCompletionRate.setText("Rate: " + completionRate + "%");

        // 🔹 Colors for completion chart - dynamic red if none completed
        int[] completionColors;
        if (done == 0) {
            // All red if no tasks completed
            completionColors = new int[]{
                    Color.rgb(244, 67, 54) // Red
            };
        } else {
            // Green for completed, Red for not completed
            completionColors = new int[]{
                    Color.rgb(76, 175, 80), // Green for completed
                    Color.rgb(244, 67, 54)  // Red for not completed
            };
        }

        // 🔹 Colors for priority chart
        int[] priorityColors = {
                Color.parseColor("#7F00FF"), // High
                Color.parseColor("#0077B6"), // Medium
                Color.parseColor("#00BFA6")  // Low
        };

        // 🔹 Setup charts
        setupPie(pieCompleted, new String[]{"Completed", "Not Completed"},
                new int[]{done, notdone}, completionColors, "Task Completion");

        setupPie(piePriority, new String[]{"High", "Medium", "Low"},
                new int[]{high, medium, low}, priorityColors, "Priority Levels");
    }

    private void setupPie(PieChart chart, String[] labels, int[] values, int[] colors, String centerText) {
        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            if (values[i] > 0) { // Only add entries with non-zero values
                entries.add(new PieEntry(values[i], labels[i]));
            }
        }

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "No data"));
            colors = new int[]{Color.GRAY};
        }

        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(colors);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(14f);
        set.setSliceSpace(2f);
        set.setSelectionShift(8f);

        PieData data = new PieData(set);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        // Chart styling
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.setCenterText(centerText);
        chart.setCenterTextSize(14f);
        chart.setCenterTextColor(helper.isDarkMode() ? Color.WHITE : Color.BLACK);
        chart.setHoleRadius(40f);
        chart.setTransparentCircleRadius(45f);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // Legend styling
        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setTextColor(helper.isDarkMode() ? Color.WHITE : Color.BLACK);

        chart.animateY(1000);
        chart.invalidate();
    }
}
