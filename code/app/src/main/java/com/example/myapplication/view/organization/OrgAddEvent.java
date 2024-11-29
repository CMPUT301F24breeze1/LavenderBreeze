package com.example.myapplication.view.organization;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.example.myapplication.controller.DeviceUtils;
import com.example.myapplication.controller.QRCodeGenerator;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.Facility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.example.myapplication.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Fragment that allow organizers to create an event details they wish to have.
 * Prompting the organizer to add Event Name, Details, Location, Capacity, Price, Poster URl
 * Event Start and End Date, Registration Start and End Date
 */
public class OrgAddEvent extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public EditText editTextEventName, editTextEventDescription, editTextLocation, editTextCapacity, editTextPrice;
    public EditText editTextEventStart, editTextEventEnd, editTextRegistrationStart, editTextRegistrationEnd;
    public EditText editWaitingListCap;
    private ImageView posterImageView;
    private FirebaseFirestore database;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    public OrgAddEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment org_add_event.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgAddEvent newInstance(String param1, String param2) {
        OrgAddEvent fragment = new OrgAddEvent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Initialize FirebaseFirestore
        database = FirebaseFirestore.getInstance();
        if (database == null) {
            Log.e("OrgAddEvent", "FirebaseFirestore is null");
            showToast("Firestore initialization failed.");
            return;
        }
        initializeImagePicker();
    }

    /**
     * Inflates the view, sets up UI elements, and configures click listeners for navigation
     * Allows organizer to enter information about the event, including uploading a poster
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_add_event, container, false);

        // Initialize Firestore database
        database = FirebaseFirestore.getInstance();

        // Bind views
        bindUIElements(view);

        fetchFacilityName();

        setupEventHandlers(view);

        return view;
    }
    private void bindUIElements(View view) {
        editTextEventName = view.findViewById(R.id.editTextEventName);
        editTextEventDescription = view.findViewById(R.id.editTextEventDescription);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        editTextCapacity = view.findViewById(R.id.editTextCapacity);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextEventStart = view.findViewById(R.id.editTextEventStart);
        editTextEventEnd = view.findViewById(R.id.editTextEventEnd);
        editTextRegistrationStart = view.findViewById(R.id.editTextRegistrationStart);
        editTextRegistrationEnd = view.findViewById(R.id.editTextRegistrationEnd);
        posterImageView = view.findViewById(R.id.imageViewPoster);
        editWaitingListCap = view.findViewById(R.id.editTextWaitingList);
    }
    private void setupEventHandlers(View view) {
        Button createEventButton = view.findViewById(R.id.buttonAddEvent);
        Button cancelButton = view.findViewById(R.id.buttonCancel);
        FloatingActionButton addPosterButton = view.findViewById(R.id.buttonAddPoster);

        createEventButton.setOnClickListener(v -> {
            if (validateFields()) {
                uploadPosterImage();
            } else {
                showToast("Please complete all fields correctly.");
            }
        });

        cancelButton.setOnClickListener(this::navigateToEventList);

        addPosterButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageLauncher.launch(Intent.createChooser(intent, "Select Event Poster"));
        });
    }
    private void initializeImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        posterImageView.setVisibility(View.VISIBLE);
                        posterImageView.setImageURI(imageUri);
                    }
                }
        );
    }

    /**
     * Validates the fields to make sure the information entered are safe
     * @return true
     */
    public boolean validateFields() {
        // added validation if waiting list is set
        if (!isEmpty(editWaitingListCap) && !isPositiveInteger(editWaitingListCap.getText().toString())) {
            return false;
        }
        return !isEmpty(editTextEventName) && !isEmpty(editTextEventDescription)
                && parseDate(editTextEventStart.getText().toString()) != null
                && parseDate(editTextEventEnd.getText().toString()) != null
                && parseDate(editTextRegistrationStart.getText().toString()) != null
                && parseDate(editTextRegistrationEnd.getText().toString()) != null
                && isPositiveInteger(editTextCapacity.getText().toString())
                && isPositiveDouble(editTextPrice.getText().toString());
    }


    /**
     * Allows the upload of Poster image and URL
     */
    private void uploadPosterImage() {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("event_posters/" + System.currentTimeMillis() + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> createEvent(uri.toString())))
                    .addOnFailureListener(e -> showToast("Failed to upload poster"));
        } else {
            showToast("No poster selected");
        }
    }


    private void fetchFacilityName() {
        String organizerID = DeviceUtils.getDeviceId(requireContext());

        database.collection("facilities")
                .whereEqualTo("organizerId", organizerID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String facilityName = document.getString("facilityName");
                        if (facilityName != null) {
                            editTextLocation.setText(facilityName);
                            editTextLocation.setEnabled(false); // Make the field non-editable
                        } else {
                            Log.e("OrgAddEvent", "facilityName is null in document");
                            showToast("No facility found for this organizer.");
                        }
                    } else {
                        Log.e("OrgAddEvent", "No documents found for organizerId: " + organizerID);
                        showToast("No facility found for this organizer.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgAddEvent", "Error fetching facility name", e);
                    showToast("Error fetching facility name.");
                });
    }

    /**
     * To create the event, generate QR Code, save to Facility if possible, and save to Firestore Firebase
     * @param posterUrl
     */
    private void createEvent(String posterUrl) {
        String organizerID = DeviceUtils.getDeviceId(requireContext());
        String eventName = editTextEventName.getText().toString();
        Date eventStartDate = parseDate(editTextEventStart.getText().toString());
        Date eventEndDate = parseDate(editTextEventEnd.getText().toString());
        Date registrationStartDate = parseDate(editTextRegistrationStart.getText().toString());
        Date registrationEndDate = parseDate(editTextRegistrationEnd.getText().toString());
        String location = editTextLocation.getText().toString();
        int capacity = Integer.parseInt(editTextCapacity.getText().toString());
        double price = Double.parseDouble(editTextPrice.getText().toString());

        // Determine waiting list settings
        boolean waitingListLimited = false;
        int waitingListCap = 0;
        if (!isEmpty(editWaitingListCap)) {
            waitingListCap = Integer.parseInt(editWaitingListCap.getText().toString());
            waitingListLimited = true;
        }


        Event event = new Event(eventName, editTextEventDescription.getText().toString(), eventStartDate,
                eventEndDate, registrationStartDate, registrationEndDate, location, capacity, price, posterUrl,
                "tempQRCode", organizerID, waitingListLimited, waitingListCap);

        // First, get the facility name and its ID
        database.collection("facilities")
                .whereEqualTo("organizerId", organizerID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot facilityDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String facilityId = facilityDoc.getId();
                        String facilityName = facilityDoc.getString("facilityName");

                        // Create event and add it to Firestore
                        database.collection("events")
                                .add(event.toMap())
                                .addOnSuccessListener(documentReference -> {
                                    String eventId = documentReference.getId();

                                    // Properly encode QR Code
                                    saveQRCodeToEvent(eventId);

                                    // Add the event to the facility's event list
                                    saveEventToFacility(facilityId, eventId);

                                    // Notify success and navigate
                                    showToast("Event created successfully!");
                                    navigateToEventList(requireView());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("OrgAddEvent", "Error creating event", e);
                                    showToast("Failed to create event");
                                });
                    } else {
                        showToast("Facility not found for this organizer.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgAddEvent", "Error fetching facility", e);
                    showToast("Failed to fetch facility.");
                });
    }

    /**
     * Save the generated eventId to the events array in the facility document.
     * This method adds only the eventId (not the full event object) to the facility's events list.
     *
     * @param facilityId The ID of the facility.
     * @param eventId The ID of the newly created event.
     */
    private void saveEventToFacility(String facilityId, String eventId) {
        // Update the "events" array in the facility document with the eventId
        database.collection("facilities")
                .document(facilityId)
                .update("events", FieldValue.arrayUnion(eventId))  // Save only the eventId
                .addOnSuccessListener(aVoid -> {
                    showToast("Event ID added to facility successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgAddEvent", "Error adding event ID to facility", e);
                });
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

    // New helper method to validate positive double values
    private boolean isPositiveDouble(String value) {
        try {
            return Double.parseDouble(value) > 0;
        } catch (NumberFormatException e) {
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
            showToast("Invalid date format. Please use yyyy-MM-dd HH:mm");
            return null; // Return null if parsing fails
        }
    }
    private void navigateToEventList(View view) {
        Navigation.findNavController(view).navigate(R.id.action_org_add_event_to_org_event_lst);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void saveQRCodeToEvent(String eventId) {
        QRCodeGenerator qrCode = new QRCodeGenerator(eventId);
        String eventQRCode = qrCode.getQRCodeAsBase64();
        database.collection("events")
                .document(eventId)
                .update("qrCode", eventQRCode)  // Save only the eventId
                .addOnSuccessListener(aVoid -> {
                    showToast("QR Code Generated Successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgAddEvent", "Error adding QR code to event", e);
                });
    }
}