package com.example.myapplication.view.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A fragment that displays the detail of an image,
 * showing whether it is an event poster or profile, and which event/user it comes from
 * It also has an option for admin to delete the image.
 */
public class AdminImageDetailsFragment extends Fragment {
    private String documentId;
    private String currentType;


    /**
     * Inflates the view for the fragment, sets up the ListView and buttons.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle with saved instance state
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_image_details, container, false);

        ImageView imageView = view.findViewById(R.id.image_view);
        TextView typeText = view.findViewById(R.id.image_type_text);
        Button backButton = view.findViewById(R.id.back_to_list_button);
        Button deleteImageButton = view.findViewById(R.id.delete_image_button);
        // Retrieve arguments
        Bundle args = getArguments();
        if (args != null) {
            documentId = args.getString("documentId");
            currentType = args.getString("currentType");
        }

        // Fetch image details
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collection = currentType.equals("users") ? "users" : "events";

        db.collection(collection).document(documentId).get().addOnSuccessListener(document -> {
            String imageUrl = currentType.equals("users")
                    ? document.getString("profilePicture")
                    : document.getString("posterUrl");
            Glide.with(requireContext()).load(imageUrl).into(imageView);
            // Fetch the name and set type text accordingly
            String displayText;
            if (currentType.equals("users")) {
                String userName = document.getString("name"); // Assuming there's a "userName" field
                displayText = "Profile Picture\nUser Name: " + userName;
            } else if (currentType.equals("events")) {
                String eventName = document.getString("eventName"); // Assuming there's an "eventName" field
                displayText = "Event Poster\nEvent Name: " + eventName;
            } else {
                displayText = "Unknown Type";
            }

            // Set the type text to include both the type and the corresponding name
            typeText.setText(displayText);
        });

        deleteImageButton.setOnClickListener(v -> deleteImage());

        // Back button handler
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        return view;
    }

    /**
     * This deletes the image based on the type of the image (user or event)
     * and sets the value to null in Firestore
     */
    private void deleteImage() {
        String collection = currentType.equals("users") ? "users" : "events";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection).document(documentId).update(
                currentType.equals("users") ? "profilePicture" : "posterUrl", null
        ).addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Image deleted successfully", Toast.LENGTH_SHORT).show();
            //Navigation.findNavController(requireView()).popBackStack(); // Navigate back to the image list
        }).addOnFailureListener(e -> {
            Log.e("AdminImageDetails", "Failed to delete image", e);
            Toast.makeText(getContext(), "Failed to delete image", Toast.LENGTH_SHORT).show();
        });
    }
}

