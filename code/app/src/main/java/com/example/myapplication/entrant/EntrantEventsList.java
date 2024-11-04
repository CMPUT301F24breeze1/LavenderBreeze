package com.example.myapplication.entrant;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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
    private List<Event> displayedEvents;
    private ListView eventList;
    private ArrayAdapter<Event> eventAdapter;
    private User user;

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

        // Initialize the ListView and displayedEvents list
        eventList = view.findViewById(R.id.eventListView);
        displayedEvents = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), displayedEvents);
        eventList.setAdapter(eventAdapter);

        // Initialize navigation buttons
        initializeButtons(view);

        // Button listeners to filter lists based on the category
        setupFilterButtons(view);

        return view;
    }

    // Method to initialize navigation buttons
    private void initializeButtons(View view) {
        view.findViewById(R.id.button_go_to_entrant_profile).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantProfile3)
        );
        view.findViewById(R.id.button_go_to_entrant_event_page).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantEventPage)
        );
        view.findViewById(R.id.button_go_to_qr_scanner).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantQrScan)
        );
        view.findViewById(R.id.button_go_to_home).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_home)
        );
        view.findViewById(R.id.button_go_to_Leave_page).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantLeavePage2)
        );
    }

    // Method to set up filter buttons for different event lists
    private void setupFilterButtons(View view) {
        view.findViewById(R.id.button_show_waitlist).setOnClickListener(v -> showEventList(waitlist));
        view.findViewById(R.id.button_show_selected).setOnClickListener(v -> showEventList(selectedlist));
        view.findViewById(R.id.button_show_cancelled).setOnClickListener(v -> showEventList(cancelledlist));
        view.findViewById(R.id.button_show_accepted).setOnClickListener(v -> showEventList(acceptedlist));
    }

    // Load the appropriate event list based on the filter
    private void showEventList(List<String> eventIds) {
        displayedEvents.clear();
        if (eventIds != null) {
            for (String eventId : eventIds) {
                Event event = new Event(eventId);
                displayedEvents.add(event); // Here, you may need to fetch event details asynchronously
            }
        }
        eventAdapter.notifyDataSetChanged();
    }

    // Method to extract event lists from the user
    private void extractData() {
        if (user != null) {
            waitlist = user.getRequestedEvents();
            selectedlist = user.getSelectedEvents();
            cancelledlist = user.getCancelledEvents();
            acceptedlist = user.getAcceptedEvents();
            showEventList(waitlist); // Show waitlist events by default
        }
    }
}
