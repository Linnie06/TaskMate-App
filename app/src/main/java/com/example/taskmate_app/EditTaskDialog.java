package com.example.taskmate_app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;

public class EditTaskDialog extends DialogFragment {

    public interface EditTaskListener {
        void onTaskUpdated(Task task, int position);
    }

    EditTaskListener listener;
    EditText etTitle, etDesc;
    Spinner spinnerPriority;
    Button btnSelectDate, btnSelectTime;
    TextView tvSelectedDate, tvSelectedTime;

    private String selectedDate = "";
    private String selectedTime = "";
    private Task taskToEdit;
    private int taskPosition;

    static final String ARG_TASK = "task_to_edit";
    static final String ARG_POSITION = "task_position";

    public static EditTaskDialog newInstance(Task task, int position) {
        EditTaskDialog d = new EditTaskDialog();
        Bundle b = new Bundle();
        try {
            b.putString(ARG_TASK, task.toJson().toString());
            b.putInt(ARG_POSITION, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        d.setArguments(b);
        return d;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EditTaskListener) {
            listener = (EditTaskListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement EditTaskListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_task, null);

        if (getArguments() != null) {
            try {
                String taskJson = getArguments().getString(ARG_TASK);
                taskPosition = getArguments().getInt(ARG_POSITION);
                if (taskJson != null) {
                    taskToEdit = Task.fromJson(new org.json.JSONObject(taskJson));
                }
            } catch (Exception e) {
                e.printStackTrace();
                dismiss();
                return builder.create();
            }
        }

        etTitle = view.findViewById(R.id.etTitle);
        etDesc = view.findViewById(R.id.etDesc);
        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnSelectTime = view.findViewById(R.id.btnSelectTime);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvSelectedTime = view.findViewById(R.id.tvSelectedTime);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.priority_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        if (taskToEdit != null) {
            etTitle.setText(taskToEdit.title);
            etDesc.setText(taskToEdit.description);
            String[] priorities = {"High", "Medium", "Low"};
            for (int i = 0; i < priorities.length; i++) {
                if (priorities[i].equals(taskToEdit.priority)) {
                    spinnerPriority.setSelection(i);
                    break;
                }
            }
            selectedDate = taskToEdit.dueDate;
            selectedTime = taskToEdit.dueTime;
            updateDateTimeDisplay();
        }

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());

        Button btnSave = view.findViewById(R.id.btnSaveTask);
        btnSave.setOnClickListener(v -> {
            try {
                String t = etTitle.getText().toString().trim();
                String d = etDesc.getText().toString().trim();
                String p = spinnerPriority.getSelectedItem().toString();

                if (TextUtils.isEmpty(t)) {
                    Toast.makeText(getActivity(), "Please enter a title", Toast.LENGTH_SHORT).show();
                    return;
                }

                Task updatedTask = new Task(t, d, p, taskToEdit.completed, selectedDate, selectedTime);
                if (listener != null) {
                    listener.onTaskUpdated(updatedTask, taskPosition);
                }

                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error updating task. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void updateDateTimeDisplay() {
        if (!TextUtils.isEmpty(selectedDate)) {
            String[] dateParts = selectedDate.split("-");
            if (dateParts.length == 3) {
                tvSelectedDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d",
                        Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]), Integer.parseInt(dateParts[0])));
            } else {
                tvSelectedDate.setText(selectedDate);
            }
        } else {
            tvSelectedDate.setText("No date selected");
        }

        if (!TextUtils.isEmpty(selectedTime)) {
            String[] timeParts = selectedTime.split(":");
            if (timeParts.length == 2) {
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                String amPm = hour >= 12 ? "PM" : "AM";
                int displayHour = hour == 0 ? 12 : (hour > 12 ? hour - 12 : hour);
                tvSelectedTime.setText(String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm));
            } else {
                tvSelectedTime.setText(selectedTime);
            }
        } else {
            tvSelectedTime.setText("No time selected");
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (!TextUtils.isEmpty(selectedDate)) {
            String[] dateParts = selectedDate.split("-");
            if (dateParts.length == 3) {
                year = Integer.parseInt(dateParts[0]);
                month = Integer.parseInt(dateParts[1]) - 1;
                day = Integer.parseInt(dateParts[2]);
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    updateDateTimeDisplay();
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (!TextUtils.isEmpty(selectedTime)) {
            String[] timeParts = selectedTime.split(":");
            if (timeParts.length == 2) {
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
            }
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, selectedHour, selectedMinute) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    updateDateTimeDisplay();
                },
                hour, minute, false
        );
        timePickerDialog.show();
    }
}
