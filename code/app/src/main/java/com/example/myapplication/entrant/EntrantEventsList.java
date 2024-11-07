package com.example.myapplication.entrant;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.EventAdapter;
import com.example.myapplication.model.User;
import java.util.ArrayList;
import java.util.List;

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

    public EntrantEventsList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new User(requireContext(), this::extractData); // Initialize user and load data
    }

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
    // Method to set up filter buttons for different event lists
    private void setupFilterButtons(View view) {
        view.findViewById(R.id.button_show_waitlist).setOnClickListener(v -> showEventList(waitlist, "Requested"));
        view.findViewById(R.id.button_show_selected).setOnClickListener(v -> showEventList(selectedlist, "Selected"));
        view.findViewById(R.id.button_show_cancelled).setOnClickListener(v -> showEventList(cancelledlist, "Cancelled"));
        view.findViewById(R.id.button_show_accepted).setOnClickListener(v -> showEventList(acceptedlist, "Accepted"));
        view.findViewById(R.id.addEventButton).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantQrScan));
    }

    // Load the appropriate event list based on the filter
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

    // Method to extract event lists from the user
    private void extractData() {
        if (user != null) {
            waitlist = user.getRequestedEvents();
            selectedlist = user.getSelectedEvents();
            cancelledlist = user.getCancelledEvents();
            acceptedlist = user.getAcceptedEvents();
            showEventList(waitlist, "Requested"); // Show waitlist events by default
        }
    }
}
