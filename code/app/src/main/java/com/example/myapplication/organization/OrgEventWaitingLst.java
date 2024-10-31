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
 * Use the {@link OrgEventWaitingLst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgEventWaitingLst extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        View view = inflater.inflate(R.layout.fragment_org_event_waiting_lst, container, false);

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