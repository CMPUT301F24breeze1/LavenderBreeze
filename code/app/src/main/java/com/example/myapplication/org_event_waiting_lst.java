package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class org_event_waiting_lst extends Fragment {

    private ListView listView;
    private EntrantAdapter entrantAdapter;
    private List<User> entrantList; // List of entrants
    private FirebaseFirestore db; // Firestore instance
    private String eventId; // Event ID to fetch the correct entrants

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_event_waiting_lst, container, false);

        // Initialize Firestore and views
        db = FirebaseFirestore.getInstance();
        listView = view.findViewById(R.id.waitlistListView);
        entrantList = new ArrayList<>();

        // Assume eventId is passed as an argument to this fragment
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }

        // Fetch and display entrants
        fetchEntrants();

        // Set item click listener for list items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedEntrant = entrantList.get(position);
                // Show entrant's map
                showEntrantMap(selectedEntrant);
            }
        });

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Button sendNotificationButton = view.findViewById(R.id.notifyButton);
        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationToEntrants();
            }
        });

        return view;
    }

    private void fetchEntrants() {
        // Fetch the waitlist from Firestore based on the event ID
        CollectionReference eventsRef = db.collection("events");
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

    private void sendNotificationToEntrants() {
        Toast.makeText(getContext(), "Notification sent to entrants in the waiting list", Toast.LENGTH_SHORT).show();
    }

    private void showEntrantMap(User selectedEntrant) {
        // Implement the logic to show the map for the selected entrant

    }
}
