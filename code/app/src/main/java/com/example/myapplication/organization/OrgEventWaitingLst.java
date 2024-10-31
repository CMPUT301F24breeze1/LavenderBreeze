package com.example.myapplication.organization;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;
<<<<<<<< HEAD:code/app/src/main/java/com/example/myapplication/organization/org_event_waiting_lst.java
import com.example.myapplication.entrant.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;
========
>>>>>>>> b1309b163bf605a73bffbd34b7d5480af71a99b8:code/app/src/main/java/com/example/myapplication/organization/OrgEventWaitingLst.java

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgEventWaitingLst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgEventWaitingLst extends Fragment {

    private List<String> entrants;
    private List<String> chosen;
    private int capacity;

    private FirebaseFirestore db;
    private CollectionReference events;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "EventId";

    // TODO: Rename and change types of parameters
    private int EventId;

    public OrgEventWaitingLst() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment org_event_waiting_lst.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgEventWaitingLst newInstance(String param1, String param2) {
        OrgEventWaitingLst fragment = new OrgEventWaitingLst();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        events = db.collection("events");
        if (getArguments() != null) {
            EventId = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_event_waiting_lst, container, false);

        db = FirebaseFirestore.getInstance();
        events = db.collection("events");
        DocumentReference eventRef = db.collection("events").document(String.valueOf(EventId));
        eventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Event event = documentSnapshot.toObject(Event.class);
                entrants = event.getWaitlist();
            }
        });


        Button select_entrants = view.findViewById(R.id.button_select_entrants);
        select_entrants.setOnClickListener(view1 -> {
            if(capacity >= entrants.size()){
                chosen.addAll(entrants);
                entrants.clear();
            } else {
                Collections.shuffle(entrants);
                for(int i = 0; i < capacity; i++){
                    chosen.add(entrants.get(0));
                    entrants.remove(0);
                }
            }


        });
        eventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Event event = documentSnapshot.toObject(Event.class);
                event.setSelectedEntrants(chosen);
            }
        });

        // Button to navigate to the Selected List
        Button buttonGoToSelectedList = view.findViewById(R.id.button_go_to_selected_list_from_org_event_waiting_lst);
        buttonGoToSelectedList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_waiting_lst_to_org_event_selected_lst)
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