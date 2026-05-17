package com.example.taskmate_app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class FilterDialog extends DialogFragment {

    public interface FilterListener {
        void onFilterApplied(FilterOptions options);

        void onFilterCleared();
    }

    FilterListener listener;
    CheckBox cbHigh, cbMedium, cbLow, cbCompleted, cbNotCompleted;
    FilterOptions currentOptions;

    static final String ARG_FILTER_OPTIONS = "current_filter";

    public static FilterDialog newInstance(FilterOptions currentFilter) {
        FilterDialog d = new FilterDialog();
        Bundle b = new Bundle();
        // Convert FilterOptions to Bundle
        b.putBoolean("showHigh", currentFilter.showHigh);
        b.putBoolean("showMedium", currentFilter.showMedium);
        b.putBoolean("showLow", currentFilter.showLow);
        b.putBoolean("showCompleted", currentFilter.showCompleted);
        b.putBoolean("showNotCompleted", currentFilter.showNotCompleted);
        d.setArguments(b);
        return d;
    }

    public static class FilterOptions {
        public boolean showHigh = true;
        public boolean showMedium = true;
        public boolean showLow = true;
        public boolean showCompleted = true;
        public boolean showNotCompleted = true;

        public FilterOptions() {
        }

        public FilterOptions(boolean high, boolean medium, boolean low, boolean completed, boolean notCompleted) {
            this.showHigh = high;
            this.showMedium = medium;
            this.showLow = low;
            this.showCompleted = completed;
            this.showNotCompleted = notCompleted;
        }

        public boolean isShowingAll() {
            return showHigh && showMedium && showLow && showCompleted && showNotCompleted;
        }

        @Override
        public String toString() {
            if (isShowingAll()) {
                return "Showing all tasks";
            }

            StringBuilder sb = new StringBuilder("Showing: ");
            boolean first = true;

            // Priority filters
            if (showHigh || showMedium || showLow) {
                if (showHigh && showMedium && showLow) {
                    // All priorities selected - don't mention
                } else {
                    if (showHigh) {
                        sb.append("High");
                        first = false;
                    }
                    if (showMedium) {
                        if (!first) sb.append(", ");
                        sb.append("Medium");
                        first = false;
                    }
                    if (showLow) {
                        if (!first) sb.append(", ");
                        sb.append("Low");
                        first = false;
                    }
                    sb.append(" priority");
                }
            }

            // Completion filters
            if (!first && (showCompleted != showNotCompleted)) {
                sb.append(" • ");
            }

            if (showCompleted && !showNotCompleted) {
                sb.append("Completed tasks");
            } else if (!showCompleted && showNotCompleted) {
                sb.append("Incomplete tasks");
            }

            return sb.toString();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FilterListener) {
            listener = (FilterListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FilterListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            currentOptions = new FilterOptions(
                    args.getBoolean("showHigh"),
                    args.getBoolean("showMedium"),
                    args.getBoolean("showLow"),
                    args.getBoolean("showCompleted"),
                    args.getBoolean("showNotCompleted")
            );
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_filter, null);

        // Initialize checkboxes
        cbHigh = view.findViewById(R.id.cbFilterHigh);
        cbMedium = view.findViewById(R.id.cbFilterMedium);
        cbLow = view.findViewById(R.id.cbFilterLow);
        cbCompleted = view.findViewById(R.id.cbFilterCompleted);
        cbNotCompleted = view.findViewById(R.id.cbFilterNotCompleted);

        if (currentOptions != null) {
            cbHigh.setChecked(currentOptions.showHigh);
            cbMedium.setChecked(currentOptions.showMedium);
            cbLow.setChecked(currentOptions.showLow);
            cbCompleted.setChecked(currentOptions.showCompleted);
            cbNotCompleted.setChecked(currentOptions.showNotCompleted);
        } else {
            // Set all as checked by default
            cbHigh.setChecked(true);
            cbMedium.setChecked(true);
            cbLow.setChecked(true);
            cbCompleted.setChecked(true);
            cbNotCompleted.setChecked(true);
        }

        // Setup buttons
        Button btnApply = view.findViewById(R.id.btnApplyFilter);
        Button btnClear = view.findViewById(R.id.btnClearFilter);

        btnApply.setOnClickListener(v -> {
            FilterOptions options = new FilterOptions(
                    cbHigh.isChecked(),
                    cbMedium.isChecked(),
                    cbLow.isChecked(),
                    cbCompleted.isChecked(),
                    cbNotCompleted.isChecked()
            );
            listener.onFilterApplied(options);
            dismiss();
        });

        btnClear.setOnClickListener(v -> {
            listener.onFilterCleared();
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }
}