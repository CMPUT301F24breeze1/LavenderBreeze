package com.example.myapplication.view.organization;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
 * The OrgAddEvent class is a Fragment that provides a user interface for event organizers to create new events.
 * It allows organizers to input event details such as name, description, date, location, capacity, price,
 * poster image, and other settings. The event details are validated and saved to Firestore, and the associated
 * facility is updated accordingly.
 */
public class OrgAddEvent extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private EditText editTextEventName, editTextEventDescription, editTextLocation, editTextCapacity, editTextPrice;
    private EditText editTextEventStart, editTextEventEnd, editTextRegistrationStart, editTextRegistrationEnd;
    private EditText editWaitingListCap;
    public Button eventPickStartDate, eventPickEndDate, registrationPickStartDate, registrationPickEndDate;
    private ImageView posterImageView;
    private FirebaseFirestore database;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private SwitchCompat geoSwitch;

    /**
     * Default constructor for OrgAddEvent.
     */
    public OrgAddEvent() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrgAddEvent.
     */
    public static OrgAddEvent newInstance(String param1, String param2) {
        OrgAddEvent fragment = new OrgAddEvent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created. Initializes Firebase Firestore and the image picker.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
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
     * Called to initialize the fragment's UI components and event handlers.
     *
     * @param inflater           LayoutInflater to inflate the fragment's layout.
     * @param container          ViewGroup that contains the fragment's UI.
     * @param savedInstanceState Previous saved state, if any.
     * @return The root View of the fragment.
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

    /**
     * Binds UI elements in the fragment's view to their respective variables.
     *
     * @param view The root view of the fragment.
     */
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


        eventPickStartDate = view.findViewById(R.id.selectStartDateButton);
        eventPickEndDate = view.findViewById(R.id.selectEndDateButton);
        registrationPickStartDate = view.findViewById(R.id.selectRegistrationStartDateButton);
        registrationPickEndDate = view.findViewById(R.id.selectRegistrationEndDateButton);
        geoSwitch = view.findViewById(R.id.geolocationSwitch);
    }

    /**
     * Sets up event handlers for various UI interactions, such as button clicks.
     *
     * @param view The root view of the fragment.
     */
    private void setupEventHandlers(View view) {
        Button createEventButton = view.findViewById(R.id.buttonAddEvent);
        Button cancelButton = view.findViewById(R.id.buttonCancel);
        FloatingActionButton addPosterButton = view.findViewById(R.id.buttonAddPoster);

        // Set up Date Picker button handlers for event and registration dates
        eventPickStartDate.setOnClickListener(v -> showDateTimePicker(editTextEventStart));
        eventPickEndDate.setOnClickListener(v -> showDateTimePicker(editTextEventEnd));
        registrationPickStartDate.setOnClickListener(v -> showDateTimePicker(editTextRegistrationStart));
        registrationPickEndDate.setOnClickListener(v -> showDateTimePicker(editTextRegistrationEnd));

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

    /**
     * Initializes the image picker for selecting a poster image.
     */
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
     * Validates the fields entered by the user to ensure they meet the required conditions.
     *
     * @return {@code true} if all fields are valid; {@code false} otherwise.
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
     * Uploads the selected poster image to Firebase Storage and retrieves its URL.
     * Once uploaded, proceeds to create the event.
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

    /**
     * Fetches the facility name associated with the organizer's ID from Firestore
     * and updates the location field accordingly.
     */
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
     * Creates an event in Firestore using the provided details, generates a QR code,
     * and associates the event with the organizer's facility.
     *
     * @param posterUrl The URL of the uploaded poster image.
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
        boolean geoRequired = geoSwitch.isChecked();

        // Determine waiting list settings
        boolean waitingListLimited = false;
        int waitingListCap = 0;
        if (!isEmpty(editWaitingListCap)) {
            waitingListCap = Integer.parseInt(editWaitingListCap.getText().toString());
            waitingListLimited = true;
        }


        Event event = new Event(eventName, editTextEventDescription.getText().toString(), eventStartDate,
                eventEndDate, registrationStartDate, registrationEndDate, location, capacity, price, posterUrl,
                "tempQRCode", organizerID, waitingListLimited, waitingListCap, geoRequired);

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
     * Updates the "events" array in the specified facility document by adding the new event ID.
     *
     * @param facilityId The ID of the facility.
     * @param eventId    The ID of the newly created event.
     */
    private void saveEventToFacility(String facilityId, String eventId) {
        // Update the "events" array in the facility document with the eventId
        database.collection("facilities")
                .document(facilityId)
                .update("events", FieldValue.arrayUnion(eventId))  // Save only the eventId
                .addOnSuccessListener(aVoid -> {
                    Log.d("Event creation","Event ID added to facility successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgAddEvent", "Error adding event ID to facility", e);
                });
    }

    /**
     * Checks if the given EditText field is empty.
     *
     * @param input The EditText field to check.
     * @return {@code true} if the field is empty; {@code false} otherwise.
     */
    private boolean isEmpty(EditText input) {
        return input.getText().toString().trim().isEmpty();
    }

    /**
     * Validates whether the provided string represents a positive integer.
     *
     * @param value The string to validate.
     * @return {@code true} if the string is a positive integer; {@code false} otherwise.
     */
    public boolean isPositiveInteger(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates whether the provided string represents a positive double.
     *
     * @param value The string to validate.
     * @return {@code true} if the string is a positive double; {@code false} otherwise.
     */
    public boolean isPositiveDouble(String value) {
        try {
            return Double.parseDouble(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses a date string into a Date object using the format "yyyy-MM-dd HH:mm".
     *
     * @param dateString The date string to parse.
     * @return A Date object if parsing succeeds; {@code null} otherwise.
     */
    public Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(dateString);
        } catch (ParseException e) {
            Log.e("org_add_event", "Date parsing error", e);
            //showToast("Invalid date format. Please use yyyy-MM-dd HH:mm");
            return null;
        }
    }

    /**
     * Navigates the user to the event list screen.
     *
     * @param view The current view.
     */
    private void navigateToEventList(View view) {
        Navigation.findNavController(view).navigate(R.id.action_org_add_event_to_org_event_lst);
    }

    /**
     * Displays a Toast message to the user.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Saves the generated QR code for the event in Firestore.
     *
     * @param eventId The ID of the event.
     */
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

    /**
     * Displays a DatePicker and TimePicker dialog for selecting a date and time,
     * then sets the result on the specified EditText field.
     *
     * @param targetEditText The EditText field to set the selected date and time.
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

}