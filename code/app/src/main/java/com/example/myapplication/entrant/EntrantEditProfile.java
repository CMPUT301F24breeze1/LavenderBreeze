package com.example.myapplication.entrant;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantEditProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantEditProfile extends Fragment {
    private Button doneEditButton;
    private ImageButton editPhotoButton, editNameButton, editEmailButton, editPhoneButton;
    private ImageView profilePicture;
    private TextView personName, emailAddress, contactPhoneNumber;
    private SwitchCompat emailNotificationSwitch;
    private User user;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageRef;


    public EntrantEditProfile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageRef = FirebaseStorage.getInstance().getReference();
        user = new User(requireContext(), () -> updateUserData());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_edit_profile, container, false);

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
        emailNotificationSwitch = view.findViewById(R.id.emailNotificationSwitch);

        updateUserData();
        editPhotoButton.setOnClickListener(v -> showEditPhotoDialog());
        doneEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
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
        emailNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(requireContext(), "Email Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Email Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void updateUserData() {
        // Update the TextViews and Switch with User's data
        personName.setText(user.getName());
        emailAddress.setText(user.getEmail());
        contactPhoneNumber.setText(user.getPhoneNumber());
        user.loadProfilePictureInto(profilePicture);
        // Example of setting an email notification switch (if stored in User class)
        //emailNotificationSwitch.setChecked(user.getIsEntrant());
    }
    private void showEditDialog(String fieldName, String currentValue, EditDialogFragment.OnEditCompleteListener listener) {
        EditDialogFragment dialog = EditDialogFragment.newInstance(fieldName, currentValue);
        dialog.setOnEditCompleteListener(listener);
        dialog.show(getParentFragmentManager(), "EditDialogFragment");
    }
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
            //deletePicture();
        });

        dialog.show();
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
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
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference profileImageRef = storageRef.child("images/" + user.getDeviceID() + ".jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        user.setProfilePicture(downloadUrl);
                        user.updateProfilePictureInDatabase();
                        user.loadProfilePictureInto(profilePicture);
                        Toast.makeText(getContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload picture.", Toast.LENGTH_SHORT).show());
    }

}