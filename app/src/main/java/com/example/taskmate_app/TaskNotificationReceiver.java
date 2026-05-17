package com.example.taskmate_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class TaskNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get task title safely
        String taskTitle = intent.getStringExtra("task_title");
        if (taskTitle == null || taskTitle.trim().isEmpty()) {
            taskTitle = "Unnamed Task";
        }

        // Notification Manager
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Channel setup (for Android 8.0+)
        String channelId = "taskmate_due_channel";
        String channelName = "TaskMate Reminders";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifies you when a task is due");
            channel.enableLights(true);
            channel.setLightColor(Color.MAGENTA);
            channel.enableVibration(true);
            nm.createNotificationChannel(channel);
        }

        // When user taps the notification → open app
        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(
                context,
                0,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_time) // ✅ Make sure ic_time exists in res/drawable
                .setContentTitle("Task Due ⏰")
                .setContentText("It's time for: " + taskTitle)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("It's time for: " + taskTitle))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL) // sound + vibration
                .setColor(Color.parseColor("#FF4081")) // baby pink accent
                .setContentIntent(pi);

        // Display the notification (unique ID)
        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}

