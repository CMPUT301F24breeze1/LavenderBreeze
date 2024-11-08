package com.example.myapplication.entrant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;


public class entrantSelectedPage extends Fragment {

    private Event event; // Store the event object
    private User user;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (com.example.myapplication.model.Event) getArguments().getSerializable("event"); // Retrieve the event
        }
        user = new User(requireContext(), null); // Initialize user and load data
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrantselectedpage, container, false);

        // Reference UI elements
        ImageButton backButton = view.findViewById(R.id.backArrowButton);
        ImageButton expandDescriptionButton = view.findViewById(R.id.expandDescriptionButton);
        TextView organizerNameTextView = view.findViewById(R.id.organizerNameTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        Button acceptButton = view.findViewById(R.id.AcceptButton);
        Button declineButton = view.findViewById(R.id.DeclineButton);

        // Populate UI with event details
        if (event != null) {
            organizerNameTextView.setText("Organized by: " + event.getOrganizerId());
            eventDescriptionTextView.setText(event.getEventDescription());
            eventDateTextView.setText("Date: " + event.getEventStart()); // Format the date if needed
        }

        // Back button to navigate to the event list
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_entrantSelectedPage_to_entrantEventsList));

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

        // Leave button to remove event from waitlist
        acceptButton.setOnClickListener(v -> acceptEvent());

        // Decline button functionality to remove event from selected list
        declineButton.setOnClickListener(v -> declineEvent());

        return view;
    }

    // Accept event and add to the user's accepted events
    private void acceptEvent() {
        if (event != null) {
            user.addAcceptedEvent(event.getEventId()); // Method to add to accepted events
            user.removeSelectedEvent(event.getEventId()); // Remove from selected events list
            event.removeFromSelectedlist(user.getDeviceID());
            event.addToAcceptedlist(user.getDeviceID());
            Log.d("EntrantSelectedPage", "Event accepted: " + event.getEventId());

            // Navigate back to the event list after accepting
            Navigation.findNavController(requireView()).navigate(R.id.action_entrantSelectedPage_to_entrantEventsList);
        }
    }

    // Decline event and remove from selected events list
    private void declineEvent() {
        if (event != null) {
            user.removeSelectedEvent(event.getEventId());
            event.removeFromSelectedlist(user.getDeviceID());
            event.addToDeclinedlist(user.getDeviceID());// Method to remove from selected events
            Log.d("EntrantSelectedPage", "Event declined: " + event.getEventId());

            // Navigate back to the event list after declining
            Navigation.findNavController(requireView()).navigate(R.id.action_entrantSelectedPage_to_entrantEventsList);
        }
    }
}