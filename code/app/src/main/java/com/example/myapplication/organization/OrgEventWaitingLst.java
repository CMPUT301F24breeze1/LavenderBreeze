package com.example.myapplication.organization;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgEventWaitingLst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgEventWaitingLst extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "eventId";
    private static final String ARG_PARAM2 = "waitlist";
    private static final String ARG_PARAM3 = "selected";
    private static final String ARG_PARAM4 = "capacity";
    private static final String ARG_PARAM5 = "eventName";
    private static final String ARG_PARAM6 = "eventDescription";
    private static final String ARG_PARAM7 = "eventStart";
    private static final String ARG_PARAM8 = "eventEnd";
    private static final String ARG_PARAM9 = "price";
    private static final String ARG_PARAM10 = "registrationStart";
    private static final String ARG_PARAM11 = "registrationEnd";
    private static final String ARG_PARAM12 = "qrCodeHash";


    // TODO: Rename and change types of parameters
    private String eventId;
    private ArrayList<String> waitlist;
    private ArrayList<String> selected;
    private int capacity;
    private String eventName;
    private String eventDescription;
    private String eventStart;
    private String eventEnd;
    private int price;
    private String registrationStart;
    private String registrationEnd;
    private String qrCodeHash;

    public OrgEventWaitingLst() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment org_event_waiting_lst.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgEventWaitingLst newInstance(String param1,ArrayList<String> param2,ArrayList<String> param3, int param4, String param5,
                                                 String param6, String param7, String param8, int param9, String param10, String param11, String param12) {
        OrgEventWaitingLst fragment = new OrgEventWaitingLst();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putStringArrayList(ARG_PARAM2,param2);
        args.putStringArrayList(ARG_PARAM3,param3);
        args.putInt(ARG_PARAM4,param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        args.putString(ARG_PARAM8, param8);
        args.putInt(ARG_PARAM9, param9);
        args.putString(ARG_PARAM10, param10);
        args.putString(ARG_PARAM11, param11);
        args.putString(ARG_PARAM12, param12);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_PARAM1);
            waitlist = getArguments().getStringArrayList(ARG_PARAM2);
            selected = getArguments().getStringArrayList(ARG_PARAM3);
            capacity = getArguments().getInt(ARG_PARAM4);
            eventName = getArguments().getString(ARG_PARAM5);
            eventDescription = getArguments().getString(ARG_PARAM6);
            eventStart = getArguments().getString(ARG_PARAM7);
            eventEnd = getArguments().getString(ARG_PARAM8);
            price = getArguments().getInt(ARG_PARAM9);
            registrationStart = getArguments().getString(ARG_PARAM10);
            registrationEnd = getArguments().getString(ARG_PARAM11);
            qrCodeHash = getArguments().getString(ARG_PARAM12);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_event_waiting_lst, container, false);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        Task<DocumentSnapshot> task = eventsRef.document(eventId).get();
        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DocumentSnapshot doc = task.getResult();
                waitlist = (ArrayList<String>) doc.get("waitlist");
                selected = (ArrayList<String>) doc.get("selectedEntrants");
                Log.d("Kenny", "data loaded");
            }
        });

        FloatingActionButton select_entrants = view.findViewById(R.id.button_select_entrants);
        select_entrants.setOnClickListener(view1 -> {
            Log.d("Kenny", "Entrants being Selected...");
            if(waitlist.isEmpty()){
                Toast.makeText(getActivity(), "Nobody has signed up for this event yet",
                        Toast.LENGTH_LONG).show();
            } else if (capacity == 0) {
                Toast.makeText(getActivity(), "Invalid event capacity!",
                        Toast.LENGTH_LONG).show();
            } else if (!selected.isEmpty()) {
                Toast.makeText(getActivity(), "Selection has already occured for this event!",
                        Toast.LENGTH_LONG).show();
            } else {
                if (capacity >= waitlist.size()) {
                    selected.addAll(waitlist);
                    Log.d("Kenny", "added all waitlisted entrants to chosen");
                    waitlist.clear();
                } else {
                    Collections.shuffle(waitlist);
                    for (int i = 0; i < capacity; i++) {
                        selected.add(waitlist.get(0));

                        Log.d("Kenny", "added " + waitlist.get(0) + " to chosen");
                        waitlist.remove(0);
                    }
                }
            }

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("waitlist", waitlist);
            eventData.put("selectedEntrants", selected);
            Log.d("Kenny", "Updating eventId: "+eventId);
            eventsRef.document(eventId).update(eventData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Event", "Event updated successfully");
                    }).addOnFailureListener(e -> {
                        Log.e("Event", "Error updating event", e);
                    });
        });

        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);
        bundle.putStringArrayList("waitlist", waitlist);
        bundle.putStringArrayList("selected", selected);
        bundle.putInt("capacity", capacity);
        bundle.putString("eventName", eventName);
        bundle.putString("eventDescription", eventDescription);
        bundle.putString("eventStart", eventStart);
        bundle.putString("eventEnd", eventEnd);
        bundle.putInt("price", price);
        bundle.putString("registrationStart", registrationStart);
        bundle.putString("registrationEnd", registrationEnd);
        bundle.putString("qrCodeHash", qrCodeHash);

        // Button to navigate to the Selected List
        Button buttonGoToSelectedList = view.findViewById(R.id.button_go_to_selected_list_from_org_event_waiting_lst);
        buttonGoToSelectedList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_waiting_lst_to_org_event_selected_lst,bundle)
        );

        Button buttonGoToEvent = view.findViewById(R.id.button_go_to_event_from_org_event_waiting_lst);
        buttonGoToEvent.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_OrgEventWaitingLst_to_OrgEvent,bundle)
        );

        // Button to navigate to Notifications
        Button buttonGoToNotif = view.findViewById(R.id.button_go_to_notif_from_org_event_waiting_lst);
        buttonGoToNotif.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_waiting_lst_to_org_notif_waiting_lst)
        );

        // Button to navigate to Map
        Button buttonGoToMap = view.findViewById(R.id.button_go_to_map_from_org_event_waiting_lst);
        buttonGoToMap.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_waiting_lst_to_org_map)
        );

        return view;
    }
}