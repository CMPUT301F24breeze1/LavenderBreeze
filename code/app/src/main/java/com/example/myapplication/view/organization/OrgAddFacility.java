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
 * Fragment for adding a Facility
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
     * Opens and populates a page for an organizer to add their facility to their organizer profile.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return the inflated view for the fragment
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
     * Initializes the UI components and sets up button listeners.
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
     * Loads the facility associated with the organizer's deviceID, if it exists
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
     * Sets up any fields for the facility that can be auto-populated
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
     * Handles the save button click to validate and save the facility.
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
     * Uploads the selected image to Firebase Storage and saves the facility.
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
     * Saves or updates the facility details in Firestore.
     */
    private void saveFacility(String name, String address, String email, String phone, String imageUrl) {
        if (existingFacility != null) {
            updateFacility(name, address, email, phone, imageUrl);
        } else {
            createFacility(name, address, email, phone, imageUrl);
        }
    }
    /**
     * Updates an existing facility in Firestore.
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
