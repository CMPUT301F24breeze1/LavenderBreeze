package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantEventsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantEventsList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EntrantEventsList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntrantEventsList.
     */
    // TODO: Rename and change types and number of parameters
    public static EntrantEventsList newInstance(String param1, String param2) {
        EntrantEventsList fragment = new EntrantEventsList();
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
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_events_list, container, false);

        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        Button profile = view.findViewById(R.id.button_go_to_entrant_profile);
        profile.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantProfile3)
        );

        Button event = view.findViewById(R.id.button_go_to_entrant_event_page);
        event.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantEventPage)
        );

        Button qr = view.findViewById(R.id.button_go_to_qr_scanner);
        qr.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantQrScan)
        );

        Button home = view.findViewById(R.id.button_go_to_home);
        home.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_home)
        );
        return inflater.inflate(R.layout.fragment_entrant_events_list, container, false);
    }
}