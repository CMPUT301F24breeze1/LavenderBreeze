package com.example.myapplication.organization;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link org_events_lst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class org_events_lst extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public org_events_lst() {
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
    public static org_events_lst newInstance(String param1, String param2) {
        org_events_lst fragment = new org_events_lst();
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
        View view = inflater.inflate(R.layout.fragment_org_events_lst, container, false);

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