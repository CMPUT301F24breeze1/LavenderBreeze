package com.example.myapplication.view.organization;

import static android.text.TextUtils.isEmpty;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrgEditEvent extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;

    private FirebaseFirestore db;
    private Uri imageUri;
    private String posterURL, tempPosterURL;

    private EditText editEventName, editEventDescription, editEventStart, editEventEnd,
            editRegistrationStart, editRegistrationEnd, editLocation, editCapacity, editPrice;
    private ImageView posterEditView;
    private Button buttonSaveEvent, buttonCancelSave;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    public OrgEditEvent() {
        // Required empty public constructor
    }

    public static OrgEditEvent newInstance(String eventID) {
        OrgEditEvent fragment = new OrgEditEvent();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventID);
        fragment.setArguments(args);
        return fragment;
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_edit_event, container, false);
        initializeUI(view);
        fetchEventInformation();
        return view;
    }

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
        buttonSaveEvent = view.findViewById(R.id.buttonSaveEvent);
        buttonCancelSave = view.findViewById(R.id.buttonCancel);

        view.findViewById(R.id.buttonEditPoster).setOnClickListener(v -> launchImagePicker());
        buttonSaveEvent.setOnClickListener(v -> saveChanges(view));
        buttonCancelSave.setOnClickListener(v -> cancelChanges(view));
    }

    private void initializeImagePicker() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                posterEditView.setImageURI(imageUri);
            }
        });
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Event Poster"));
    }

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

        posterURL = snapshot.getString("posterUrl");
        tempPosterURL = posterURL;
        if (posterURL != null && !posterURL.isEmpty()) {
            Glide.with(this).load(posterURL).into(posterEditView);
        }
    }

    private void saveChanges(View view) {
        if (validateInputs()) {
            Log.d("OrgEditEvent", "Inputs validated successfully.");
            // If a new poster is selected, upload it and update Firestore
            if (imageUri != null) {
                uploadPosterToStorage(true);
            }
            Map<String, Object> updates = collectEventData();
            db.collection("events").document(eventId).update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Event updated successfully.", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.action_OrgEditEvent_to_OrgEvent, getArguments());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("OrgEditEvent", "Failed to update event: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to update event.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void cancelChanges(View view) {
        if (tempPosterURL != null && !tempPosterURL.isEmpty()) {
            Glide.with(this).load(tempPosterURL).into(posterEditView);
        }
        imageUri = null; // Discard the new poster
        Toast.makeText(getContext(), "Changes canceled.", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.action_OrgEditEvent_to_OrgEvent, getArguments());
    }

    private void uploadPosterToStorage(boolean updateFirestore) {
        if (imageUri == null) return;

        String storagePath = "event_posters/" + eventId + ".jpg";
        FirebaseStorage.getInstance().getReference(storagePath).putFile(imageUri)
                .addOnSuccessListener(task -> task.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    posterURL = uri.toString();
                    if (updateFirestore) {
                        updatePosterInFirestore();
                    }

                }))
                .addOnFailureListener(e -> {
                    Log.e("OrgEditEvent", "Failed to upload poster: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to upload poster.", Toast.LENGTH_SHORT).show();
                });
    }
    private void updatePosterInFirestore() {
        db.collection("events").document(eventId)
                .update("posterUrl", posterURL)
                .addOnSuccessListener(aVoid -> Log.d("OrgEditEvent", "Poster URL updated in Firestore"))
                .addOnFailureListener(e -> Log.e("OrgEditEvent", "Failed to update poster URL: " + e.getMessage()));
    }

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

        return valid;
    }

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
        return data;
    }

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

    private String formatTimestamp(Object timestamp) {
        if (timestamp instanceof com.google.firebase.Timestamp) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return format.format(((com.google.firebase.Timestamp) timestamp).toDate());
        }
        return "";
    }
    private boolean isEmpty(EditText input) {
        return input.getText().toString().trim().isEmpty();
    }

    private boolean isPositiveInteger(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

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
     * To turn a String into a Date data type
     * @param dateString
     * @return
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
}
