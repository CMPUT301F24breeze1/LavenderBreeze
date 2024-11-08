package com.example.myapplication.entrant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.myapplication.R;


public class EntrantEventPage extends Fragment {


    Button homeButton, profileButton, eventsButton;

    public EntrantEventPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_event_page, container, false);
        intializeBottomNavButton(view);
        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        Button events = view.findViewById(R.id.button_go_to_entrant_event_list);
        events.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventPage_to_entrantEventsList)
        );
        return view;
    }
    public void intializeBottomNavButton(View view){
        homeButton = view.findViewById(R.id.homeButton);
        profileButton = view.findViewById(R.id.profileButton);
        eventsButton = view.findViewById(R.id.eventsButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantEventPage_to_home); // ID of the destination in nav_graph.xml
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantEventPage_to_entrantProfile3); // ID of the destination in nav_graph.xml
            }
        });
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantEventPage_to_entrantEventsList); // ID of the destination in nav_graph.xml
            }
        });
    }

}