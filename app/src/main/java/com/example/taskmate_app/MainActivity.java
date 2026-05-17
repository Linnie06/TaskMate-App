package com.example.taskmate_app;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.os.Build;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;
import android.provider.Settings;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.content.ClipboardManager;
import android.content.ClipData;

public class MainActivity extends AppCompatActivity
        implements AddTaskDialog.AddTaskListener,
        EditTaskDialog.EditTaskListener,
        FilterDialog.FilterListener {

    private static final int REQ_CODE_SPEECH_INPUT = 1001;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> tasks;
    private SharedPrefHelper prefHelper;
    private TextView tvYourTasks;
    private FilterDialog.FilterOptions currentFilter;
    private boolean lastKnownDarkMode = false;
    public int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefHelper = new SharedPrefHelper(this);

        // Apply theme before loading layout
        if (prefHelper.isDarkMode()) {
            setTheme(R.style.Theme_TaskMate_app_Dark);
            lastKnownDarkMode = true;
        } else {
            setTheme(R.style.Theme_TaskMate_app);
            lastKnownDarkMode = false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvYourTasks = findViewById(R.id.tvYourTasks);

        ImageView btnCalendar = findViewById(R.id.btnCalendar);
        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        // Load saved tasks
        tasks = prefHelper.loadTasks();
        currentFilter = new FilterDialog.FilterOptions();

        adapter = new TaskAdapter(tasks, prefHelper, new TaskAdapter.TaskChangeListener() {
            @Override
            public void onTasksChanged(List<Task> updated) {
                tasks.clear();
                tasks.addAll(updated);
                prefHelper.saveTasks(tasks);
                adapter.updateTasks(tasks);
            }

            @Override
            public void onTaskEdit(Task task, int position) {
                EditTaskDialog dialog = EditTaskDialog.newInstance(task, position);
                dialog.show(getSupportFragmentManager(), "editTask");
            }
        });
        recyclerView.setAdapter(adapter);

        // Buttons
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnVoice = findViewById(R.id.btnVoice);
        Button btnAnalytics = findViewById(R.id.btnAnalytics);
        Button btnFilter = findViewById(R.id.btnFilter);

        btnAdd.setOnClickListener(v -> {
            AddTaskDialog dialog = new AddTaskDialog();
            dialog.show(getSupportFragmentManager(), "addTask");
        });

        btnVoice.setOnClickListener(v -> promptSpeechInput());

        btnAnalytics.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AnalyticsActivity.class);
            startActivity(i);
        });

        btnFilter.setOnClickListener(v -> {
            FilterDialog dialog = FilterDialog.newInstance(currentFilter);
            dialog.show(getSupportFragmentManager(), "filterTasks");
        });
    }

    private void promptSpeechInput() {
        PackageManager pm = getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            Toast.makeText(this, "No microphone found", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your task title");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Speech not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT &&
                resultCode == Activity.RESULT_OK &&
                data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spoken = result.get(0);
                AddTaskDialog dialog = AddTaskDialog.newInstance(spoken);
                dialog.show(getSupportFragmentManager(), "addTaskVoice");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean currentDarkMode = prefHelper.isDarkMode();
        if (lastKnownDarkMode != currentDarkMode) {
            recreate();
        }
    }

    @Override
    public void onTaskAdded(Task task) {
        tasks.add(0, task);
        prefHelper.saveTasks(tasks);
        adapter.updateTasks(tasks);
        recyclerView.scrollToPosition(0);
        scheduleNotification(task);
    }

    @Override
    public void onTaskUpdated(Task task, int position) {
        if (position >= 0 && position < tasks.size()) {
            tasks.set(position, task);
            prefHelper.saveTasks(tasks);
            adapter.updateTasks(tasks);
            scheduleNotification(task);
        }
    }

    @Override
    public void onFilterApplied(FilterDialog.FilterOptions options) {
        currentFilter = options;
        adapter.applyFilter(options);
        tvYourTasks.setText(options.isShowingAll()
                ? "TASK LIST"
                : "Filtered: " + options.toString());
    }

    @Override
    public void onFilterCleared() {
        currentFilter = new FilterDialog.FilterOptions();
        adapter.clearFilter();
        tvYourTasks.setText("TASK LIST");
    }

    // 🕒 Schedule notification when task is due
    private void scheduleNotification(Task task) {
        // 1️⃣ Validate input
        if (task.dueDate == null || task.dueDate.trim().isEmpty() ||
                task.dueTime == null || task.dueTime.trim().isEmpty()) {
            Toast.makeText(this, "Task date or time is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2️⃣ Convert date & time to milliseconds
        long triggerTimeMillis = getTriggerTimeMillis(task.dueDate, task.dueTime);
        if (triggerTimeMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "Cannot schedule notification in the past", Toast.LENGTH_SHORT).show();
            return;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 3️⃣ Check exact alarm permission for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Please allow exact alarms in system settings", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return;
            }
        }

        // 4️⃣ Prepare PendingIntent
        Intent intent = new Intent(this, TaskNotificationReceiver.class);
        intent.putExtra("task_title", task.title);

        // Use task.id if exists, otherwise generate unique code
        int requestCode = task.id != 0 ? task.id : (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 5️⃣ Schedule the alarm
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
            );
            Toast.makeText(this, "Notification scheduled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to get AlarmManager", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to convert date and time to milliseconds
    private long getTriggerTimeMillis(String dueDate, String dueTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf.parse(dueDate + " " + dueTime);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
