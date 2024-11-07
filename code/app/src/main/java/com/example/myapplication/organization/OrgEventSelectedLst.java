package com.example.myapplication.organization;

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
import com.example.myapplication.model.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class OrgEventSelectedLst extends Fragment {

    private String eventId;
    private Event event;
    private List<String> acceptedList;
    private List<String> canceledList;
    private List<User> displayedUsers = new ArrayList<>();
    private ListView userListView;
    private UserAdapter userAdapter;  // Use EventAdapter instead of ArrayAdapter;

    private Button filterAllButton, filterAcceptedButton, filterCanceledButton;
    public OrgEventSelectedLst() {
        // Required empty public constructor
    }
    public static OrgEventSelectedLst newInstance(String eventId) {
        OrgEventSelectedLst fragment = new OrgEventSelectedLst();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            eventId= getArguments().getString("eventId");
        }
        loadEventData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_event_selected_lst, container, false);

        // Initialize the ListView and set an empty adapter initially
        userListView = view.findViewById(R.id.list_view_event_selected_list);
        userAdapter = new UserAdapter(requireContext(),displayedUsers, "all");
        userListView.setAdapter(userAdapter);  // Set adapter here to avoid NullPointerException


        // Button listeners to filter lists based on the category
        setupFilterButtons(view);

        return view;
    }


    // Method to set up filter buttons for different event lists
    private void setupFilterButtons(View view) {
        filterAllButton = view.findViewById(R.id.filterAll);
        filterAcceptedButton = view.findViewById(R.id.filterAccepted);
        filterCanceledButton = view.findViewById(R.id.filterCancelled);

        filterAllButton.setOnClickListener(v -> showUserList("all"));
        filterAcceptedButton.setOnClickListener(v -> showUserList("accepted"));
        filterCanceledButton.setOnClickListener(v -> showUserList("canceled"));
    }

    private void loadEventData() {
        event = new Event(eventId);
        event.loadEventDataAsync(new Event.OnEventDataLoadedListener() {
            @Override
            public void onEventDataLoaded(Event loadedEvent) {
                if (loadedEvent != null) {
                    acceptedList = loadedEvent.getAcceptedEntrants();
                    canceledList = loadedEvent.getDeclinedEntrants();
                    showUserList("all");  // Show all by default
                }
                Log.d("OrgEventSelectedLst", "Received Accepted List: ");
                //userAdapter.notifyDataSetChanged();
            }
        });
    }
    // Load the appropriate event list based on the filter
    private void showUserList(String filter) {
        displayedUsers.clear();

        List<String> userIdsToDisplay = new ArrayList<>();
        if ("accepted".equals(filter)) {
            userIdsToDisplay.addAll(acceptedList);
        } else if ("canceled".equals(filter)) {
            userIdsToDisplay.addAll(canceledList);
        } else {
            userIdsToDisplay.addAll(acceptedList);
            userIdsToDisplay.addAll(canceledList);
        }
        Log.d("OrgEventSelectedLst", "User Ids to Display: " + userIdsToDisplay);

        userAdapter = new UserAdapter(requireContext(), displayedUsers, filter);
        userListView.setAdapter(userAdapter);
        for (String userId : userIdsToDisplay) {
            User user = new User(userId,loadedUser -> {
                if (loadedUser != null) {
                    displayedUsers.add(loadedUser);
                    Log.d("OrgEventSelectedLst", "Loaded User: " + loadedUser.getName());
                    userAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}

