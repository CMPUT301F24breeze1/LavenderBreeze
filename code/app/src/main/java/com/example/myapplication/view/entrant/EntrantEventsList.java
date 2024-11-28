// From chatgpt, openai, "write a java implementation with java documentation of EntrantEventsList
// Class with methods to show the event list
// given here is the xml code for it", 2024-11-02
package com.example.myapplication.view.entrant;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.example.myapplication.controller.EventAdapter;
import com.example.myapplication.model.User;
import java.util.ArrayList;
import java.util.List;
/**
 * A fragment that displays a list of events for the entrant, categorized into
 * different states such as requested, selected, cancelled, and accepted. It also
 * provides navigation to other fragments and the ability to filter events by their status.
 */
public class EntrantEventsList extends Fragment {

    private List<String> waitlist;
    private List<String> selectedlist;
    private List<String> cancelledlist;
    private List<String> acceptedlist;
    private List<Event> displayedEvents = new ArrayList<>();
    private ListView eventList;
    private EventAdapter eventAdapter;  // Use EventAdapter instead of ArrayAdapter
    private User user;
    Button homeButton, profileButton, eventsButton;
    /**
     * Default constructor required for instantiating the fragment.
     */
    public EntrantEventsList() {
        // Required empty public constructor
    }
    /**
     * Initializes the fragment, sets up the User data, and loads event lists.
     * @param savedInstanceState Bundle with saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new User(requireContext(), this::extractData); // Initialize user and load data
    }
    /**
     * Inflates the view for the fragment, sets up the ListView, and configures navigation and filter buttons.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle with saved instance state
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_events_list, container, false);

        // Initialize the ListView and set an empty adapter initially
        eventList = view.findViewById(R.id.eventListView);
        eventAdapter = new EventAdapter(requireContext(), displayedEvents, "Requested");
        eventList.setAdapter(eventAdapter);  // Set adapter here to avoid NullPointerException

        // Initialize navigation buttons
        intializeBottomNavButton(view);

        // Button listeners to filter lists based on the category
        setupFilterButtons(view);

        return view;
    }
    /**
     * Initializes bottom navigation buttons and sets up click listeners for navigation.
     * @param view the root view of the fragment
     */
    public void intializeBottomNavButton(View view){
        homeButton = view.findViewById(R.id.homeButton);
        profileButton = view.findViewById(R.id.profileButton);
        eventsButton = view.findViewById(R.id.eventsButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_home); // ID of the destination in nav_graph.xml
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantProfile3); // ID of the destination in nav_graph.xml
            }
        });
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_self); // ID of the destination in nav_graph.xml
            }
        });
    }
    /**
     * Configures filter buttons to display events by status. Each button loads a
     * different category of events (e.g., requested, selected, cancelled, accepted).
     * @param view the root view of the fragment
     */
    private void setupFilterButtons(View view) {
        view.findViewById(R.id.button_show_waitlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new User(requireContext(), ()->updatedData());
                showEventList(waitlist, "Requested");
            }});
        view.findViewById(R.id.button_show_selected).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new User(requireContext(), ()->updatedData());
                showEventList(selectedlist, "Selected");
            }});
        view.findViewById(R.id.button_show_cancelled).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new User(requireContext(), ()->updatedData());
                showEventList(cancelledlist, "Cancelled");
            }});
        view.findViewById(R.id.button_show_accepted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new User(requireContext(), ()->updatedData());
                showEventList(acceptedlist, "Accepted");
            }});
        view.findViewById(R.id.addEventButton).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantQrScan));
    }

    /**
     * Loads and displays a list of events based on the specified event IDs and status.
     * If the list is empty, it clears the displayed events.
     * @param eventIds list of event IDs to be displayed
     * @param status the status of events (e.g., Requested, Selected, Cancelled, Accepted)
     */
    private void showEventList(List<String> eventIds, String status) {
        if (eventList == null) {
            Log.d("EntrantEventsList", "eventList is null");
            return;
        }
        if (eventIds == null || eventIds.isEmpty()) {
            Log.d("EntrantEventsList", "eventIds is empty or null");
            displayedEvents.clear();
            eventAdapter.notifyDataSetChanged(); // Clear the adapter if no events are found
            return;
        }

        Log.d("EntrantEventsList", "Loading events for status: " + status);

        displayedEvents.clear();
        eventAdapter = new EventAdapter(requireContext(), displayedEvents, status);
        eventList.setAdapter(eventAdapter);

        for (String eventId : eventIds) {
            Event event = new Event(eventId);
            event.loadEventDataAsync(new Event.OnEventDataLoadedListener() {
                @Override
                public void onEventDataLoaded(Event loadedEvent) {
                    if (loadedEvent != null) {
                        Log.d("EntrantEventsList", "Loaded event: " + loadedEvent.getEventName());
                        displayedEvents.add(loadedEvent);
                        eventAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * Extracts event lists from the User object, categorizing them as requested,
     * selected, cancelled, and accepted. By default, the requested events are shown.
     */
    private void extractData() {
        if (user != null) {
            waitlist = user.getRequestedEvents();
            selectedlist = user.getSelectedEvents();
            cancelledlist = user.getCancelledEvents();
            acceptedlist = user.getAcceptedEvents();
            showEventList(waitlist, "Requested"); // Show waitlist events by default
        }
    }
    private void updatedData() {
        if (user != null) {
            waitlist = user.getRequestedEvents();
            selectedlist = user.getSelectedEvents();
            cancelledlist = user.getCancelledEvents();
            acceptedlist = user.getAcceptedEvents();
        }
    }
}
