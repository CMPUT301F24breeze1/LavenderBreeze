package com.example.myapplication.view.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.controller.EventsListAdapter;
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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminEventsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminEventsList extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventDataList;
    private ArrayList<User> userDataList;
    private ArrayList<String> userIds;
    private ArrayList<Facility> facilityDataList;
    private ArrayList<String> facilityOrganizerIds;
    private EventsListAdapter eventArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private CollectionReference facilitiesRef;
    List<DocumentSnapshot> events;


    public AdminEventsList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdminEventsList.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminEventsList newInstance() {
        AdminEventsList fragment = new AdminEventsList();
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
        View view = inflater.inflate(R.layout.fragment_admin_events_list, container, false);

        db = FirebaseFirestore.getInstance();

        usersRef = db.collection("users");
        eventsRef  = db.collection("events");
        facilitiesRef = db.collection("facilities");

        fetchEvents();
        fetchUsers();
        fetchFacilities();

        //initialize event data list and array adapter
        eventList = view.findViewById(R.id.list_view_admin_events_list);

        eventDataList = new ArrayList<Event>();
        userDataList = new ArrayList<User>();
        facilityDataList = new ArrayList<Facility>();
        userIds = new ArrayList<String>();
        facilityOrganizerIds = new ArrayList<String>();

        eventArrayAdapter = new EventsListAdapter(this.getContext(), eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> accepted = eventDataList.get(i).getAcceptedEntrants();
                List<String> cancelled = eventDataList.get(i).getDeclinedEntrants();
                List<String> selected = eventDataList.get(i).getSelectedEntrants();
                List<String> waitlist = eventDataList.get(i).getWaitlist();

                if(!accepted.isEmpty()){
                    for (int j = 0; j < accepted.size(); j++) {
                        // finds the user by searching the userIds list, then removes the event from their accepted list
                        userDataList.get(userIds.indexOf(accepted.get(j))).removeAcceptedEvent(eventDataList.get(i).getEventId());
                    }
                }
                if(!cancelled.isEmpty()){
                    for (int j = 0; j < accepted.size(); j++) {
                        // finds the user by searching the userIds list, then removes the event from their accepted list
                        userDataList.get(userIds.indexOf(cancelled.get(j))).removeCancelledEvent(eventDataList.get(i).getEventId());
                    }
                }
                if(!selected.isEmpty()){
                    for (int j = 0; j < accepted.size(); j++) {
                        // finds the user by searching the userIds list, then removes the event from their accepted list
                        userDataList.get(userIds.indexOf(selected.get(j))).removeSelectedEvent(eventDataList.get(i).getEventId());
                    }
                }
                if(!waitlist.isEmpty()){
                    for (int j = 0; j < accepted.size(); j++) {
                        // finds the user by searching the userIds list, then removes the event from their accepted list
                        userDataList.get(userIds.indexOf(waitlist.get(j))).removeRequestedEvent(eventDataList.get(i).getEventId());
                    }
                }

                /**
                Facility eventLocation = facilityDataList.get(facilityOrganizerIds.indexOf(eventDataList.get(i).getOrganizerId()));
                eventLocation.getEvents().remove(eventDataList.get(i));
                eventLocation.saveToFirestore();
                 **/

                eventsRef.document(eventDataList.get(i).getEventId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("AdminEventsList", "Event successfully Deleted");
                            }
                        });
            }
        });

        Button home = view.findViewById(R.id.button_go_to_home_from_admin_events_list);
        home.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminEventsList_to_home));

        Button users = view.findViewById(R.id.users);
        users.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminEventsList_to_adminUsersList));

        Button facilities = view.findViewById(R.id.facilities);
        facilities.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminEventsList_to_adminFacilitiesList));

        Button pictures = view.findViewById(R.id.images);
        pictures.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminEventsList_to_adminPicturesList));

        return view;
    }

    private void fetchEvents(){
        Task<QuerySnapshot> task = eventsRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                events = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for(int i = 0; i < events.size(); i++){
                    String eventName = events.get(i).getString("eventName");
                    int capacity = events.get(i).getDouble("capacity").intValue();


                    // I set the event description as the doc ID to make it easier to pass when clicked
                    eventDataList.add(new Event(eventName, events.get(i).getString("eventDescription"), ((Timestamp) events.get(i).get("eventStart")).toDate(), ((Timestamp) events.get(i).get("eventEnd")).toDate(),
                            ((Timestamp) events.get(i).get("registrationStart")).toDate(), ((Timestamp) events.get(i).get("registrationEnd")).toDate(), events.get(i).getString("location"),capacity, ((Long)events.get(i).get("price")).intValue(),
                            events.get(i).getString("posterUrl"), events.get(i).getString("qrCodeHash"), events.get(i).getString("organizerId")));
                }
                eventArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchUsers(){
        Task<QuerySnapshot> task = usersRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> users = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for (int i = 0; i < users.size(); i++) {
                    DocumentSnapshot current = users.get(i);
                    User user = new User(current.getId(), loadedUser -> {
                        if (loadedUser != null) {
                            userDataList.add(loadedUser);
                            userIds.add(loadedUser.getDeviceID());
                            Log.d("AdminEventsList", "Loaded User: " + loadedUser.getName());
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

                    facilityDataList.add(new Facility(facilities.get(i).getString("facilityName"),facilities.get(i).getString("facilityAddress"),
                            facilities.get(i).getString("facilityEmail"),facilities.get(i).getString("facilityPhoneNumber"),
                            facilities.get(i).getString("organizerId"),facilities.get(i).getString("profileImageUrl")));
                    facilityOrganizerIds.add(facilities.get(i).getString("organizerId"));
                }
            }
        });
    }
}