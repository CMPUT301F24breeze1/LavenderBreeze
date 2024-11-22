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
import com.example.myapplication.model.Facility;
import com.google.firebase.firestore.CollectionReference;
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

    private EditText editFacilityName, editFacilityAddress, editFacilityEmail, editFacilityPhoneNumber;
    private ImageView facilityProfileImage;
    private Uri profileImageUri = null;
    private String organizerId = "yourOrganizerId"; // Replace with actual organizer ID logic
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

        // Initialize views
        editFacilityName = view.findViewById(R.id.editTextFacilityName);
        editFacilityAddress = view.findViewById(R.id.editTextFacilityAddress);
        editFacilityEmail = view.findViewById(R.id.editTextFacilityEmail);
        editFacilityPhoneNumber = view.findViewById(R.id.editTextFacilityPhone);
        facilityProfileImage = view.findViewById(R.id.facilityProfileImage);

        Button buttonToHome = view.findViewById(R.id.button_go_to_home_from_add_facility);
        buttonToHome.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_orgAddFacility_to_home)
        );

        Button buttonSelectProfileImage = view.findViewById(R.id.buttonSelectProfileImage);
        Button createFacilityButton = view.findViewById(R.id.buttonCreateFacility);

        buttonSelectProfileImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        createFacilityButton.setOnClickListener(v -> saveOrUpdateFacility());

        fetchExistingFacility();

        return view;
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
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        existingFacility = document.toObject(Facility.class);

                        if (existingFacility != null) {
                            // Set the facility ID for future updates
                            existingFacility.setFacilityId(document.getId());

                            // Populate fields with the existing facility data
                            editFacilityName.setText(existingFacility.getFacilityName());
                            editFacilityAddress.setText(existingFacility.getFacilityAddress());
                            editFacilityEmail.setText(existingFacility.getFacilityEmail());
                            editFacilityPhoneNumber.setText(existingFacility.getFacilityPhoneNumber());

                            // Load profile image if available
                            if (existingFacility.getProfileImageUrl() != null && !existingFacility.getProfileImageUrl().isEmpty()) {
                                Picasso.get().load(existingFacility.getProfileImageUrl()).into(facilityProfileImage);
                            }
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
    private void populateFieldsWithExistingFacility() {
        editFacilityName.setText(existingFacility.getFacilityName());
        editFacilityAddress.setText(existingFacility.getFacilityAddress());
        editFacilityEmail.setText(existingFacility.getFacilityEmail());
        editFacilityPhoneNumber.setText(existingFacility.getFacilityPhoneNumber());

        if (existingFacility.getProfileImageUrl() != null) {
            Picasso.get().load(existingFacility.getProfileImageUrl()).into(facilityProfileImage);
        }
    }

    /**
     * Called when organizer attempts to save their facility.
     * Ensures information is of proper type.
     */
    private void saveOrUpdateFacility() {
        String facilityName = editFacilityName.getText().toString().trim();
        String facilityAddress = editFacilityAddress.getText().toString().trim();
        String facilityEmail = editFacilityEmail.getText().toString().trim();
        String facilityPhoneNumber = editFacilityPhoneNumber.getText().toString().trim();

        if (facilityName.isEmpty() || facilityAddress.isEmpty() || facilityEmail.isEmpty() || facilityPhoneNumber.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (profileImageUri != null) {
            uploadImageAndSaveOrUpdate(facilityName, facilityAddress, facilityEmail, facilityPhoneNumber);
        } else {
            saveOrUpdateFacilityDetails(facilityName, facilityAddress, facilityEmail, facilityPhoneNumber, existingFacility != null ? existingFacility.getProfileImageUrl() : null);
        }
    }

    /**
     * Method to be called when there is an attempt to upload a poster
     * @param name facility name
     * @param address poster url
     * @param email organizer's email
     * @param phone organizer's phone number
     */
    private void uploadImageAndSaveOrUpdate(String name, String address, String email, String phone) {
        StorageReference profileImageRef = storage.getReference().child("facilities/" + organizerId + "/profile.jpg");
        profileImageRef.putFile(profileImageUri)
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveOrUpdateFacilityDetails(name, address, email, phone, uri.toString()))
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to get image URL", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    /**
     * Method to be called when there is an attempt to edit information about facility
     * @param name facility name
     * @param address poster url
     * @param email organizer's email
     * @param phone organizer's phone number
     */
    private void saveOrUpdateFacilityDetails(String name, String address, String email, String phone, String imageUrl) {
        if (existingFacility != null) {
            // Update existing facility
            existingFacility.setFacilityName(name);
            existingFacility.setFacilityAddress(address);
            existingFacility.setFacilityEmail(email);
            existingFacility.setFacilityPhoneNumber(phone);
            existingFacility.setProfileImageUrl(imageUrl);

            // Check that the facility has a valid ID before updating
            if (existingFacility.getFacilityId() != null && !existingFacility.getFacilityId().isEmpty()) {
                db.collection("facilities").document(existingFacility.getFacilityId())
                        .set(existingFacility)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Facility updated successfully", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigate(R.id.action_orgAddFacility_to_OrgEventLst);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("OrgAddFacility", "Failed to update facility", e);
                            Toast.makeText(getContext(), "Failed to update facility", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Log.e("OrgAddFacility", "No facility ID found for existing facility");
                Toast.makeText(getContext(), "Facility ID is missing; cannot update.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Create a new facility
            Facility newFacility = new Facility(name, address, email, phone, organizerId, imageUrl);
            db.collection("facilities").add(newFacility)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Facility saved successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.action_orgAddFacility_to_OrgEventLst);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("OrgAddFacility", "Failed to save new facility", e);
                        Toast.makeText(getContext(), "Failed to save facility", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}
