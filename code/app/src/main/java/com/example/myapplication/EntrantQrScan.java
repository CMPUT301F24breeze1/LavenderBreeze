package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantQrScan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantQrScan extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "DeviceID";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String deviceID;
    private String mParam2;

    public EntrantQrScan() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntrantQrScan.
     */
    // TODO: Rename and change types and number of parameters
    public static EntrantQrScan newInstance(String param1, String param2) {
        EntrantQrScan fragment = new EntrantQrScan();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deviceID = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_qr_scan, container, false);

        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        Button eventPage = view.findViewById(R.id.button_go_to_entrant_event_page);
        eventPage.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantQrScan_to_entrantEventPage)
        );
        Button eventList = view.findViewById(R.id.button_go_to_entrant_event_list);
        eventList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantQrScan_to_entrantEventsList)
        );

        Button scanQR = view.findViewById(R.id.scanQR);
        scanQR.setOnClickListener();
        return view;
    }
}