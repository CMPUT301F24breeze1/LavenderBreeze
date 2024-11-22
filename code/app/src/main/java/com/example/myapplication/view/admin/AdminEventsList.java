package com.example.myapplication.view.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.controller.EventsListAdapter;
import com.example.myapplication.model.Event;
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
    private EventsListAdapter eventArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    List<DocumentSnapshot> events;
    ArrayList<String> eventIds = new ArrayList<>();



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

        //initialize database instance
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        Task<QuerySnapshot> task = eventsRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                events = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for(int i = 0; i < events.size(); i++){
                    String eventName = events.get(i).getString("eventName");
                    String eventId = events.get(i).getId();
                    int capacity = events.get(i).getDouble("capacity").intValue();


                    // I set the event description as the doc ID to make it easier to pass when clicked
                    eventIds.add(eventId);
                    eventDataList.add(new Event(eventName, events.get(i).getString("eventDescription"), ((Timestamp) events.get(i).get("eventStart")).toDate(), ((Timestamp) events.get(i).get("eventEnd")).toDate(),
                            ((Timestamp) events.get(i).get("registrationStart")).toDate(), ((Timestamp) events.get(i).get("registrationEnd")).toDate(), events.get(i).getString("location"),capacity, ((Long)events.get(i).get("price")).intValue(),
                            events.get(i).getString("posterUrl"), events.get(i).getString("qrCodeHash"), events.get(i).getString("organizerId")));
                }
                eventArrayAdapter.notifyDataSetChanged();
            }
        });

        //initialize event data list and array adapter
        eventList = view.findViewById(R.id.list_view_admin_events_list);
        eventDataList = new ArrayList<Event>();

        eventArrayAdapter = new EventsListAdapter(this.getContext(), eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        Button home = view.findViewById(R.id.button_go_to_home_from_admin_events_list);
        home.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminEventsList_to_home));


        return view;
    }
}