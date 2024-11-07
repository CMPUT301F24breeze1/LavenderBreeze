package com.example.myapplication.entrant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class entrantJoinPage extends Fragment {

    private String eventID;
    private Event event;
    private User user;

//    private FirebaseFirestore db;
//    private CollectionReference cRef = db.collection("events");
//    private DocumentReference dRef;
//    private DocumentSnapshot ds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = (String) getArguments().getSerializable("eventID"); // Retrieve the event
        }
        user = new User(requireContext(), null); // Initialize user and load data
        event = new Event(eventID);
//        dRef = cRef.document(eventID);
//        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    ds = task.getResult();
//                    if (!ds.exists()) {
//                        Log.d("Event", "No such event in Firestore");
//                        Log.d("Event", ds.toString());
//                    }
//                }
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_join_page, container, false);

        // Reference UI elements
        ImageButton eventList = view.findViewById(R.id.backArrowButton);
        ImageButton expandDescriptionButton = view.findViewById(R.id.expandDescriptionButton);
        TextView organizerNameTextView = view.findViewById(R.id.organizerNameTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        Button addButton = view.findViewById(R.id.addButton);

        // Populate UI with event details
        if (event != null) {
            organizerNameTextView.setText("Organized by: " + event.getOrganizerId());
            eventDescriptionTextView.setText(event.getEventDescription());
            eventDateTextView.setText("Date: " + event.getEventStart()); // Format the date if needed
        }

        // Back button to navigate to the event list
        eventList.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_entrantJoinPage_to_entrantEventsList));

        // Expand description
        expandDescriptionButton.setOnClickListener(v -> {
            if (eventDescriptionTextView.getMaxLines() == 2) {
                eventDescriptionTextView.setMaxLines(Integer.MAX_VALUE);
                expandDescriptionButton.setImageResource(R.drawable.arrow_up);
            } else {
                eventDescriptionTextView.setMaxLines(2);
                expandDescriptionButton.setImageResource(R.drawable.arrow_down);
            }
        });

        // Add button to add event to waitlist
        addButton.setOnClickListener(v -> addEvent());

        return view;
    }

    private void addEvent() {
        if (event != null) {
            // Assuming you have a user instance
            //User user = new User(requireContext(), null); // or retrieve your existing user instance
            Log.d("EntrantAddPage", "User: " + user.getRequestedEvents());
            user.addRequestedEvent(event.getEventId()); // Adjust this method as necessary
            event.addToWaitlist(user.getDeviceID());

            // Navigate back to the event list
            Navigation.findNavController(requireView()).navigate(R.id.action_entrantJoinPage_to_entrantEventsList);
        }
    }
}
