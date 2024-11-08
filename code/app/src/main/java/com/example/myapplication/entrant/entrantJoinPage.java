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
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = (String) getArguments().getSerializable("eventID");
        }
        user = new User(requireContext(), null); // Initialize user and load data
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_join_page, container, false);

        ImageButton eventList = view.findViewById(R.id.backArrowButton);
        ImageButton expandDescriptionButton = view.findViewById(R.id.expandDescriptionButton);
        TextView organizerNameTextView = view.findViewById(R.id.organizerNameTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        Button addButton = view.findViewById(R.id.addButton);

        Event temp = new Event(eventID, loadedEvent -> { // Means of loading the event asynchronously
            if (loadedEvent != null) {
                Log.d("entrantJoinPage_LoadedEvent", "Loaded Event: " + loadedEvent.getEventId());
                organizerNameTextView.setText("Organized by: " + loadedEvent.getOrganizerId());
                eventDescriptionTextView.setText(loadedEvent.getEventDescription());
                eventDateTextView.setText("Date: " + loadedEvent.getEventStart());
                // Call function to add user to waitlist for event, and event to the requested list of user. (On join button press)
                addButton.setOnClickListener(v -> addEvent(loadedEvent));
            }
            else {
                Log.d("entrantJoinPage_LoadedEvent", "NULL");
            }
        });

        // Return to event list
        eventList.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_entrantJoinPage_to_entrantEventsList));

        expandDescriptionButton.setOnClickListener(v -> { // If description is long, user can choose whether or not to expand
            if (eventDescriptionTextView.getMaxLines() == 2) {
                eventDescriptionTextView.setMaxLines(Integer.MAX_VALUE);
                expandDescriptionButton.setImageResource(R.drawable.arrow_up);
            } else {
                eventDescriptionTextView.setMaxLines(2);
                expandDescriptionButton.setImageResource(R.drawable.arrow_down);
            }
        });

        return view;
    }

    private void addEvent(Event event) {
        if (event != null) {
            Log.d("EntrantAddPage", "User: " + user.getRequestedEvents());
            user.addRequestedEvent(event.getEventId());
            event.addToWaitlist(user.getDeviceID());

            Navigation.findNavController(requireView()).navigate(R.id.action_entrantJoinPage_to_entrantEventsList);
        }
    }
}
