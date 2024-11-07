package com.example.myapplication.organization;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
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
import java.util.Date;
import java.util.Locale;

import com.example.myapplication.DeviceUtils;
import com.example.myapplication.QRCodeGenerator;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserIDCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.myapplication.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgAddEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgAddEvent extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditText editTextEventName, editTextEventDescription, editTextLocation,
            editTextCapacity, editTextPrice, editTextPosterUrl,
            editTextEventStart, editTextEventEnd,
            editTextRegistrationStart, editTextRegistrationEnd;

    private FirebaseFirestore database;
    private String eventQRCode;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_add_event, container, false);

        // Initialize Firestore database
        database = FirebaseFirestore.getInstance();

        // Bind views
        editTextEventName = view.findViewById(R.id.editTextEventName);
        editTextEventDescription = view.findViewById(R.id.editTextEventDescription);
        editTextEventStart = view.findViewById(R.id.editTextEventStart);
        editTextEventEnd = view.findViewById(R.id.editTextEventEnd);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        editTextCapacity = view.findViewById(R.id.editTextCapacity);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextRegistrationStart = view.findViewById(R.id.editTextRegistrationStart);
        editTextRegistrationEnd = view.findViewById(R.id.editTextRegistrationEnd);

        Button buttonCreateEvent = view.findViewById(R.id.buttonAddEvent);  // Create Event button
        Button buttonCancel = view.findViewById(R.id.buttonCancel);  // Cancel button

        buttonCreateEvent.setOnClickListener(v -> {
            // Validate input fields first
            if (validateFields()) {
                // If the form is valid, upload poster (if selected) and create the event
                uploadPosterImage();
            } else {
                // Show a toast if validation fails
                Toast.makeText(requireContext(), "Please complete all fields before submitting.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(v -> {
            // Handle cancel action, e.g., navigate back to the event list
            Navigation.findNavController(v).navigate(R.id.action_org_add_event_to_org_event_lst);
        });


        FloatingActionButton buttonAddPoster = view.findViewById(R.id.buttonAddPoster);
        buttonAddPoster.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            pickImageLauncher.launch(Intent.createChooser(intent, "Select Event Poster"));
        });

        ImageView posterImageView = view.findViewById(R.id.imageViewPoster);
        // Initialize the image picker launcher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        posterImageView.setVisibility(View.VISIBLE);
                        posterImageView.setImageURI(imageUri); // Display selected image in ImageView

                        // Call the method to upload the image to Firebase Storage
                        uploadPosterImage();
                    }
                }
        );

        return view;
    }

    public boolean validateFields() {
        String eventName = editTextEventName.getText().toString();
        String eventDescription = editTextEventDescription.getText().toString();
        String eventLocation = editTextLocation.getText().toString();
        String eventCapacityString = editTextCapacity.getText().toString();
        String eventPriceString = editTextPrice.getText().toString();
        String eventStart = editTextEventStart.getText().toString();
        String eventEnd = editTextEventEnd.getText().toString();
        String registrationStart = editTextRegistrationStart.getText().toString();
        String registrationEnd = editTextRegistrationEnd.getText().toString();

        // Check required fields for emptiness
        if (eventName.isEmpty() || eventDescription.isEmpty() || eventLocation.isEmpty() || eventStart.isEmpty() ||
                eventEnd.isEmpty() || registrationStart.isEmpty() || registrationEnd.isEmpty()) {
            return false;  // Return false if any required field is empty
        }

        // Check if eventCapacity and eventPrice are valid integers
        if (eventCapacityString.isEmpty() || eventPriceString.isEmpty()) {
            return false;  // Return false if the capacity or price fields are empty
        }

        try {
            int eventCapacity = Integer.parseInt(eventCapacityString);
            int eventPrice = Integer.parseInt(eventPriceString);

            // Further validation on the parsed values
            if (eventCapacity <= 0 || eventPrice < 0) {
                return false;  // Invalid if capacity is less than or equal to 0 or price is negative
            }

        } catch (NumberFormatException e) {
            return false;  // Return false if parsing the number fails (invalid input)
        }

        // If all validations pass
        return true;
    }


    // Upload Poster Image and Trigger Event Creation after Upload
    private void uploadPosterImage() {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("event_posters/" + System.currentTimeMillis() + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String posterUrl = uri.toString();  // Get the image URL

                                // Once the image upload is complete, call createEvent
                                createEvent(posterUrl); // Pass the poster URL after upload completes
                            }))
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to upload poster", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(requireContext(), "No poster selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Create Event Method
    public void createEvent(String eventPosterURL) {
        // Retrieve other event information
        String eventName = editTextEventName.getText().toString();
        String eventDescription = editTextEventDescription.getText().toString();
        String eventLocation = editTextLocation.getText().toString();
        int eventCapacity = Integer.parseInt(editTextCapacity.getText().toString());
        int eventPrice = Integer.parseInt(editTextPrice.getText().toString());
        Date eventStart = parseDate(editTextEventStart.getText().toString());
        Date eventEnd = parseDate(editTextEventEnd.getText().toString());
        Date eventRegistrationStart = parseDate(editTextRegistrationStart.getText().toString());
        Date eventRegistrationEnd = parseDate(editTextRegistrationEnd.getText().toString());

        // Validate event data (including poster URL)
        if (!validateEventData(eventName, eventDescription, eventLocation, eventPosterURL,
                eventCapacity, eventPrice, eventStart, eventEnd, eventRegistrationStart, eventRegistrationEnd)) {
            Toast.makeText(requireContext(), "Invalid input. Please check your data.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate QR Code
//        QRCodeGenerator qrCode = new QRCodeGenerator(eventName);
//        String eventQRCode = qrCode.getQRCodeAsBase64();
        String eventQRCode = "temp";

        // Create event
        String organizerID = DeviceUtils.getDeviceId(requireContext());
        Event event = new Event(eventName, eventDescription, eventStart, eventEnd, eventRegistrationStart,
                eventRegistrationEnd, eventLocation, eventCapacity, eventPrice, eventPosterURL,
                eventQRCode, organizerID);
        event.saveEvent();
        Toast.makeText(requireContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).navigate(R.id.action_org_add_event_to_org_event_lst);
    }

    public boolean validateEventData(String eventName, String eventDescription, String eventLocation, String eventPosterURL,
                                     int eventCapacity, int eventPrice,
                                     Date eventStart, Date eventEnd, Date registrationStart, Date registrationEnd) {

        // Check required fields
        if (eventName == null || eventName.isEmpty()) return false;
        if (eventDescription == null || eventDescription.isEmpty()) return false;
        if (eventLocation == null || eventLocation.isEmpty()) return false;
        if (eventPosterURL == null || eventPosterURL.isEmpty()) return false;

        // Validate capacity and price
        if (eventCapacity <= 0) return false;
        if (eventPrice < 0) return false;

        // Validate date fields
        if (eventStart == null || eventEnd == null || registrationStart == null || registrationEnd == null) return false;
        if (eventStart.after(eventEnd)) return false;
        if (registrationStart.after(registrationEnd)) return false;
        if (registrationEnd.after(eventStart)) return false;

        // All validations passed
        return true;
    }

    public Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            //Log.e("org_add_event", "Date parsing error", e);
            //Toast.makeText(getContext(), "Invalid date format. Please use yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show();
            return null; // Return null if parsing fails
        }
    }
}