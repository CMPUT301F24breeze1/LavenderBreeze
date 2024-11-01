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
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.entrant.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgEventWaitingLst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgEventWaitingLst extends Fragment {

    private List<String> entrants;
    private List<String> chosen;
    private int capacity = 3;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "eventId";

    // TODO: Rename and change types of parameters
    private String eventId;

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
    public static OrgEventWaitingLst newInstance(String param1) {
        OrgEventWaitingLst fragment = new OrgEventWaitingLst();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_event_waiting_lst, container, false);

        Event event = new Event(eventId);

        entrants = event.getWaitlist();
        chosen = event.getSelectedEntrants();

        Button select_entrants = view.findViewById(R.id.button_select_entrants);
        select_entrants.setOnClickListener(view1 -> {
            Log.d("Kenny", "Entrants being Selected...");
            if(capacity >= entrants.size()){
                chosen.addAll(entrants);
                Log.d("Kenny", "added all waitlisted entrants to chosen");
                entrants.clear();
            } else {
                Collections.shuffle(entrants);
                for(int i = 0; i < capacity; i++){
                    chosen.add(entrants.get(0));
                    Log.d("Kenny", "added: " + entrants.get(0) + " to chosen");
                    entrants.remove(0);
                }
            }

            event.setSelectedEntrants(chosen);
            event.setWaitlist(entrants);
            event.saveEvent();
        });



        // Button to navigate to the Selected List
        Button buttonGoToSelectedList = view.findViewById(R.id.button_go_to_selected_list_from_org_event_waiting_lst);
        buttonGoToSelectedList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_waiting_lst_to_org_event_selected_lst)
        );

        Bundle bundle = new Bundle();
        bundle.putString("eventId",eventId);
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