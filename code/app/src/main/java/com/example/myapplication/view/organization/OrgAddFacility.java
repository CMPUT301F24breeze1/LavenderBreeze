package com.example.myapplication.view.organization;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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

import com.example.myapplication.R;
import com.example.myapplication.controller.DeviceUtils;
import com.example.myapplication.model.Facility;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

/**
 * Fragment for adding or editing a facility associated with an organizer.
 * Allows organizers to input facility details, select a profile image,
 * and save the information to Firestore. If a facility already exists
 * for the organizer, its details are preloaded for editing.
 */
public class OrgAddFacility extends Fragment {
    private static final String TAG = "OrgAddFacility";
    private EditText editFacilityName, editFacilityAddress, editFacilityEmail, editFacilityPhoneNumber;
    private Button selectImageButton, saveButton;
    private ImageView facilityProfileImage;
    private Uri profileImageUri = null;
    private String organizerId;
    private Facility existingFacility = null;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    profileImageUri = uri;
                    facilityProfileImage.setImageURI(uri);
                }
            }
    );

    /**
     * Inflates the layout for this fragment and initializes views and data.
     *
     * @param inflater The LayoutInflater object to inflate views in the fragment.
     * @param container The parent view that the fragment's UI should attach to.
     * @param savedInstanceState If non-null, the fragment is being re-constructed
     *                           from a previous saved state.
     * @return The inflated view for this fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_add_facility, container, false);

        organizerId  = DeviceUtils.getDeviceId(requireContext());

        // Initialize views
        initializeViews(view);

        fetchExistingFacility();

        return view;
    }

    /**
     * Initializes UI components and sets up click listeners for buttons.
     *
     * @param view The root view containing the fragment's UI elements.
     */
    private void initializeViews(View view) {
        editFacilityName = view.findViewById(R.id.editTextFacilityName);
        editFacilityAddress = view.findViewById(R.id.editTextFacilityAddress);
        editFacilityEmail = view.findViewById(R.id.editTextFacilityEmail);
        editFacilityPhoneNumber = view.findViewById(R.id.editTextFacilityPhone);
        facilityProfileImage = view.findViewById(R.id.facilityProfileImage);

        selectImageButton = view.findViewById(R.id.buttonSelectProfileImage);
        saveButton = view.findViewById(R.id.buttonCreateFacility);

        selectImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        saveButton.setOnClickListener(v -> handleSave());

        view.findViewById(R.id.button_go_to_home_from_add_facility)
                .setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_orgAddFacility_to_home));
    }

    /**
     * Fetches the existing facility associated with the organizer's ID from Firestore.
     * If a facility exists, its details are populated in the input fields.
     */
    private void fetchExistingFacility() {
        db.collection("facilities")
                .whereEqualTo("organizerId", organizerId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        existingFacility = doc.toObject(Facility.class);

                        if (existingFacility != null) {
                            existingFacility.setFacilityId(doc.getId());
                            populateFields(existingFacility);
                        }
                    } else {
                        Log.d("OrgAddFacility", "No facility found with organizerId: " + organizerId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgAddFacility", "Error fetching facility details", e);
                    Toast.makeText(getContext(), "Error fetching facility details", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Populates the input fields with the details of the existing facility.
     *
     * @param facility The existing facility to populate the fields with.
     */
    private void populateFields(Facility facility) {
        editFacilityName.setText(existingFacility.getFacilityName());
        editFacilityAddress.setText(existingFacility.getFacilityAddress());
        editFacilityEmail.setText(existingFacility.getFacilityEmail());
        editFacilityPhoneNumber.setText(existingFacility.getFacilityPhoneNumber());

        if (facility.getProfileImageUrl() != null && !facility.getProfileImageUrl().isEmpty()) {
            Picasso.get().load(facility.getProfileImageUrl()).into(facilityProfileImage);
        }
    }

    /**
     * Handles the save action, validating inputs and saving the facility.
     */
    private void handleSave() {
        String name = editFacilityName.getText().toString().trim();
        String address = editFacilityAddress.getText().toString().trim();
        String email = editFacilityEmail.getText().toString().trim();
        String phone = editFacilityPhoneNumber.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (profileImageUri != null) {
            uploadImageAndSave(name, address, email, phone);
        } else {
            saveFacility(name, address, email, phone, existingFacility != null ? existingFacility.getProfileImageUrl() : null);
        }
    }

    /**
     * Uploads the selected profile image to Firebase Storage and then saves the facility.
     *
     * @param name  The facility name.
     * @param address The facility address.
     * @param email The facility email.
     * @param phone The facility phone number.
     */
    private void uploadImageAndSave(String name, String address, String email, String phone) {
        StorageReference imageRef = storage.getReference().child("facilities/" + organizerId + "/profile.jpg");

        imageRef.putFile(profileImageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveFacility(name, address, email, phone, uri.toString()))
                        .addOnFailureListener(e -> showError("Failed to retrieve image URL")))
                .addOnFailureListener(e -> showError("Image upload failed"));
    }

    /**
     * Saves a new facility or updates an existing one in Firestore.
     *
     * @param name The facility name.
     * @param address The facility address.
     * @param email The facility email.
     * @param phone The facility phone number.
     * @param imageUrl The URL of the facility's profile image.
     */
    private void saveFacility(String name, String address, String email, String phone, String imageUrl) {
        if (existingFacility != null) {
            updateFacility(name, address, email, phone, imageUrl);
        } else {
            createFacility(name, address, email, phone, imageUrl);
        }
    }

    /**
     * Updates the existing facility's details in Firestore.
     *
     * @param name The facility name.
     * @param address The facility address.
     * @param email The facility email.
     * @param phone The facility phone number.
     * @param imageUrl The URL of the facility's profile image.
     */
    private void updateFacility(String name, String address, String email, String phone, String imageUrl) {
        existingFacility.setFacilityName(name);
        existingFacility.setFacilityAddress(address);
        existingFacility.setFacilityEmail(email);
        existingFacility.setFacilityPhoneNumber(phone);
        existingFacility.setProfileImageUrl(imageUrl);

        db.collection("facilities")
                .document(existingFacility.getFacilityId())
                .set(existingFacility)
                .addOnSuccessListener(aVoid -> navigateToEventList("Facility updated successfully"))
                .addOnFailureListener(e -> showError("Failed to update facility"));
    }

    /**
     * Creates a new facility in Firestore.
     *
     * @param name The facility name.
     * @param address The facility address.
     * @param email The facility email.
     * @param phone The facility phone number.
     * @param imageUrl The URL of the facility's profile image.
     */
    private void createFacility(String name, String address, String email, String phone, String imageUrl) {
        Facility newFacility = new Facility(name, address, email, phone, organizerId, imageUrl);

        db.collection("facilities")
                .add(newFacility)
                .addOnSuccessListener(docRef -> navigateToEventList("Facility saved successfully"))
                .addOnFailureListener(e -> showError("Failed to save facility"));
    }

    /**
     * Navigates to the event list with a success message.
     */
    private void navigateToEventList(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).navigate(R.id.action_orgAddFacility_to_OrgEventLst);
    }

    /**
     * Displays a toast for errors.
     */
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, message);
    }

}
