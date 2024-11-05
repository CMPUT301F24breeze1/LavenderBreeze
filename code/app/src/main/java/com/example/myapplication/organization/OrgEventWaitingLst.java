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

        // Assume eventId is passed as an argument to this fragment
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }

        // Fetch and display entrants
        fetchEntrants();

        loadEventData();

        FloatingActionButton selectEntrantsButton = view.findViewById(R.id.button_select_entrants);
        selectEntrantsButton.setOnClickListener(view1 -> selectEntrants());

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
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            List<String> waitlist = event.getWaitlist();
                            if (waitlist != null) {
                                // Fetch each entrant's data by their ID
                                for (String entrantId : waitlist) {
                                    fetchEntrantData(entrantId);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
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
        Log.d("Kenny", "Selecting entrants...");
        if (waitlist.isEmpty()) {
            Toast.makeText(getActivity(), "Nobody has signed up for this event yet", Toast.LENGTH_LONG).show();
            return;
        }
        if (capacity == 0) {
            Toast.makeText(getActivity(), "Invalid event capacity!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!selected.isEmpty()) {
            Toast.makeText(getActivity(), "Selection has already occurred for this event!", Toast.LENGTH_LONG).show();
            return;
        }

        if (capacity >= waitlist.size()) {
            selected.addAll(waitlist);
            waitlist.clear();
            Log.d("Kenny", "Added all waitlisted entrants to selected");
        } else {
            Collections.shuffle(waitlist);
            for (int i = 0; i < capacity; i++) {
                selected.add(waitlist.remove(0));
                Log.d("Kenny", "Added " + selected.get(selected.size() - 1) + " to selected");
            }
        }

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("waitlist", waitlist);
        eventData.put("selectedEntrants", selected);

        eventsRef.document(eventId).update(eventData)
                .addOnSuccessListener(aVoid -> Log.d("Event", "Event updated successfully"))
                .addOnFailureListener(e -> Log.e("Event", "Error updating event", e));
    }

    private void sendNotificationToEntrants() {
        Toast.makeText(getContext(), "Notification sent to entrants in the waiting list", Toast.LENGTH_SHORT).show();
    }

    private void showEntrantMap() {
        // Implement the logic to show the map for the selected entrant

    }
}