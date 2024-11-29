// From chatgpt, openai, "write a java implementation with java documentation of entrantEventDescription
// Class with methods to show the event description
// given here is the xml code for it", 2024-11-02
package com.example.myapplication.view.entrant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;

/**
 * A fragment that displays details about a specific event, including the organizer's name,
 * description, and date. It also includes functionality to expand or collapse the event description.
 */
public class entranteventdescription extends Fragment {

    private Event event; // Store the event object
    private User user;
    /**
     * Initializes the fragment, retrieves the Event data if passed in arguments,
     * and sets up the User object.
     * @param savedInstanceState Bundle with saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (com.example.myapplication.model.Event) getArguments().getSerializable("event"); // Retrieve the event
        }
        user = new User(requireContext(), null); // Initialize user and load data
    }
    /**
     * Inflates the view for the fragment, sets up UI elements, and populates the view with event details.
     * Also handles the functionality for the back button and expanding/collapsing the event description.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle with saved instance state
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entranteventdescription, container, false);

        // Reference UI elements
        ImageButton backButton = view.findViewById(R.id.backArrowButton);
        ImageButton expandDescriptionButton = view.findViewById(R.id.expandDescriptionButton);
        ImageView eventImageView = view.findViewById(R.id.eventImageView);
        TextView organizerNameTextView = view.findViewById(R.id.organizerNameTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);

        // Populate UI with event details
        if (event != null) {
            Glide.with(requireContext())
                    .load(event.getPosterUrl())
                    .transform(new CircleCrop())            // Make image circular
                    .into(eventImageView);
            organizerNameTextView.setText("Event: " + event.getEventName());
            eventDescriptionTextView.setText(event.getEventDescription());
            eventDateTextView.setText("Date: " + event.getEventStart()); // Format the date if needed
        }

        // Back button to navigate to the event list
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_entranteventdescription_to_entrantEventsList));

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

        return view;
    }
}