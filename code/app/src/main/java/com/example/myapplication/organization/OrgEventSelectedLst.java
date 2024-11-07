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
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.EventAdapter;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserAdapter;
import com.example.myapplication.controller.NotificationSender;


import java.util.ArrayList;
import java.util.List;

public class OrgEventSelectedLst extends Fragment {
    private String eventId;
    private Event event;
    private List<String> selectedList;
    private List<String> acceptedList;
    private List<String> canceledList;
     private List<String> notifyList;
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

    @Nullable
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

        Button backButton = view.findViewById(R.id.button_go_to_event_from_org_event_selected_lst);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
      
      // Set up the notification button for selected participants
        Button notifyButton = view.findViewById(R.id.button_go_to_notif_from_org_event_selected_lst);
        notifyButton.setOnClickListener(v -> sendNotification());

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
                    selectedList = loadedEvent.getSelectedEntrants();
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
            notifyList = acceptedList;
        } else if ("canceled".equals(filter)) {
            userIdsToDisplay.addAll(canceledList);
            notifyList = canceledList;
        } else {
            userIdsToDisplay.addAll(selectedList);
            notifyList = selectedList;
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

    private void sendNotification() {
        if (notifyList != null && !notifyList.isEmpty()) {
            NotificationSender notificationSender = new NotificationSender();
            
            String message;
            if (notifyList == canceledList) {
                message = "We're sorry, but your registration has been declined.";
            } else if (notifyList == selectedList) {
                message = "You've been selected for the event!";
            } else {
                // For canceled list or other cases, set an appropriate message
                message = "We're sorry, but your registration has been declined.";
            }
    
            notificationSender.sendNotification(
                notifyList,              // List of device IDs based on current filter
                "Event Update",          // Notification title
                message                  // Notification message
            );
            Toast.makeText(getActivity(), "Notification sent to " + currentFilter + " users", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "No users to notify in this list.", Toast.LENGTH_SHORT).show();
        }
}

    
            
            
    

}

