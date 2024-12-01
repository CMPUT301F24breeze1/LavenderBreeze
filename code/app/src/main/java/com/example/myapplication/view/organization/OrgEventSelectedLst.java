package com.example.myapplication.view.organization;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;
import com.example.myapplication.controller.UserAdapter;
import com.example.myapplication.controller.NotificationHelper;


import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that displays a selected list of an event,
 * with 3 filter buttons to view the desired list (selected, accepted, and declined)
 */
public class OrgEventSelectedLst extends Fragment {
    private String eventId;
    private Event event;
    private List<String> selectedList;
    private List<String> acceptedList;
    private List<String> canceledList;
    private int notifyList = 0;
    private List<User> displayedUsers = new ArrayList<>();
    private ListView userListView;
    private UserAdapter userAdapter;  // Use EventAdapter instead of ArrayAdapter;
    private Button filterAllButton, filterAcceptedButton, filterCanceledButton;

    /**
     * Default constructor required for instantiating the fragment.
     */
    public OrgEventSelectedLst() {
        // Required empty public constructor
    }

    /**
     * This creates a new instance of the OrgEventSelectedLst fragment,
     * while also setting up some initial data (eventId)
     */
    public static OrgEventSelectedLst newInstance(String eventId) {
        OrgEventSelectedLst fragment = new OrgEventSelectedLst();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes the fragment, and loads event data.
     * @param savedInstanceState Bundle with saved instance state
     */
    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            eventId= getArguments().getString("eventId");
        }
        loadEventData();
    }

    /**
     * Inflates the view for the fragment, sets up the ListView and buttons.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle with saved instance state
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_event_selected_lst, container, false);

        // Initialize the ListView and set an empty adapter initially
        userListView = view.findViewById(R.id.list_view_event_selected_list);
        userAdapter = new UserAdapter(requireContext(),displayedUsers, "Selected",eventId,getArguments());
        userListView.setAdapter(userAdapter);  // Set adapter here to avoid NullPointerException


        // Button listeners to filter lists based on the category
        setupFilterButtons(view);

        Button backButton = view.findViewById(R.id.button_go_to_event_from_org_event_selected_lst);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_OrgEventSelectedLst_to_OrgEvent,getArguments());
            }
        });

      // Set up the notification button for selected participants
        ImageButton notifyButton = view.findViewById(R.id.button_go_to_notif_from_org_event_selected_lst);
        notifyButton.setOnClickListener(v -> sendNotification());

        return view;
    }

    /**
     * This sets up filter buttons for different event lists
     */
    private void setupFilterButtons(View view) {
        filterAllButton = view.findViewById(R.id.filterAll);
        filterAcceptedButton = view.findViewById(R.id.filterAccepted);
        filterCanceledButton = view.findViewById(R.id.filterCancelled);

        filterAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateListData();
                showUserList("Selected");
                filterAllButton.setBackgroundColor(getResources().getColor(R.color.kennyBlueHighlight));
                filterAcceptedButton.setBackgroundColor(getResources().getColor(R.color.kennyBlue));
                filterCanceledButton.setBackgroundColor(getResources().getColor(R.color.kennyBlue));
            }});
        filterAcceptedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateListData();
                showUserList("Accepted");
                filterAllButton.setBackgroundColor(getResources().getColor(R.color.kennyBlue));
                filterAcceptedButton.setBackgroundColor(getResources().getColor(R.color.kennyBlueHighlight));
                filterCanceledButton.setBackgroundColor(getResources().getColor(R.color.kennyBlue));
            }});
        filterCanceledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateListData();
                showUserList("Canceled");
                filterAllButton.setBackgroundColor(getResources().getColor(R.color.kennyBlue));
                filterAcceptedButton.setBackgroundColor(getResources().getColor(R.color.kennyBlue));
                filterCanceledButton.setBackgroundColor(getResources().getColor(R.color.kennyBlueHighlight));
            }});
    }

    /**
     * This loads event data from Firestore
     */
    private void loadEventData() {
        event = new Event(eventId);
        event.loadEventDataAsync(new Event.OnEventDataLoadedListener() {
            @Override
            public void onEventDataLoaded(Event loadedEvent) {
                if (loadedEvent != null) {
                    selectedList = loadedEvent.getSelectedEntrants();
                    acceptedList = loadedEvent.getAcceptedEntrants();
                    canceledList = loadedEvent.getCancelledEntrants();
                    showUserList("Selected");  // Show all by default
                }
                Log.d("OrgEventSelectedLst", "selected list"+selectedList   + "accepted list"+acceptedList + "canceled list"+canceledList);
                //userAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This updates the list data for the event
     */
    private void updateListData() {
        event = new Event(eventId);
        event.loadEventDataAsync(new Event.OnEventDataLoadedListener() {
            @Override
            public void onEventDataLoaded(Event loadedEvent) {
                if (loadedEvent != null) {
                    selectedList = loadedEvent.getSelectedEntrants();
                    acceptedList = loadedEvent.getAcceptedEntrants();
                    canceledList = loadedEvent.getCancelledEntrants();
                }
                Log.d("OrgEventSelectedLst", "selected list"+selectedList   + "accepted list"+acceptedList + "canceled list"+canceledList);
                //userAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This loads the appropriate event list based on the filter
     */
    private void showUserList(String filter) {
        displayedUsers.clear();

        List<String> userIdsToDisplay = new ArrayList<>();
        if ("Accepted".equals(filter)) {
            notifyList = 1;
        } else if ("Canceled".equals(filter)) {
            notifyList = 2;
        } else {
            notifyList = 3;
        }
        Log.d("OrgEventSelectedLst", "User Ids to Display: " + userIdsToDisplay);

        userAdapter = new UserAdapter(requireContext(), displayedUsers, filter,eventId,getArguments());
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

    /**
     * This sends notification to the desired list (selected or canceled)
     */
    private void sendNotification() {
        if (notifyList != 0) {
            NotificationHelper notificationHelper = new NotificationHelper();

            if (notifyList == 1) {
                notificationHelper.sendNotification(
                        selectedList,              // List of device IDs based on current filter
                        "Event Update",          // Notification title
                        "You've been selected"                  // Notification message
                );
            } else if (notifyList == 2) {
                notificationHelper.sendNotification(
                        acceptedList,              // List of device IDs based on current filter
                        "Event Update",          // Notification title
                        "You've been accepted"                  // Notification message
                );
            } else if (notifyList == 3){
                notificationHelper.sendNotification(
                        canceledList,              // List of device IDs based on current filter
                        "Event Update",          // Notification title
                        "You've been cancelled"                  // Notification message
                );
            }


            Toast.makeText(getActivity(), "Notification sent!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "No users to notify in this list.", Toast.LENGTH_SHORT).show();
        }
    }
}








