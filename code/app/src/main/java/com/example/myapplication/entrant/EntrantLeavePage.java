package com.example.myapplication.entrant;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;

public class EntrantLeavePage extends Fragment {

    private Event event; // Store the event object

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event"); // Retrieve the event
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_leave_page, container, false);

        // Reference UI elements
        ImageButton eventList = view.findViewById(R.id.backArrowButton);
        ImageButton expandDescriptionButton = view.findViewById(R.id.expandDescriptionButton);
        TextView organizerNameTextView = view.findViewById(R.id.organizerNameTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        Button leaveButton = view.findViewById(R.id.leaveButton);

        // Populate UI with event details
        if (event != null) {
            organizerNameTextView.setText("Organized by: " + event.getOrganizerId());
            eventDescriptionTextView.setText(event.getEventDescription());
            eventDateTextView.setText("Date: " + event.getEventStart()); // Format the date if needed
        }

        // Back button to navigate to the event list
        eventList.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_entrantLeavePage_to_entrantEventsList));

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
        leaveButton.setOnClickListener(v -> leaveEvent());

        return view;
    }

    private void leaveEvent() {
        if (event != null) {
            // Assuming you have a user instance
            User user = new User(requireContext(), null); // or retrieve your existing user instance
            user.removeRequestedEvent(event.getEventId()); // Adjust this method as necessary

            // Navigate back to the event list
            Navigation.findNavController(requireView()).navigate(R.id.action_entrantLeavePage_to_entrantEventsList);
        }
    }
}
