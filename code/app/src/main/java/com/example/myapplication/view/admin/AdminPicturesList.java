package com.example.myapplication.view.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.controller.AdminImageAdapter;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fragment that displays user or event photos.
 */
public class AdminPicturesList extends Fragment {

    private enum PhotoType { USERS, EVENTS }

    private List<String> documentIds = new ArrayList<>();
    private String currentType = "users"; // Default case
    private ListView imageList;
    private AdminImageAdapter imageAdapter;
    private Button userButton, eventButton;

    public AdminPicturesList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_pictures_list, container, false);

        imageList = view.findViewById(R.id.photo_list_view);

        // Load data based on the default type
        setupFilterButtons(view);
        fetchDocuments(currentType);

        return view;
    }
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
     * Fetches document IDs from the appropriate collection based on the photo type.
     * @param type The type of photos to display (users or events).
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
                            if(type.equals("users")&& !document.getString("profilePicture").isEmpty()) {
                                //Log.d("AdminPicturesList", "profile picture: " + document.getString("profilePicture"));
                                documentIds.add(document.getId()); // Add each document ID
                            }
                            else if(type.equals("events") && !document.getString("posterUrl").isEmpty()) {
                                //Log.d("AdminPicturesList", "poster url: " + document.getString("posterUrl"));
                                documentIds.add(document.getId()); // Add each document ID
                            }
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
        });
    }


}
