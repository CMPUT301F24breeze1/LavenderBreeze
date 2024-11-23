// From chatgpt, openai, "write a java implementation with java documentation of EntrantEdit Profile
// Class with methods to edit profile information
// given here is the xml code for it", 2024-11-02
package com.example.myapplication.view.entrant;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A fragment that provides the interface for editing a user's profile, including
 * updating the profile picture, name, email, phone number, and email notification settings.
 */

public class EntrantEditProfile extends Fragment {
    private Button doneEditButton;
    private ImageButton editPhotoButton, editNameButton, editEmailButton, editPhoneButton;
    private ImageView profilePicture;
    private TextView personName, emailAddress, contactPhoneNumber;
    private SwitchCompat notifSwitch;
    private User user;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageRef;
    Button homeButton, profileButton, eventsButton;

    /**
     * Default constructor required for instantiating the fragment.
     */
    public EntrantEditProfile() {
        // Required empty public constructor
    }
    /**
     * Initializes the fragment, retrieves the User data, and sets up Firebase storage reference.
     * @param savedInstanceState the saved instance state bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageRef = FirebaseStorage.getInstance().getReference();
        if(getArguments() != null) {
            user = (User) getArguments().getSerializable("updated_user");
        }
        if(user == null) {
            user = new User(requireContext(), () -> updateUserData());

        }
    }
    /**
     * Inflates the view for the fragment and sets up UI components, click listeners,
     * and updates the user data.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle with the saved instance state
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_edit_profile, container, false);
        intializeBottomNavButton(view);
        // Find the views
        doneEditButton = view.findViewById(R.id.doneEdit);
        editPhotoButton = view.findViewById(R.id.editPhotoButton);
        editNameButton = view.findViewById(R.id.editNameButton);
        editEmailButton = view.findViewById(R.id.editEmailButton);
        editPhoneButton = view.findViewById(R.id.editPhoneButton);
        profilePicture = view.findViewById(R.id.profilePicture);
        personName = view.findViewById(R.id.personName);
        emailAddress = view.findViewById(R.id.emailAddress);
        contactPhoneNumber = view.findViewById(R.id.contactPhoneNumber);
        notifSwitch = view.findViewById(R.id.emailNotificationSwitch);

        updateUserData();
        editPhotoButton.setOnClickListener(v -> showEditPhotoDialog());
        doneEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
                Bundle result = new Bundle();
                result.putSerializable("updated_user", user);
                getParentFragmentManager().setFragmentResult("requestKey", result);
                Navigation.findNavController(v).navigate(R.id.action_entrantEditProfile_to_entrantProfile3);
            }
        });

        // Set up edit buttons to open dialogs for editing
        editNameButton.setOnClickListener(v -> showEditDialog("Name", user.getName(), newValue -> {
            user.setName(newValue);
            updateUserData();
        }));

        editEmailButton.setOnClickListener(v -> showEditDialog("Email", user.getEmail(), newValue -> {
            user.setEmail(newValue);
            updateUserData();
        }));

        editPhoneButton.setOnClickListener(v -> showEditDialog("Phone Number", user.getPhoneNumber(), newValue -> {
            user.setPhoneNumber(newValue);
            updateUserData();
        }));

        // Set up the email notification switch
        notifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("Peter", "onCreateView: "+ isChecked);
            user.setToggleNotif(isChecked);
            user.updateToggleNotifInDatabase(isChecked, user.getDeviceID()); // Call method to update the database
        });

        return view;
    }


    /**
     * Initializes the bottom navigation buttons and their respective actions.
     * @param view the root view of the fragment
     */
    public void intializeBottomNavButton(View view){
        homeButton = view.findViewById(R.id.homeButton);
        profileButton = view.findViewById(R.id.profileButton);
        eventsButton = view.findViewById(R.id.eventsButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantEditProfile_to_home); // ID of the destination in nav_graph.xml
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantEditProfile_to_entrantProfile3); // ID of the destination in nav_graph.xml
            }
        });
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantEditProfile_to_entrantEventsList); // ID of the destination in nav_graph.xml
            }
        });
    }
    /**
     * Updates the user interface with the user's data.
     */
    private void updateUserData() {
        // Update the TextViews and Switch with User's data
        personName.setText(user.getName());
        emailAddress.setText(user.getEmail());
        contactPhoneNumber.setText(user.getPhoneNumber());
        user.loadProfilePictureInto(profilePicture,requireContext());
        // Example of setting an email notification switch (if stored in User class)
        notifSwitch.setChecked(user.isToggleNotif());
    }
    /**
     * Shows a dialog for editing a specified field.
     * @param fieldName the name of the field to edit
     * @param currentValue the current value of the field
     * @param listener a listener to handle completion of the edit action
     */
    private void showEditDialog(String fieldName, String currentValue, EditDialogFragment.OnEditCompleteListener listener) {
        EditDialogFragment dialog = EditDialogFragment.newInstance(fieldName, currentValue);
        dialog.setOnEditCompleteListener(listener);
        dialog.show(getParentFragmentManager(), "EditDialogFragment");
    }
    /**
     * Displays a dialog with options to upload, generate, or delete a profile picture.
     */
    private void showEditPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_photo_options, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Button uploadButton = dialogView.findViewById(R.id.button_upload_photo);
        Button aiButton = dialogView.findViewById(R.id.button_ai_generated_photo);
        Button deleteButton = dialogView.findViewById(R.id.button_delete_photo);

        uploadButton.setOnClickListener(v -> {
            dialog.dismiss();
            openImagePicker();
        });

        aiButton.setOnClickListener(v -> {
            dialog.dismiss();
            //generateAIPicture();
        });

        deleteButton.setOnClickListener(v -> {
            dialog.dismiss();
            deletePicture();
        });

        dialog.show();
    }

    /**
     * Opens the image picker to allow the user to select a profile picture from their device.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
    /**
     * Handles the result of the image picker activity.
     */
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        uploadImageToFirebase(selectedImageUri);
                    }
                }
            }
    );

    /**
     * Uploads the selected image to Firebase Storage.
     * @param imageUri the URI of the selected image
     */
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference profileImageRef = storageRef.child("images/" + user.getDeviceID() + ".jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        user.setProfilePicture(downloadUrl);
                        user.updateProfilePictureInDatabase();
                        user.loadProfilePictureInto(profilePicture,requireContext());
                        Toast.makeText(getContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload picture.", Toast.LENGTH_SHORT).show());
    }
    /**
     * Deletes the user's profile picture from Firebase Storage.
     */
    private void deletePicture() {
        StorageReference profileImageRef = storageRef.child("images/" + user.getDeviceID() + ".jpg");
        profileImageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // On successful deletion, update the database and UI
                    user.setProfilePicture(""); // Set to empty string
                    user.updateProfilePictureInDatabase();
                    user.loadProfilePictureInto(profilePicture, requireContext());
                    Toast.makeText(getContext(), "Profile picture deleted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(getContext(), "Failed to delete profile picture.", Toast.LENGTH_SHORT).show();
                });
    }


}