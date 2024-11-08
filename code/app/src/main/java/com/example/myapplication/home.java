package com.example.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Fragment where user log in as organizer to create, edit, and view event/facility
 * Or where user log in as entrant to scan QR code to enter an event, or events they are registered in
 */
public class home extends Fragment {

    public home() {
        // Required empty public constructor
    }


    /**
     * Initialize fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflate view, initialize and connect UI components. Defined navigation
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        Button org = view.findViewById(R.id.button_go_to_add_facility);
        org.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_orgAddFacility)
        );

        Button entrant = view.findViewById(R.id.button_go_to_entrant_profile);
        entrant.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_entrantProfile3)
        );

        return view;
    }
}
