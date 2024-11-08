// From chatgpt, openai, "write a java implementation of EditDialogFragment Class", 2024-11-02

package com.example.myapplication.entrant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

/**
 * class to extend DialogFragment and create a dialog for editing text fields.
 */
public class EditDialogFragment extends DialogFragment {
    /**
     * Interface to handle the completion of edit actions.
     *
     */
    public interface OnEditCompleteListener {
        /**
         * Callback method triggered when editing is completed
         * @param newValue
         */
        void onEditComplete(String newValue);
    }

    private static final String ARG_FIELD_NAME = "field_name";
    private static final String ARG_CURRENT_VALUE = "current_value";
    private OnEditCompleteListener listener;
    /**
     * Creates a new instance of EditDialogFragment with the specified field name and current value.
     * @param fieldName
     * @param currentValue
     * @return a new instance of EditDialogFragment
     */

    public static EditDialogFragment newInstance(String fieldName, String currentValue) {
        EditDialogFragment fragment = new EditDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FIELD_NAME, fieldName);
        args.putString(ARG_CURRENT_VALUE, currentValue);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *  Sets a listener for the edit completion event.
     * @param listener
     */
    public void setOnEditCompleteListener(OnEditCompleteListener listener) {
        this.listener = listener;
    }

    /**
     * Creates the dialog for editing text fields.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return the created dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String fieldName = getArguments() != null ? getArguments().getString(ARG_FIELD_NAME) : "";
        String currentValue = getArguments() != null ? getArguments().getString(ARG_CURRENT_VALUE) : "";

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
        EditText editText = view.findViewById(R.id.editText);
        editText.setText(currentValue);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Edit " + fieldName)
                .setView(view)
                .setPositiveButton("Save", null) // Set to null initially to override later
                .setNegativeButton("Cancel", null)
                .create();

        // Override the Save button's click listener for validation
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String newValue = editText.getText().toString().trim();

                if (TextUtils.isEmpty(newValue)) {
                    Toast.makeText(getContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate phone number if the field is "Phone Number"
                if ("Phone Number".equals(fieldName) && !newValue.matches("\\d{10}")) {
                    Toast.makeText(getContext(), "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ("Email".equals(fieldName) && !newValue.contains("@")) {
                    Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If validation passes, notify listener and close dialog
                if (listener != null) {
                    listener.onEditComplete(newValue);
                }
                dialog.dismiss(); // Close dialog only if input is valid
            });
        });

        return dialog;
    }
}
