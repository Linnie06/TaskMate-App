package com.example.taskmate_app;

import org.json.JSONException;
import org.json.JSONObject;

public class Task {
    public String title;
    public String description;
    public String priority; // High, Medium, Low
    public boolean completed;
    public String dueDate; // Format: "yyyy-MM-dd"
    public String dueTime; // Format: "HH:mm"
    public int id;

    public Task(String title, String description, String priority, boolean completed) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.completed = completed;
        this.dueDate = "";
        this.dueTime = "";
    }

    public Task(String title, String description, String priority, boolean completed, String dueDate, String dueTime) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.completed = completed;
        this.dueDate = dueDate != null ? dueDate : "";
        this.dueTime = dueTime != null ? dueTime : "";
    }

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("title", title);
        o.put("description", description);
        o.put("priority", priority);
        o.put("completed", completed);
        o.put("dueDate", dueDate);
        o.put("dueTime", dueTime);
        return o;
    }

    public static Task fromJson(JSONObject o) throws JSONException {
        return new Task(
                o.optString("title"),
                o.optString("description"),
                o.optString("priority", "Medium"),
                o.optBoolean("completed", false),
                o.optString("dueDate", ""),
                o.optString("dueTime", "")
        );
    }
}
