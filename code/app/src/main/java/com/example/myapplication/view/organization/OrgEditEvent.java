package com.example.myapplication.view.organization;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgEditEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgEditEvent extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private FirebaseFirestore db;

    private EditText editEventName, editEventDescription, editEventStart, editEventEnd,
            editRegistrationStart, editRegistrationEnd, editLocation, editCapacity, editPrice;
    private Button buttonSaveEvent;
    private Button buttonCancelSave;
    ImageView posterEditView;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri imageUri;
    String posterURL;
    String tempPosterURl;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrgEditEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventID
     * @return A new instance of fragment org_edit_event.
     */
    // TODO: Rename and change types and number of parameters
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
            eventId = getArguments().getString("eventId");
            Log.d("OrgEditEvent", "Received eventId: " + eventId); // Log for debugging
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_edit_event, container, false);

        initializeImagePicker();

        // Initialize UI elements
        editEventName = view.findViewById(R.id.editEventName);
        editEventDescription = view.findViewById(R.id.editEventDescription);
        editEventStart = view.findViewById(R.id.editEventStart);
        editEventEnd = view.findViewById(R.id.editEventEnd);
        editRegistrationStart = view.findViewById(R.id.editRegistrationStart);
        editRegistrationEnd = view.findViewById(R.id.editRegistrationEnd);
        editLocation = view.findViewById(R.id.editLocation);
        editCapacity = view.findViewById(R.id.editCapacity);
        editPrice = view.findViewById(R.id.editPrice);
        buttonSaveEvent = view.findViewById(R.id.buttonSaveEvent);
        posterEditView = view.findViewById(R.id.imageViewEditPoster);

        // Fetch event data and populate fields
        fetchEventInformation();

        // Save the event
        buttonSaveEvent.setOnClickListener(v -> {
            // If a new poster is selected, upload it to Firebase Storage and update Firestore
            if (imageUri != null) {
                uploadImageToFirebase();
            } else {
                // If no new image is selected, proceed with the other event changes
                saveEventChanges()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // If the update was successful, navigate back to the event page
                                Log.d("OrgEvent", "Event updated successfully.");
                                Navigation.findNavController(v).navigate(R.id.action_OrgEditEvent_to_OrgEvent, getArguments());
                            } else {
                                // Handle failure if needed
                                Log.e("OrgEvent", "Error updating event", task.getException());
                                Toast.makeText(getContext(), "Failed to update event.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Cancel the edit and go back to Event View
        buttonCancelSave = view.findViewById(R.id.buttonCancel);
        buttonCancelSave.setOnClickListener(v -> {
            // Revert the poster to the old one
            if (tempPosterURl != null && !tempPosterURl.isEmpty()) {
                Glide.with(this).load(tempPosterURl).into(posterEditView); // Revert to the old poster
                Toast.makeText(getContext(), "Changes reverted.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("OrgEditEvent", "No previous poster found.");
                Toast.makeText(getContext(), "No previous poster to revert to.", Toast.LENGTH_SHORT).show();
            }
            // Navigate back without updating Firestore
            Navigation.findNavController(view).navigate(R.id.action_OrgEditEvent_to_OrgEvent, getArguments());
        });
        // Edit poster
        FloatingActionButton addPosterButton = view.findViewById(R.id.buttonEditPoster);
        addPosterButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageLauncher.launch(Intent.createChooser(intent, "Select Event Poster"));
        });


        return view;
    }
    private void initializeImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        posterEditView.setVisibility(View.VISIBLE);
                        posterEditView.setImageURI(imageUri);
                        uploadImageToFirebase();
                    }
                }
        );
    }
    private void uploadImageToFirebase() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "No image selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        String storagePath = "event_posters/" + eventId + ".jpg";
        FirebaseStorage.getInstance().getReference(storagePath).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        posterURL = uri.toString(); // Update the poster URL
                        tempPosterURl = posterURL; // Save the updated URL for cancel
                        Glide.with(this).load(posterURL).into(posterEditView); // Display the new image
                        updatePosterUrlInFirestore(posterURL)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        // After Firestore update, navigate back to the event page
                                        Log.d("OrgEvent", "Poster uploaded and Firestore updated successfully.");
                                        Navigation.findNavController(v).navigate(R.id.action_OrgEditEvent_to_OrgEvent, getArguments());
                                    } else {
                                        // Handle Firestore update failure
                                        Log.e("OrgEvent", "Error updating poster URL in Firestore", updateTask.getException());
                                        Toast.makeText(getContext(), "Failed to update poster URL in Firestore.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgEditEvent", "Error uploading poster: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Failed to upload poster.", Toast.LENGTH_SHORT).show();
                });
    }
    private Task<Void> updatePosterUrlInFirestore(String newPosterUrl) {
        // Assuming you're updating a document in Firestore
        DocumentReference eventRef = FirebaseFirestore.getInstance().collection("events").document(eventId);
        return eventRef.update("posterURL", newPosterUrl);
    }

    private void fetchEventInformation(){
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(getContext(), "Event ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        populateFields(documentSnapshot); // Where the fields are populated
                    } else {
                        Toast.makeText(getContext(), "Event not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgEditEvent", "Error fetching event: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Error fetching event data.", Toast.LENGTH_SHORT).show();
                });
    }
    private void populateFields(DocumentSnapshot documentSnapshot) {
        editEventName.setText(documentSnapshot.getString("eventName"));
        editEventDescription.setText(documentSnapshot.getString("eventDescription"));

        // Handle 'eventStart' as either a Timestamp or String
        editEventStart.setText(getDateFromField(documentSnapshot, "eventStart"));
        editEventEnd.setText(getDateFromField(documentSnapshot, "eventEnd"));
        editRegistrationStart.setText(getDateFromField(documentSnapshot, "registrationStart"));
        editRegistrationEnd.setText(getDateFromField(documentSnapshot, "registrationEnd"));

        editLocation.setText(documentSnapshot.getString("location"));
        editLocation.setEnabled(false);

        Long capacity = documentSnapshot.getLong("capacity");
        editCapacity.setText(capacity != null ? String.valueOf(capacity) : "");

        Double price = documentSnapshot.getDouble("price");
        editPrice.setText(price != null ? String.valueOf(price) : "");

        // Fetch and display the poster URL
        posterURL = documentSnapshot.getString("posterUrl");
        tempPosterURl = posterURL;
        Log.d("OrgEditEvent", "Poster URL: " + posterURL);
        if (posterURL != null && !posterURL.isEmpty()) {
            Glide.with(this)
                    .load(posterURL)
                    .into(posterEditView);
        }
    }

    // Helper method to extract date as a formatted string
    private String getDateFromField(DocumentSnapshot documentSnapshot, String fieldName) {
        Object fieldValue = documentSnapshot.get(fieldName);
        if (fieldValue instanceof com.google.firebase.Timestamp) {
            // Convert Timestamp to formatted date string
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return sdf.format(((com.google.firebase.Timestamp) fieldValue).toDate());
        } else if (fieldValue instanceof String) {
            // Return the String value directly
            return (String) fieldValue;
        }
        return ""; // Return an empty string if the field is null or not a recognized type
    }

    private Task<Void> saveEventChanges() {
        DocumentReference eventRef = FirebaseFirestore.getInstance().collection("events").document(eventId);

        Map<String, Object> updatedEvent = new HashMap<>();
        updatedEvent.put("eventName", editEventName.getText().toString());
        updatedEvent.put("eventDescription", editEventDescription.getText().toString());

        // Convert EditText input to Firestore Timestamps
        updatedEvent.put("eventStart", parseDateToTimestamp(editEventStart.getText().toString()));
        updatedEvent.put("eventEnd", parseDateToTimestamp(editEventEnd.getText().toString()));
        updatedEvent.put("registrationStart", parseDateToTimestamp(editRegistrationStart.getText().toString()));
        updatedEvent.put("registrationEnd", parseDateToTimestamp(editRegistrationEnd.getText().toString()));
        //updatedEvent.put("posterUrl", posterURL);
        updatedEvent.put("location", editLocation.getText().toString());
        updatedEvent.put("capacity", Integer.parseInt(editCapacity.getText().toString()));
        updatedEvent.put("price", Double.parseDouble(editPrice.getText().toString()));

        return eventRef.update(updatedEvent);
    }


    // Helper method to parse user input into Firestore Timestamps
    private com.google.firebase.Timestamp parseDateToTimestamp(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null; // Return null if no input
        }
        try {
            // Match user input format (e.g., "yyyy-MM-dd HH:mm")
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return new com.google.firebase.Timestamp(date);
        } catch (Exception e) {
            Log.e("OrgEditEvent", "Error parsing input date: " + e.getMessage(), e);
            return null; // Return null if parsing fails
        }
    }
}