package com.example.taskmate_app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SharedPrefHelper {
    private static final String PREFS = "taskmate_prefs";
    private static final String KEY_TASKS = "tasks_json";
    private static final String KEY_DARK = "dark_mode";
    private static final String KEY_HELP_SHOWN = "help_shown";
    SharedPreferences prefs;

    public SharedPrefHelper(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveTasks(List<Task> tasks) {
        JSONArray arr = new JSONArray();
        try {
            for (Task t : tasks) {
                arr.put(t.toJson());
            }
            prefs.edit().putString(KEY_TASKS, arr.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Task> loadTasks() {
        List<Task> list = new ArrayList<>();
        String s = prefs.getString(KEY_TASKS, null);
        if (s == null) return list;
        try {
            JSONArray arr = new JSONArray(s);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(Task.fromJson(o));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void setDarkMode(boolean on) {
        prefs.edit().putBoolean(KEY_DARK, on).apply();
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK, false);
    }

    public void setHelpShown(boolean shown) {
        prefs.edit().putBoolean(KEY_HELP_SHOWN, shown).apply();
    }

    public boolean isHelpShown() {
        return prefs.getBoolean(KEY_HELP_SHOWN, false);
    }
}
