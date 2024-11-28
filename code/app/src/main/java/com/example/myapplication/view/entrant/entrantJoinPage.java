package com.example.myapplication.view.entrant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.controller.DeviceUtils;
import com.example.myapplication.controller.PermissionHelper;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class entrantJoinPage extends Fragment {

    private String eventID;
    private User user;
    private PermissionHelper permissionHelper;

    /**
     * Pulls eventID from bundle and initializes User object from deviceID
     * @param savedInstanceState Bundle with saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = (String) getArguments().getSerializable("eventID");
        }
        user = new User(requireContext(), null); // Initialize user and load data

        // Check and request permissions
        permissionHelper = new PermissionHelper(requireActivity());
        permissionHelper.fetchAndStoreLocation(FirebaseFirestore.getInstance(), eventID);
    }

    /**
     * Sets up appropriate buttons on the entrantJoinPage
     * Loads the event from FireStore DB using eventID with asynchronous methods from Event class
     * Sets text views using information for the event stored in DB
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle with saved instance state
     * @return the inflated view for the fragment
     */
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
                // Populate information for event on page.
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

        // If description is long, user can choose whether or not to expand
        expandDescriptionButton.setOnClickListener(v -> {
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

    /**
     * Adds the event to the user's requested events in DB
     * Adds the user to the event's waitlist in DB
     * @param event Event object associated with the EventID obtained from QR
     */
    private void addEvent(Event event) {
        if (event != null) {
            Log.d("EntrantAddPage", "User: " + user.getRequestedEvents());
            user.addRequestedEvent(event.getEventId());
            event.addToWaitlist(user.getDeviceID());

            Navigation.findNavController(requireView()).navigate(R.id.action_entrantJoinPage_to_entrantEventsList);
        }
    }
}
