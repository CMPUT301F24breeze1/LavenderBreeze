package com.example.myapplication.organization;

import android.os.Bundle;

import androidx.annotation.Nullable;
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
import com.example.myapplication.model.Event;
import com.example.myapplication.model.EventsListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgEventLst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgEventLst extends Fragment {

    private ListView eventList;
    private ArrayList<Event> eventDataList;
    private EventsListAdapter eventArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public OrgEventLst() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment org_events_lst.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgEventLst newInstance(String param1, String param2) {
        OrgEventLst fragment = new OrgEventLst();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_events_lst, container, false);

        //initialize database instance
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        Task<QuerySnapshot> task = eventsRef.get();

        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                task.getResult().getDocuments();
            }
        });

        // initialize array of waitlists and selected entrant lists
        ArrayList < ArrayList < String >> waitlists = new ArrayList<>();
        ArrayList<ArrayList<String>> selecteds = new ArrayList<>();

        //initialize event data list and array adapter
        eventList = view.findViewById(R.id.event_list_view);
        eventDataList = new ArrayList<Event>();

        eventArrayAdapter = new EventsListAdapter(this.getContext(), eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        //When anything regarding the database happens, this code will run, updating the list
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                }
                if(value != null){
                    eventDataList.clear();
                    for(QueryDocumentSnapshot doc:value){
                        // Get event name, capacity, waitlist, and selected entrants list
                        String eventName = doc.getString("eventName");
                        int capacity = doc.getDouble("capacity").intValue();
                        ArrayList<String> waitlist = (ArrayList<String>) doc.get("waitlist");
                        ArrayList<String> selected = (ArrayList<String>) doc.get("selectedEntrants");

                        // Checking that data was read in properly
                        Log.d("Kenny", String.valueOf(waitlist.size()));
                        Log.d("Kenny", String.valueOf(selected.size()));
                        Log.d("Firestore", String.format("Event %s added",eventName));

                        // add waitlist and selected list to respective lists
                        waitlists.add(waitlist);
                        selecteds.add(selected);


                        // I set the event description as the doc ID to make it easier to pass when clicked
                        eventDataList.add(new Event(eventName, doc.getId(), new Date(), new Date(),
                                new Date(), new Date(), "String location",capacity, 0,
                                "String posterUrl", "qrHash", "String organizerId"));

                    }
                }
                eventArrayAdapter.notifyDataSetChanged();
            }
        });
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // put necessary arguments into a bundle

                Bundle bundle = new Bundle();
                bundle.putString("eventId",eventDataList.get(i).getEventDescription());
                bundle.putStringArrayList("waitlist",waitlists.get(i));
                bundle.putStringArrayList("selected",selecteds.get(i));
                bundle.putInt("capacity",eventDataList.get(i).getCapacity());

                //check that the lists have the correct length

                Log.d("Kenny", "events_lst waitlist size: "+String.valueOf(waitlists.get(i).size()));
                Log.d("Kenny", "events_lst selected size: "+String.valueOf(selecteds.get(i).size()));

                Navigation.findNavController(view).navigate(R.id.action_org_events_lst_to_org_event,bundle);
            }
        });

        Button button_go_to_home_from_org_event_list = view.findViewById(R.id.button_go_to_home_from_org_event_list);
        button_go_to_home_from_org_event_list.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_events_lst_to_home)
        );

// Button to navigate to Event Details
        Button button_go_to_event_from_org_event_list = view.findViewById(R.id.button_go_to_event_from_org_event_list);
        button_go_to_event_from_org_event_list.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_events_lst_to_org_event)
        );
        Button buttonGoToAddEvent = view.findViewById(R.id.button_go_to_add_event_from_org_events_lst);
        buttonGoToAddEvent.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_events_lst_to_org_add_event)
        );
        Button buttonGoToEditOrganizer = view.findViewById(R.id.button_go_to_edit_organizer_from_org_events_lst);
        buttonGoToEditOrganizer.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_events_lst_to_org_edit_organizer)
        );

        return view;
    }
}