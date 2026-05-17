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

public class AddTaskDialog extends DialogFragment {

    public interface AddTaskListener {
        void onTaskAdded(Task task);
    }

    AddTaskListener listener;
    EditText etTitle, etDesc;
    Spinner spinnerPriority;
    Button btnSelectDate, btnSelectTime;
    TextView tvSelectedDate, tvSelectedTime;

    private String selectedDate = "";
    private String selectedTime = "";

    static final String ARG_TITLE = "prefill_title";

    public static AddTaskDialog newInstance(String title) {
        AddTaskDialog d = new AddTaskDialog();
        Bundle b = new Bundle();
        b.putString(ARG_TITLE, title);
        d.setArguments(b);
        return d;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddTaskListener) {
            listener = (AddTaskListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement AddTaskListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_task, null);

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

        if (getArguments() != null && getArguments().containsKey(ARG_TITLE)) {
            etTitle.setText(getArguments().getString(ARG_TITLE));
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

                Task task = new Task(t, d, p, false, selectedDate, selectedTime);
                if (listener != null) {
                    listener.onTaskAdded(task);
                }

                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error saving task. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    tvSelectedDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedMonth + 1, selectedDay, selectedYear));
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, selectedHour, selectedMinute) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    String amPm = selectedHour >= 12 ? "PM" : "AM";
                    int displayHour = selectedHour == 0 ? 12 :
                            (selectedHour > 12 ? selectedHour - 12 : selectedHour);
                    tvSelectedTime.setText(String.format(Locale.getDefault(), "%02d:%02d %s",
                            displayHour, selectedMinute, amPm));
                },
                hour, minute, false
        );
        timePickerDialog.show();
    }
}
