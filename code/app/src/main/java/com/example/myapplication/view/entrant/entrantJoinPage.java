package com.example.myapplication.view.entrant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.myapplication.R;
import com.example.myapplication.controller.DeviceUtils;
import com.example.myapplication.controller.PermissionHelper;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * This Fragment representing the Entrant Join Page, allowing users to view and join events.
 * This fragment is responsible for displaying event details retrieved from a Firestore database,
 * handling user interactions such as joining an event's waitlist, and managing UI elements for
 * navigation and event description toggling.
 */
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
        ImageView eventImageView = view.findViewById(R.id.eventImageView);
        TextView eventNameTextView = view.findViewById(R.id.eventNameTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        TextView eventCapacityTextView = view.findViewById(R.id.eventCapacityTextView);
        TextView eventPriceTextView = view.findViewById(R.id.eventPriceTextView);
        TextView eventRegistrationTextView = view.findViewById(R.id.eventRegistrationTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        Button addButton = view.findViewById(R.id.addButton);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Event temp = new Event(eventID, loadedEvent -> { // Means of loading the event asynchronously
            if (loadedEvent != null) {
                Log.d("entrantJoinPage_LoadedEvent", "Loaded Event: " + loadedEvent.getEventId());
                // Populate information for event on page.
                Glide.with(requireContext())
                        .load(loadedEvent.getPosterUrl())
                        .into(eventImageView);
                String eventStart = sdf.format(loadedEvent.getEventStart());
                String eventEnd = sdf.format(loadedEvent.getEventEnd());
                String registrationStart = sdf.format(loadedEvent.getRegistrationStart());
                String registrationEnd = sdf.format(loadedEvent.getRegistrationEnd());
                eventNameTextView.setText(loadedEvent.getEventName());
                eventDateTextView.setText("Schedule: " + eventStart + " to " + eventEnd);
                eventCapacityTextView.setText("Capacity: " + loadedEvent.getCapacity());
                eventPriceTextView.setText("Price: " + String.format("$%.2f", loadedEvent.getPrice()));
                eventRegistrationTextView.setText("Registration Period: " + registrationStart + " to " + registrationEnd);
                eventDescriptionTextView.setText(loadedEvent.getEventDescription());
                // Call function to add user to waitlist for event, and event to the requested list of user. (On join button press)
                addButton.setOnClickListener(v -> {
                    if (loadedEvent.getSelectedEntrants().isEmpty()) {
                        if (loadedEvent.getGeolocationRequirement()) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Location Required")
                                    .setMessage("This event requires you to share your location")
                                    .setPositiveButton("I would like to join anyway", (dialog, which) -> addEvent(loadedEvent))
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                        else {
                            addEvent(loadedEvent);
                        }
                    }
                    else {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Unable to join waitlist")
                                .setMessage("The lottery for this event has already been drawn")
                                .setPositiveButton("Back to events list", (dialog, which) ->
                                        Navigation.findNavController(v).navigate(R.id.action_entrantJoinPage_to_entrantEventsList))
                                .show();
                    }
                });
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
            // Check if the waitlist is limited
            if (event.isWaitingListLimited()) {
                if (event.getWaitingListCount() < event.getWaitingListCap()) {
                    // Waitlist is not full, allow user to join
                    user.addRequestedEvent(event.getEventId());
                    event.addToWaitlist(user.getDeviceID());
                    Toast.makeText(requireContext(), "You have successfully joined the waitlist!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigate(R.id.action_entrantJoinPage_to_entrantEventsList);
                } else {
                    // Waitlist is full
                    Toast.makeText(requireContext(), "Waitlist is full. You cannot join.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Unlimited waitlist, allow user to join
                user.addRequestedEvent(event.getEventId());
                event.addToWaitlist(user.getDeviceID());
                Toast.makeText(requireContext(), "You have successfully joined the waitlist!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigate(R.id.action_entrantJoinPage_to_entrantEventsList);
            }
        }
    }

}
