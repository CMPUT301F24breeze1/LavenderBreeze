package com.example.myapplication.organization;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.model.EntrantAdapter;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrgEventWaitingLst extends Fragment {

    private ListView listView;
    private EntrantAdapter entrantAdapter;
    private List<User> entrantList; // List of entrants
    private FirebaseFirestore db; // Firestore instance
    private String eventId; // Event ID to fetch the correct entrants
    private List<String> waitlist, selected;
    private int capacity;
    private CollectionReference eventsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_event_waiting_lst, container, false);

        // Initialize Firestore and views
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        listView = view.findViewById(R.id.event_waitlist_listview);
        entrantList = new ArrayList<>();

        // Check if arguments were passed and retrieve them
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            waitlist = getArguments().getStringArrayList("waitlist");
            selected = getArguments().getStringArrayList("selected");
            capacity = getArguments().getInt("capacity");

            // Ensure lists are initialized
            if (waitlist == null) {
                waitlist = new ArrayList<>();
            }
            if (selected == null) {
                selected = new ArrayList<>();
            }

            // Log or display the data to verify it is received correctly
            Log.d("OrgWaitlist", "Event ID: " + eventId);
            Log.d("OrgWaitlist", "Waitlist: " + waitlist);
            Log.d("OrgWaitlist", "Selected List: " + selected);
        } else {
            Log.e("OrgWaitlist", "No arguments passed to OrgWaitlist");
        }

        // Fetch and display entrants
        fetchEntrants();

        loadEventData();

        FloatingActionButton selectEntrantsButton = view.findViewById(R.id.button_select_entrants);
        selectEntrantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEntrants();
            }
        });

        Button showMapButton = view.findViewById(R.id.button_go_to_map_from_org_event_waiting_lst);
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEntrantMap();
            }
        });

        Button backButton = view.findViewById(R.id.button_go_to_event_from_org_event_waiting_lst);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Button sendNotificationButton = view.findViewById(R.id.button_go_to_notif_from_org_event_waiting_lst);
        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationToEntrants();
            }
        });

        return view;
    }

    private void loadEventData() {
        eventsRef.document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                waitlist = (ArrayList<String>) documentSnapshot.get("waitlist");
                selected = (ArrayList<String>) documentSnapshot.get("selectedEntrants");
                capacity = documentSnapshot.getLong("capacity").intValue();  // Example for capacity
                Log.d("Kenny", "Event data loaded");
            }
        }).addOnFailureListener(e -> Log.e("Kenny", "Error loading event data", e));
    }

    private void fetchEntrants() {
        // Fetch the waitlist from Firestore based on the event ID
        eventsRef.document(eventId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        waitlist = (ArrayList<String>) document.get("waitlist");
                        if (waitlist != null) {
                            // Fetch each entrant's data by their ID
                            for (String entrantId : waitlist) {
                                fetchEntrantData(entrantId);
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Error fetching entrants", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchEntrantData(String entrantId) {
        // Fetch user data for each entrant from Firestore
        CollectionReference usersRef = db.collection("users");
        usersRef.document(entrantId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User entrant = document.toObject(User.class);
                        entrantList.add(entrant);
                        updateListView(); // Update the list view when a new entrant is fetched
                    }
                }
            }
        });
    }

    private void updateListView() {
        // Create and set the adapter for the ListView
        if (entrantAdapter == null) {
            entrantAdapter = new EntrantAdapter(getContext(), entrantList);
            listView.setAdapter(entrantAdapter);
        } else {
            entrantAdapter.notifyDataSetChanged(); // Notify adapter of data change
        }
    }

    private void selectEntrants() {
        Log.d("Kenny", "Starting entrant selection...");

        // Ensure waitlist and selected lists are initialized
        if (waitlist == null) {
            Log.e("Kenny", "Waitlist is null!");
            Toast.makeText(getActivity(), "Waitlist is not initialized!", Toast.LENGTH_LONG).show();
            return;
        }
        if (selected == null) {
            Log.e("Kenny", "Selected list is null!");
            Toast.makeText(getActivity(), "Selected list is not initialized!", Toast.LENGTH_LONG).show();
            return;
        }

        // Check for empty waitlist
        if (waitlist.isEmpty()) {
            Toast.makeText(getActivity(), "Nobody has signed up for this event yet", Toast.LENGTH_LONG).show();
            return;
        }

        // Check for valid capacity
        if (capacity <= 0) {
            Toast.makeText(getActivity(), "Invalid event capacity!", Toast.LENGTH_LONG).show();
            return;
        }

        // Ensure no prior selection has occurred
        if (!selected.isEmpty()) {
            Toast.makeText(getActivity(), "Selection has already occurred for this event!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("Kenny", "Selecting entrants based on capacity: " + capacity);

        // Shuffle and select entrants based on capacity
        if (capacity >= waitlist.size()) {
            selected.addAll(waitlist);
            waitlist.clear();
            Log.d("Kenny", "Added all waitlisted entrants to selected.");
        } else {
            Collections.shuffle(waitlist);
            for (int i = 0; i < capacity; i++) {
                selected.add(waitlist.remove(0));
                Log.d("Kenny", "Added entrant " + selected.get(selected.size() - 1) + " to selected list.");
            }
        }

        // Ensure `eventsRef` and `eventId` are valid before updating Firestore
        if (eventsRef == null || eventId == null || eventId.isEmpty()) {
            Log.e("Kenny", "Firestore reference or event ID is null/empty. Cannot update database.");
            Toast.makeText(getActivity(), "Failed to update the database. Event ID is missing.", Toast.LENGTH_LONG).show();
            return;
        }

        // Prepare data for Firestore update
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("waitlist", waitlist);
        eventData.put("selectedEntrants", selected);

        // Update Firestore document
        eventsRef.document(eventId).update(eventData)
                .addOnSuccessListener(aVoid -> Log.d("Kenny", "Event updated successfully in Firestore."))
                .addOnFailureListener(e -> {
                    Log.e("Kenny", "Error updating event in Firestore", e);
                    Toast.makeText(getActivity(), "Failed to update event in Firestore.", Toast.LENGTH_LONG).show();
                });
    }


    private void sendNotificationToEntrants() {
        Toast.makeText(getContext(), "Notification sent to entrants in the waiting list", Toast.LENGTH_SHORT).show();
    }

    private void showEntrantMap() {
        // Implement the logic to show the map for the selected entrant

    }
}