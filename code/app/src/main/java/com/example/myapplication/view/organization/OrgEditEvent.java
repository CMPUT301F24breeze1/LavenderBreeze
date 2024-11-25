package com.example.myapplication.view.organization;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Fetch event data and populate fields
        fetchEventInformation();

        buttonSaveEvent.setOnClickListener(v -> saveEventChanges());

        return view;
    }

    private void fetchEventInformation(){
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(getContext(), "Event ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        populateFields(documentSnapshot);
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

    private void saveEventChanges() {
        Map<String, Object> updatedEvent = new HashMap<>();
        updatedEvent.put("eventName", editEventName.getText().toString());
        updatedEvent.put("eventDescription", editEventDescription.getText().toString());

        // Convert EditText input to Firestore Timestamps
        updatedEvent.put("eventStart", parseDateToTimestamp(editEventStart.getText().toString()));
        updatedEvent.put("eventEnd", parseDateToTimestamp(editEventEnd.getText().toString()));
        updatedEvent.put("registrationStart", parseDateToTimestamp(editRegistrationStart.getText().toString()));
        updatedEvent.put("registrationEnd", parseDateToTimestamp(editRegistrationEnd.getText().toString()));

        updatedEvent.put("location", editLocation.getText().toString());
        updatedEvent.put("capacity", Integer.parseInt(editCapacity.getText().toString()));
        updatedEvent.put("price", Double.parseDouble(editPrice.getText().toString()));

        db.collection("events").document(eventId).update(updatedEvent)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event updated successfully!", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_OrgEditEvent_to_OrgEvent, getArguments());
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgEditEvent", "Error updating event: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Failed to update event.", Toast.LENGTH_SHORT).show();
                });
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