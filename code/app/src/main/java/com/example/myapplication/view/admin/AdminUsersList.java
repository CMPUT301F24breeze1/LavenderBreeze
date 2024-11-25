package com.example.myapplication.view.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.controller.UserListAdapter;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.Facility;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminUsersList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUsersList extends Fragment {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private CollectionReference facilitiesRef;
    private List<DocumentSnapshot> users;
    // Display lists
    private ListView userList;
    private ArrayList<Event> eventDataList;
    private ArrayList<User> userDataList;
    private ArrayList<String> eventIds;
    private ArrayList<String> userIds;
    private ArrayList<Facility> facilityDataList;
    private ArrayList<String> facilityOrganizerIds;
    private UserListAdapter entrantAdapter;

    public AdminUsersList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdminUsersList.
     */
    public static AdminUsersList newInstance() {
        AdminUsersList fragment = new AdminUsersList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_users_list, container, false);


        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        eventsRef  = db.collection("events");
        facilitiesRef = db.collection("facilities");

        eventDataList = new ArrayList<Event>();
        userDataList = new ArrayList<User>();
        facilityDataList = new ArrayList<Facility>();
        eventIds = new ArrayList<String>();
        userIds = new ArrayList<String>();
        facilityOrganizerIds = new ArrayList<String>();

        entrantAdapter = new UserListAdapter(this.getContext(), userDataList);

        fetchUsers();
        fetchEvents();
        fetchFacilities();

        userList = view.findViewById(R.id.list_view_admin_users_list);
        userDataList = new ArrayList<User>();
        entrantAdapter = new UserListAdapter(this.getContext(), userDataList);
        userList.setAdapter(entrantAdapter);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User clicked = userDataList.get(i);

                if (Objects.equals(clicked.getDeviceID(), Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID))){
                    Toast.makeText(getActivity(), "You cannot delete yourself", Toast.LENGTH_LONG).show();
                    return;
                }

                deleteEventFromUsers(clicked);

                Log.d("Kenny", String.valueOf(facilityOrganizerIds.contains(clicked.getDeviceID())));
                if(facilityOrganizerIds.contains(clicked.getDeviceID())){

                    Facility facility = facilityDataList.get(facilityOrganizerIds.indexOf(clicked.getDeviceID()));
                    List<String> facilityEvents = facility.getEvents();

                    Log.d("Kenny", "Deleting events from facility"+facility.getFacilityName());

                    if(facilityEvents != null && !facilityEvents.isEmpty()) {
                        for (int j = 0; j < facilityEvents.size(); j++) {
                            Event event = eventDataList.get(eventIds.indexOf(facilityEvents.get(j)));
                            Log.d("Kenny", "deleting event"+event.getEventName());


                            deleteEvent(event);
                        }
                    }

                    facilitiesRef.document(facilityDataList.get(facilityOrganizerIds.indexOf(clicked.getDeviceID())).getFacilityId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("AdminFacilitiesList", "Facility successfully Deleted");
                                }
                            });
                }

                usersRef.document(clicked.getDeviceID())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("AdminFacilitiesList", "User successfully Deleted");
                                userDataList.remove(clicked);
                                entrantAdapter.notifyDataSetChanged();
                            }
                        });
            }
        });


        Button home = view.findViewById(R.id.button_go_to_home_from_admin_users_list);
        home.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminUsersList_to_home));

        Button events = view.findViewById(R.id.events);
        events.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminUsersList_to_adminEventsList));

        Button facilities = view.findViewById(R.id.facilities);
        facilities.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminUsersList_to_adminFacilitiesList));

        Button pictures = view.findViewById(R.id.images);
        pictures.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminUsersList_to_adminPicturesList));
        return view;
    }

    private void fetchEvents(){
        Task<QuerySnapshot> task = eventsRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> events = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for(int i = 0; i < events.size(); i++){
                    String eventName = events.get(i).getString("eventName");
                    int capacity = events.get(i).getDouble("capacity").intValue();


                    // I set the event description as the doc ID to make it easier to pass when clicked
                    eventDataList.add(new Event(events.get(i).getId(),eventName, events.get(i).getString("eventDescription"), ((Timestamp)
                            events.get(i).get("eventStart")).toDate(), ((Timestamp) events.get(i).get("eventEnd")).toDate(),
                            ((Timestamp) events.get(i).get("registrationStart")).toDate(), ((Timestamp) events.get(i).get("registrationEnd")).toDate(),
                            events.get(i).getString("location"),capacity, ((Number)events.get(i).get("price")).intValue(),
                            events.get(i).getString("posterUrl"), events.get(i).getString("qrCodeHash"), events.get(i).getString("organizerId"),
                            (List<String>) events.get(i).get("acceptedEntrants"), (List<String>) events.get(i).get("selectedntrants"),
                            (List<String>) events.get(i).get("declinedEntrants"), (List<String>) events.get(i).get("waitlist")));
                    eventIds.add(events.get(i).getId());
                }
            }
        });
    }

    private void fetchUsers(){
        Task<QuerySnapshot> task = usersRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                users = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for (int i = 0; i < users.size(); i++) {
                    DocumentSnapshot current = users.get(i);

                    User user = new User(current.getId(), loadedUser -> {
                        if (loadedUser != null) {
                            userDataList.add(loadedUser);
                            userIds.add(loadedUser.getDeviceID());
                            Log.d("OrgEventSelectedLst", "Loaded User: " + loadedUser.getName());
                            entrantAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void fetchFacilities(){
        Task<QuerySnapshot> task = facilitiesRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> facilities = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for(int i = 0; i < facilities.size(); i++){
                    Facility facility = new Facility(facilities.get(i).getId(),facilities.get(i).getString("facilityName"),facilities.get(i).getString("facilityAddress"),
                            facilities.get(i).getString("facilityEmail"),facilities.get(i).getString("facilityPhoneNumber"),
                            facilities.get(i).getString("organizerId"),facilities.get(i).getString("profileImageUrl"));
                    if(facilities.get(i).get("events") == null){
                        facility.setEvents(new ArrayList<String>());
                    } else {
                        facility.setEvents((List<String>) facilities.get(i).get("events"));
                    }


                    facilityDataList.add(facility);
                    facilityOrganizerIds.add(facility.getOrganizerId());
                }
                Log.d("AdminUsersList", String.valueOf(facilityDataList));
                Log.d("AdminUsersList", String.valueOf(facilityOrganizerIds));
            }
        });
    }

    private void deleteEventFromUsers(User clicked){
        List<String> accepted = clicked.getAcceptedEvents();
        List<String> selected = clicked.getSelectedEvents();
        List<String> cancelled = clicked.getCancelledEvents();
        List<String> requested = clicked.getRequestedEvents();


        if(!accepted.isEmpty()){
            for (int j = 0; j < accepted.size(); j++) {
                //finds events to which user was accepted, and removes them from the lists
                Event event = eventDataList.get(eventIds.indexOf(accepted.get(j)));
                event.removeFromAcceptedlist(clicked.getDeviceID());
                event.setAcceptedEntrants(event.getAcceptedEntrants());
            }
        }

        if(!selected.isEmpty()){
            for (int j = 0; j < selected.size(); j++) {
                //finds events to which user was accepted, and removes them from the lists
                Event event = eventDataList.get(eventIds.indexOf(selected.get(j)));
                event.removeFromSelectedlist(clicked.getDeviceID());
                event.setSelectedEntrants(event.getSelectedEntrants());
            }
        }

        if(!cancelled.isEmpty()){
            for (int j = 0; j < cancelled.size(); j++) {
                //finds events to which user was accepted, and removes them from the lists
                Event event = eventDataList.get(eventIds.indexOf(cancelled.get(j)));
                event.removeFromDeclinedlist(clicked.getDeviceID());
                event.setDeclinedEntrants(event.getDeclinedEntrants());
            }
        }

        if(!requested.isEmpty()){
            for (int j = 0; j < requested.size(); j++) {
                //finds events to which user was accepted, and removes them from the lists
                Event event = eventDataList.get(eventIds.indexOf(requested.get(j)));
                event.removeFromWaitlist(clicked.getDeviceID());
                event.setWaitlist(event.getWaitlist());
            }
        }
    }

    private void deleteEvent(Event event){
        List<String> accepted = event.getAcceptedEntrants();
        List<String> cancelled = event.getDeclinedEntrants();
        List<String> selected = event.getSelectedEntrants();
        List<String> waitlist = event.getWaitlist();

        if(!accepted.isEmpty()){
            for (int j = 0; j < accepted.size(); j++) {
                // finds the user by searching the userIds list, then removes the event from their accepted list
                User user = userDataList.get(userIds.indexOf(accepted.get(j)));
                user.removeAcceptedEvent(event.getEventId());            }
        }
        if(!cancelled.isEmpty()){
            for (int j = 0; j < cancelled.size(); j++) {
                // finds the user by searching the userIds list, then removes the event from their accepted list
                User user = userDataList.get(userIds.indexOf(cancelled.get(j)));
                user.removeCancelledEvent(event.getEventId());
            }
        }
        if(!selected.isEmpty()){
            for (int j = 0; j < selected.size(); j++) {
                Log.d("Kenny", "requested users detected");
                // finds the user by searching the userIds list, then removes the event from their accepted list
                User user = userDataList.get(userIds.indexOf(selected.get(j)));
                user.removeSelectedEvent(event.getEventId());            }
        }
        if(!waitlist.isEmpty()){
            for (int j = 0; j < waitlist.size(); j++) {
                // finds the user by searching the userIds list, then removes the event from their accepted list
                User user = userDataList.get(userIds.indexOf(waitlist.get(j)));
                user.removeRequestedEvent(event.getEventId());            }
        }


        Facility eventLocation = facilityDataList.get(facilityOrganizerIds.indexOf(event.getOrganizerId()));
        List<String> eventsAtLocation = eventLocation.getEvents();
        Log.d("Kenny", eventsAtLocation.toString());
        eventsAtLocation.remove(event.getEventId());
        eventLocation.setEvents(eventsAtLocation);
        Log.d("Kenny", eventLocation.getEvents().toString());
        eventLocation.updateFirestore();



        eventsRef.document(event.getEventId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        eventDataList.remove(event);
                        Log.d("AdminEventsList", "Event successfully Deleted");
                    }
                });
    }

}