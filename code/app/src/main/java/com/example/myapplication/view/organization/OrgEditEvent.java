package com.example.myapplication.view.organization;

import static android.text.TextUtils.isEmpty;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment for editing an existing event. Allows users to modify event details such as
 * name, description, dates, location, capacity, price, poster, and geolocation settings.
 * Users can upload a new poster, and changes are saved to Firestore.
 */
public class OrgEditEvent extends Fragment {
    /**
     * Argument key for passing the event ID to this fragment.
     */
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;

    private FirebaseFirestore db;
    private Uri imageUri;
    private String posterURL, tempPosterURL;

    private EditText editEventName, editEventDescription, editEventStart, editEventEnd,
            editRegistrationStart, editRegistrationEnd, editLocation, editCapacity, editPrice;
    private EditText editWaitingListCap;
    private ImageView posterEditView;
    private Button buttonSaveEvent, buttonCancelSave;
    private SwitchCompat editGeoSwitch;

    private Button eventPickStartDate, eventPickEndDate, registrationPickStartDate, registrationPickEndDate;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    /**
     * Required empty public constructor for the fragment.
     */
    public OrgEditEvent() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided event ID.
     *
     * @param eventID The ID of the event to edit.
     * @return A new instance of fragment OrgEditEvent.
     */
    public static OrgEditEvent newInstance(String eventID) {
        OrgEditEvent fragment = new OrgEditEvent();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventID);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created. Initializes Firestore and image picker functionality.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            Log.d("OrgEditEvent", "Event ID: " + eventId);
        }
        db = FirebaseFirestore.getInstance();
        initializeImagePicker();
    }

    /**
     * Inflates the layout for this fragment and initializes the UI elements.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views
     *                           in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI
     *                           should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-created from a previous
     *                           saved state.
     * @return The view for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_edit_event, container, false);
        initializeUI(view);
        fetchEventInformation();
        return view;
    }

    /**
     * Initializes the UI elements and sets up button click listeners.
     *
     * @param view The fragment's root view.
     */
    private void initializeUI(View view) {
        editEventName = view.findViewById(R.id.editEventName);
        editEventDescription = view.findViewById(R.id.editEventDescription);
        editEventStart = view.findViewById(R.id.editEventStart);
        editEventEnd = view.findViewById(R.id.editEventEnd);
        editRegistrationStart = view.findViewById(R.id.editRegistrationStart);
        editRegistrationEnd = view.findViewById(R.id.editRegistrationEnd);
        editLocation = view.findViewById(R.id.editLocation);
        editCapacity = view.findViewById(R.id.editCapacity);
        editPrice = view.findViewById(R.id.editPrice);
        posterEditView = view.findViewById(R.id.imageViewEditPoster);
        editWaitingListCap = view.findViewById(R.id.editWaitingList);
        buttonSaveEvent = view.findViewById(R.id.buttonSaveEvent);
        buttonCancelSave = view.findViewById(R.id.buttonEditCancel);
        editGeoSwitch = view.findViewById(R.id.editGeolocationSwitch);

        // Date Picker buttons for event and registration dates
        eventPickStartDate = view.findViewById(R.id.selectStartDateButton);
        eventPickEndDate = view.findViewById(R.id.selectEndDateButton);
        registrationPickStartDate = view.findViewById(R.id.selectRegistrationStartDateButton);
        registrationPickEndDate = view.findViewById(R.id.selectRegistrationEndDateButton);

        view.findViewById(R.id.buttonEditPoster).setOnClickListener(v -> launchImagePicker());
        buttonSaveEvent.setOnClickListener(v -> saveChanges(view));
        buttonCancelSave.setOnClickListener(v -> cancelChanges(view));

        // Set up date pickers
        eventPickStartDate.setOnClickListener(v -> showDateTimePicker(editEventStart));
        eventPickEndDate.setOnClickListener(v -> showDateTimePicker(editEventEnd));
        registrationPickStartDate.setOnClickListener(v -> showDateTimePicker(editRegistrationStart));
        registrationPickEndDate.setOnClickListener(v -> showDateTimePicker(editRegistrationEnd));
    }

    /**
     * Initializes the image picker functionality for selecting a new event poster.
     */
    private void initializeImagePicker() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                posterEditView.setImageURI(imageUri);
            }
        });
    }

    /**
     * Launches the image picker to allow the user to select a new event poster.
     */
    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Event Poster"));
    }

    /**
     * Fetches event information from Firestore based on the provided event ID.
     */
    private void fetchEventInformation() {
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(getContext(), "Missing Event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(this::populateFields)
                .addOnFailureListener(e -> {
                    Log.e("OrgEditEvent", "Error fetching event: " + e.getMessage());
                    Toast.makeText(getContext(), "Error fetching event data.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Populates the UI fields with the fetched event data.
     *
     * @param snapshot A Firestore DocumentSnapshot containing the event data.
     */
    private void populateFields(DocumentSnapshot snapshot) {
        editEventName.setText(snapshot.getString("eventName"));
        editEventDescription.setText(snapshot.getString("eventDescription"));
        editEventStart.setText(formatTimestamp(snapshot.get("eventStart")));
        editEventEnd.setText(formatTimestamp(snapshot.get("eventEnd")));
        editRegistrationStart.setText(formatTimestamp(snapshot.get("registrationStart")));
        editRegistrationEnd.setText(formatTimestamp(snapshot.get("registrationEnd")));
        editLocation.setText(snapshot.getString("location"));
        editLocation.setEnabled(false);
        editCapacity.setText(String.valueOf(snapshot.getLong("capacity")));
        editPrice.setText(String.valueOf(snapshot.getDouble("price")));
        editGeoSwitch.setChecked(Boolean.TRUE.equals(snapshot.getBoolean("geolocationRequired")));

        posterURL = snapshot.getString("posterUrl");
        tempPosterURL = posterURL;
        if (posterURL != null && !posterURL.isEmpty()) {
            Glide.with(this).load(posterURL).into(posterEditView);
        }
        long waitingListCap = snapshot.getLong("waitingListCap") != null ? snapshot.getLong("waitingListCap") : 0;
        editWaitingListCap.setText(String.valueOf(waitingListCap));
    }

    /**
     * Saves the changes made to the event. If a new poster is selected, it is uploaded
     * before updating Firestore.
     *
     * @param view The current view context.
     */
    private void saveChanges(View view) {
        if (!validateInputs()) {
            Toast.makeText(getContext(), "Please fill in all fields correctly.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            // Upload the new poster first, then update Firestore and navigate back
            uploadPosterToStorage(newPosterUrl -> {
                posterURL = newPosterUrl;
                updateEventInFirestore(view);
            }, view);
        } else {
            // If no new poster, directly update Firestore
            updateEventInFirestore(view);
        }
    }

    /**
     * Updates the event details in Firestore.
     *
     * @param view The current view context.
     */
    private void updateEventInFirestore(View view) {
        Map<String, Object> updates = collectEventData();
        db.collection("events").document(eventId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("OrgEditEvent", "Event updated successfully.");
                    Toast.makeText(getContext(), "Event updated successfully.", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.action_OrgEditEvent_to_OrgEvent, getArguments());
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgEditEvent", "Failed to update event: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to update event.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Cancels the changes made and resets the UI to the original event data.
     *
     * @param view The current view context.
     */
    private void cancelChanges(View view) {
        if (tempPosterURL != null && !tempPosterURL.isEmpty()) {
            Glide.with(this).load(tempPosterURL).into(posterEditView);
        }
        imageUri = null; // Discard the new poster
        Toast.makeText(getContext(), "Changes canceled.", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.action_OrgEditEvent_to_OrgEvent, getArguments());
    }

    /**
     * Uploads a new event poster to Firebase Storage and retrieves its URL.
     *
     * @param callback Callback for handling the uploaded poster URL.
     * @param view     The current view context.
     */
    private void uploadPosterToStorage(OnPosterUploadCallback callback, View view) {
        String storagePath = "event_posters/" + eventId + ".jpg";
        FirebaseStorage.getInstance().getReference(storagePath).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Log.d("OrgEditEvent", "Poster uploaded successfully: " + uri.toString());
                            callback.onUploadSuccess(uri.toString());
                        })
                        .addOnFailureListener(e -> {
                            Log.e("OrgEditEvent", "Failed to get download URL: " + e.getMessage());
                            Toast.makeText(getContext(), "Failed to upload poster.", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e("OrgEditEvent", "Poster upload failed: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to upload poster.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Validates user inputs to ensure all required fields are filled correctly.
     *
     * @return True if all inputs are valid, false otherwise.
     */
    private boolean validateInputs() {
        boolean valid = true;
        // Check each field and log if any validation fails
        if (isEmpty(editEventName)) {
            Log.e("OrgEditEvent", "Event Name is empty.");
            valid = false;
        }
        if (isEmpty(editEventDescription)) {
            Log.e("OrgEditEvent", "Event Description is empty.");
            valid = false;
        }
        if (parseDate(editEventStart.getText().toString()) == null) {
            Log.e("OrgEditEvent", "Invalid Event Start date.");
            valid = false;
        }
        if (parseDate(editEventEnd.getText().toString()) == null) {
            Log.e("OrgEditEvent", "Invalid Event End date.");
            valid = false;
        }
        if (parseDate(editRegistrationStart.getText().toString()) == null) {
            Log.e("OrgEditEvent", "Invalid Registration Start date.");
            valid = false;
        }
        if (parseDate(editRegistrationEnd.getText().toString()) == null) {
            Log.e("OrgEditEvent", "Invalid Registration End date.");
            valid = false;
        }
        if (!isPositiveInteger(editCapacity.getText().toString())) {
            Log.e("OrgEditEvent", "Invalid capacity value.");
            valid = false;
        }
        if (!isPositiveDouble(editPrice.getText().toString())) {
            Log.e("OrgEditEvent", "Invalid price value.");
            valid = false;
        }
        // Validate waiting list capacity (can be 0 or positive)
        if (!isNonNegativeInteger(editWaitingListCap.getText().toString())) {
            Log.e("OrgEditEvent", "Invalid waiting list capacity value.");
            valid = false;
        }

        return valid;
    }

    /**
     * Collects event data from the UI fields into a map for updating Firestore.
     *
     * @return A map containing the event data.
     */
    private Map<String, Object> collectEventData() {
        Map<String, Object> data = new HashMap<>();
        data.put("eventName", editEventName.getText().toString());
        data.put("eventDescription", editEventDescription.getText().toString());
        data.put("eventStart", parseDateToTimestamp(editEventStart.getText().toString()));
        data.put("eventEnd", parseDateToTimestamp(editEventEnd.getText().toString()));
        data.put("registrationStart", parseDateToTimestamp(editRegistrationStart.getText().toString()));
        data.put("registrationEnd", parseDateToTimestamp(editRegistrationEnd.getText().toString()));
        data.put("posterUrl", posterURL);
        data.put("location", editLocation.getText().toString());
        data.put("capacity", Integer.parseInt(editCapacity.getText().toString()));
        data.put("price", Double.parseDouble(editPrice.getText().toString()));

        int waitingListCap = Integer.parseInt(editWaitingListCap.getText().toString());
        data.put("waitingListCap", waitingListCap);
        data.put("waitingListLimited", waitingListCap > 0);

        data.put("geolocationRequired", editGeoSwitch.isChecked());

        return data;
    }

    /**
     * Parses a date string into a Firestore Timestamp object.
     *
     * @param dateStr The date string to parse.
     * @return A Firestore Timestamp object or null if parsing fails.
     */
    private com.google.firebase.Timestamp parseDateToTimestamp(String dateStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = format.parse(dateStr);
            return new com.google.firebase.Timestamp(date);
        } catch (Exception e) {
            Log.e("OrgEditEvent", "Error parsing date: " + e.getMessage());
            return null;
        }
    }

    /**
     * Formats a Firestore Timestamp object into a readable date string.
     *
     * @param timestamp The Firestore Timestamp to format.
     * @return A formatted date string.
     */
    private String formatTimestamp(Object timestamp) {
        if (timestamp instanceof com.google.firebase.Timestamp) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return format.format(((com.google.firebase.Timestamp) timestamp).toDate());
        }
        return "";
    }

    /**
     * Checks if the given EditText field is empty.
     *
     * @param input the EditText field to be checked.
     * @return {@code true} if the input field is empty or contains only whitespace, {@code false} otherwise.
     */
    private boolean isEmpty(EditText input) {
        return input.getText().toString().trim().isEmpty();
    }

    /**
     * Validates whether a given string represents a positive integer.
     *
     * @param value the string to be validated.
     * @return {@code true} if the string can be parsed into a positive integer, {@code false} otherwise.
     *         (e.g., "1", "10" return {@code true}, but "0", "-1", "abc" return {@code false}).
     */
    private boolean isPositiveInteger(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates whether a given string represents a positive double.
     *
     * @param value the string to be validated.
     * @return {@code true} if the string can be parsed into a positive double, {@code false} otherwise.
     *         (e.g., "1.5", "10.0" return {@code true}, but "0", "-1.0", "abc" return {@code false}).
     */
    private boolean isPositiveDouble(String value) {
        try {
            double parsedValue = Double.parseDouble(value);
            if (parsedValue <= 0) {
                Log.e("OrgEditEvent", "Price is not a positive double: " + value);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            Log.e("OrgEditEvent", "Invalid number format for price: " + value);
            return false;
        }
    }

    /**
     * Validates whether a given string represents a non-negative integer.
     *
     * @param value the string to be validated.
     * @return {@code true} if the string can be parsed into a non-negative integer (including 0), {@code false} otherwise.
     *         (e.g., "0", "5" return {@code true}, but "-1", "abc" return {@code false}).
     */
    private boolean isNonNegativeInteger(String value) {
        try {
            return Integer.parseInt(value) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses a date string into a Date object.
     *
     * @param dateString The date string to parse.
     * @return A Date object or null if parsing fails.
     */
    public Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(dateString);
        } catch (ParseException e) {
            Log.e("org_add_event", "Date parsing error", e);
            Toast.makeText(requireContext(), "Invalid date format. Please use yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show();
            return null; // Return null if parsing fails
        }
    }

    /**
     * Displays a date and time picker dialog for the user to select a date and time.
     *
     * @param targetEditText The EditText to populate with the selected date and time.
     */
    private void showDateTimePicker(EditText targetEditText) {
        // Get the current date and time
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog
        new DatePickerDialog(requireContext(), (view, year1, month1, dayOfMonth) -> {
            // Update the calendar with the selected date
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Show TimePickerDialog
            new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> {
                // Update the calendar with the selected time
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                // Format and set the selected date and time on the EditText
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                targetEditText.setText(dateTimeFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }, year, month, day).show();
    }

    /**
     * Callback interface for handling poster upload success.
     */
    interface OnPosterUploadCallback {
        void onUploadSuccess(String posterUrl);
    }
}
