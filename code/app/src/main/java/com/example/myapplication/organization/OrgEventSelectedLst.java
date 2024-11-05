package com.example.myapplication.organization;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgEventSelectedLst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgEventSelectedLst extends Fragment {

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

    public OrgEventSelectedLst() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment org_event_selected_lst.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgEventSelectedLst newInstance(String param1,ArrayList<String> param2,ArrayList<String> param3, int param4, String param5,
                                                  String param6, String param7, String param8, int param9, String param10, String param11, String param12) {
        OrgEventSelectedLst fragment = new OrgEventSelectedLst();
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
        View view = inflater.inflate(R.layout.fragment_org_event_selected_lst, container, false);


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

        Button buttonGoToEvent = view.findViewById(R.id.button_go_to_event_from_org_event_selected_lst);
        buttonGoToEvent.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_OrgEventSelectedLst_to_OrgEvent,bundle)
        );
        // Inflate the layout for this fragment
        return view;
    }
}