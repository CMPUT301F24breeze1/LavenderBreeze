// From chatgpt, openai, "write a java implementation with java documentation of EntrantSelectedPage
//class with methods to show the event details and option to accept/decline the event
//given here is the xml code for it", 2024-11-02
package com.example.myapplication.view.entrant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.myapplication.R;
import com.example.myapplication.controller.NotificationHelper;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment that displays the details of a selected event and allows the user to accept or decline
 * the event invitation. It includes options to expand the event description and navigate back to the
 * event list.
 */
public class entrantSelectedPage extends Fragment {

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
            event = (com.example.myapplication.model.Event) getArguments().getSerializable("event"); // Retrieve the event
        }
        user = new User(requireContext(), null); // Initialize user and load data
    }
    /**
     * Inflates the view for the fragment, sets up UI elements, populates event details,
     * and configures click listeners for navigation, description expansion, and accepting/declining the event.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle containing the saved instance state
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrantselectedpage, container, false);

        // Reference UI elements
        ImageButton backButton = view.findViewById(R.id.backArrowButton);
        ImageButton expandDescriptionButton = view.findViewById(R.id.expandDescriptionButton);
        ImageView eventImageView = view.findViewById(R.id.eventImageView);
        TextView eventNameTextView = view.findViewById(R.id.eventNameTextView);
        TextView eventDateTextView = view.findViewById(R.id.eventDateTextView);
        TextView eventCapacityTextView = view.findViewById(R.id.eventCapacityTextView);
        TextView eventPriceTextView = view.findViewById(R.id.eventPriceTextView);
        TextView eventRegistrationTextView = view.findViewById(R.id.eventRegistrationTextView);
        TextView eventDescriptionTextView = view.findViewById(R.id.eventDescriptionTextView);
        Button acceptButton = view.findViewById(R.id.AcceptButton);
        Button declineButton = view.findViewById(R.id.DeclineButton);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Populate UI with event details
        if (event != null) {
            Glide.with(requireContext())
                    .load(event.getPosterUrl())
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

    /**
     * Accepts the event by adding it to the user's accepted events and removing it from
     * the selected events. It also updates the event's participant lists accordingly.
     * After accepting, navigates back to the event list.
     */
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
    /**
     * Declines the event by removing it from the user's selected events and adding it to
     * the declined events. Updates the event's participant lists accordingly.
     * The lottery system will then pull 1 more person from the waitlist to the selected list to replace the decliner.
     * After declining, navigates back to the event list.
     */
    private void declineEvent() {
        if (event != null) {
            user.removeSelectedEvent(event.getEventId());
            event.removeFromSelectedlist(user.getDeviceID());
            event.addToDeclinedlist(user.getDeviceID());// Method to remove from selected events
            Log.d("EntrantSelectedPage", "Event declined: " + event.getEventId());
            List<String> waitlist = event.getWaitlist();
            List<String> selected = event.getSelectedEntrants();
            int capacity = event.getCapacity();
            String eventId = event.getEventId();

            if (!waitlist.isEmpty()) {
                //Select new entrant
                Collections.shuffle(waitlist);
                User winner = new User(waitlist.get(0), null);
                winner.addSelectedEvent(eventId);
                winner.removeRequestedEvent(eventId);

                List<String> notifyUser = new ArrayList<>();
                notifyUser.add(waitlist.get(0));

                // Move entrants in event lists
                selected.add(waitlist.remove(0));
                // Update Firestore
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("waitlist", waitlist);
                eventData.put("selectedEntrants", selected);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference eventsRef = db.collection("events");
                eventsRef.document(eventId).update(eventData)
                        .addOnSuccessListener(aVoid -> Log.d("Lucas", "Event updated successfully in Firestore."))
                        .addOnFailureListener(e -> {
                            Log.e("Lucas", "Error updating event in Firestore", e);
                            Toast.makeText(getActivity(), "Failed to update event in Firestore.", Toast.LENGTH_LONG).show();
                        });

                // Send new selected a notification
                NotificationHelper notificationHelper = new NotificationHelper();
                notificationHelper.sendNotification(
                        notifyUser,             // List containing only the new selected person's device ID
                        "Congratulations!",     // Notification title
                        "You won the lottery! Claim your prize now." // Notification message
                );
            }
            // Navigate back to the event list after declining
            Navigation.findNavController(requireView()).navigate(R.id.action_entrantSelectedPage_to_entrantEventsList);
        }
    }
}