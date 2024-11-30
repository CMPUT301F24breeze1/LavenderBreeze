// From chatgpt, openai, "write a java implementation with java documentation of EntrantLeavePage
//class with methods to leave an event
//given here is the xml code for it", 2024-11-02
package com.example.myapplication.view.entrant;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;

import java.text.SimpleDateFormat;

/**
 * A fragment that displays the details of an event and allows the user to leave the event.
 * It shows information about the event organizer, description, and date, with options
 * to expand the description and leave the event.
 */
public class EntrantLeavePage extends Fragment {

    private Event event; // Store the event object
    private User user;
    /**
     * Initializes the fragment, retrieves the Event data from arguments if available,
     * and sets up the User object.
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event"); // Retrieve the event
        }
        user = new User(requireContext(), null); // Initialize user and load data
    }
    /**
     * Inflates the view for the fragment, sets up UI elements, populates event details,
     * and configures click listeners for navigation, description expansion, and leaving the event.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle containing the saved instance state
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_leave_page, container, false);

        // Reference UI elements
        ImageButton eventList = view.findViewById(R.id.backArrowButton);
        ImageButton expandDescriptionButton = view.findViewById(R.id.expandDescriptionButton);
        ImageView eventImageView = view.findViewById(R.id.eventImageView);
        TextView eventNameTextView = view.findViewById(R.id.eventNameTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        TextView eventCapacityTextView = view.findViewById(R.id.eventCapacityTextView);
        TextView eventPriceTextView = view.findViewById(R.id.eventPriceTextView);
        TextView eventRegistrationTextView = view.findViewById(R.id.eventRegistrationTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        Button leaveButton = view.findViewById(R.id.leaveButton);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Populate UI with event details
        if (event != null) {
            Glide.with(requireContext())
                    .load(event.getPosterUrl())
                    //.transform(new CircleCrop()) // Make image circular
                    .into(eventImageView);
            String eventStart = sdf.format(event.getEventStart());
            String eventEnd = sdf.format(event.getEventEnd());
            String registrationStart = sdf.format(event.getRegistrationStart());
            String registrationEnd = sdf.format(event.getRegistrationEnd());
            eventNameTextView.setText(event.getEventName());
            eventDateTextView.setText("Schedule: " + eventStart + " to " + eventEnd);
            eventCapacityTextView.setText("Capacity: " + event.getCapacity());
            eventPriceTextView.setText("Price: " + String.format("$%.2f", event.getPrice()));
            eventRegistrationTextView.setText("Registration Period: " + registrationStart + " to " + registrationEnd);
            eventDescriptionTextView.setText(event.getEventDescription());
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
    /**
     * Removes the event from the user's waitlist and navigates back to the event list.
     * Logs the current state of the user's requested events and updates both the user and event data.
     */
    private void leaveEvent() {
        if (event != null) {
            // Assuming you have a user instance
            //User user = new User(requireContext(), null); // or retrieve your existing user instance
            Log.d("EntrantLeavePage", "User: " + user.getRequestedEvents());
            user.removeRequestedEvent(event.getEventId());
            event.removeFromWaitlist(user.getDeviceID()); // Remove the user from the waitlist// Adjust this method as necessary

            // Navigate back to the event list
            Navigation.findNavController(requireView()).navigate(R.id.action_entrantLeavePage_to_entrantEventsList);
        }
    }
}
