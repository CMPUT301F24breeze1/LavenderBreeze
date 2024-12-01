// From chatgpt, openai, "write a java implementation with java documentation of AdminPicturesList
//class with methods to show the images of users and events
//given here is the xml code for it", 2024-11-27
package com.example.myapplication.view.admin;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.controller.AdminImageAdapter;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.Facility;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fragment that displays a list of images, either user profile pictures or event posters,
 * and provides navigation options for admin functionalities.
 */
public class AdminPicturesList extends Fragment {

    private enum PhotoType { USERS, EVENTS }

    private List<String> documentIds = new ArrayList<>();
    private String currentType = "users"; // Default case
    private ListView imageList;
    private Button buttonBack;
    private ImageButton buttonUsers;
    private ImageButton buttonEvents;
    private ImageButton buttonFacilities;
    private AdminImageAdapter imageAdapter;
    private Button userButton, eventButton;

    /**
     * Default constructor for the fragment.
     */
    public AdminPicturesList() {
        // Required empty public constructor
    }
    /**
     * Called to initialize the fragment when it is created.
     * @param savedInstanceState Bundle containing the saved instance state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * Inflates the fragment layout and sets up UI elements and event listeners.
     *
     * @param inflater           LayoutInflater to inflate the view.
     * @param container          ViewGroup container for the fragment.
     * @param savedInstanceState Bundle containing the saved instance state.
     * @return the inflated view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_pictures_list, container, false);

        imageList = view.findViewById(R.id.photo_list_view);

        buttonBack = view.findViewById(R.id.button_go_to_home_from_admin_pictures_list);
        buttonBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_adminPicturesList_to_home);
        });

        buttonEvents = view.findViewById(R.id.events);
        buttonEvents.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_adminPicturesList_to_adminEventsList);
        });

        buttonFacilities = view.findViewById(R.id.facilities);
        buttonFacilities.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_adminPicturesList_to_adminFacilitiesList);
        });

        buttonUsers = view.findViewById(R.id.users);
        buttonUsers.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_adminPicturesList_to_adminUsersList);
        });

        // Load data based on the default type
        setupFilterButtons(view);
        fetchDocuments(currentType);

        return view;
    }
    /**
     * Sets up filter buttons to toggle between user photos and event posters.
     *
     * @param view The root view of the fragment.
     */
    private void setupFilterButtons(View view) {
        view.findViewById(R.id.button_user_photos).setOnClickListener(v ->{
            fetchDocuments("users");
            currentType = "users";});
        view.findViewById(R.id.button_event_posters).setOnClickListener(v -> {
            fetchDocuments("events");
            currentType = "events";
        });
    }
    /**
     * Fetches document IDs from the Firestore database based on the selected photo type.
     * @param type The type of photos to display ("users" or "events").
     */
    private void fetchDocuments(String type) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collection = (Objects.equals(type, "users")) ? "users" : "events";

        db.collection(collection)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        documentIds.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            if(type.equals("users")) {
                                String profilePicture = document.getString("profilePicture");
                                if (profilePicture != null && !profilePicture.isEmpty()) {
                                    //Log.d("AdminPicturesList", "profile picture: " + document.getString("profilePicture"));
                                    documentIds.add(document.getId()); // Add each document ID
                                }
                            }
                            else if (type.equals("events")) {
                                String posterUrl = document.getString("posterUrl");
                                if (posterUrl != null && !posterUrl.isEmpty()) {
                                    //Log.d("AdminPicturesList", "poster url: " + document.getString("posterUrl"));
                                    documentIds.add(document.getId()); // Add each document ID
                                }
                            }
                        }
                        if (documentIds.isEmpty()) {
                            showEmptyState(); // Show a message if no images
                        } else {
                            hideEmptyState(); // Hide the message if images are present
                        }
                        setupAdapter();
                    } else {
                        Log.d("AdminPicturesList", "Error fetching documents: ", task.getException());
                    }
                });
    }

    /**
     * Sets up the adapter and attaches it to the ListView.
     */
    private void setupAdapter() {
        imageAdapter = new AdminImageAdapter(requireContext(), documentIds,currentType);
        imageList.setAdapter(imageAdapter);

        // Set click listener to open a dialog with details
        imageList.setOnItemClickListener((parent, view, position, id) -> {
            String documentId = documentIds.get(position);
            Log.d("AdminPicturesList", "Clicked on document ID: " + documentId);
            Bundle bundle = new Bundle();
            bundle.putString("documentId", documentId);
            bundle.putString("currentType", currentType);

            // Navigate to the image details fragment
            Navigation.findNavController(view).navigate(R.id.action_to_imageDetails, bundle);
        });
    }
    /**
     * Displays a message when no images are found for the selected type.
     */
    private void showEmptyState() {
        imageList.setVisibility(View.GONE); // Hide the ListView
        TextView emptyStateView = requireView().findViewById(R.id.empty_state_text);
        emptyStateView.setVisibility(View.VISIBLE); // Show the "No images" message
    }

    /**
     * Hides the message when images are available for the selected type.
     */
    private void hideEmptyState() {
        imageList.setVisibility(View.VISIBLE); // Show the ListView
        TextView emptyStateView = requireView().findViewById(R.id.empty_state_text);
        emptyStateView.setVisibility(View.GONE); // Hide the "No images" message
    }
}
