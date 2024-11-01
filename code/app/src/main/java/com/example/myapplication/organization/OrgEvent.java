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
 * Use the {@link OrgEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgEvent extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrgEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment org_event.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgEvent newInstance(String param1, String param2) {
        OrgEvent fragment = new OrgEvent();
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
        View view = inflater.inflate(R.layout.fragment_org_event, container, false);

        // Button to navigate to the QR code fragment
        Button buttonGoToQRCode = view.findViewById(R.id.button_go_to_qrcode_from_org_event);
        buttonGoToQRCode.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_to_org_view_event_qrcode)
        );

        // Button to navigate to the Edit Event fragment
        Button buttonGoToEditEvent = view.findViewById(R.id.button_go_to_edit_event_from_org_event);
        buttonGoToEditEvent.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_to_org_edit_event2)
        );

        // Button to navigate to the Waiting List fragment
        Button buttonGoToWaitingList = view.findViewById(R.id.button_go_to_waiting_list_from_org_event);
        buttonGoToWaitingList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_to_org_event_waiting_lst)
        );

        // Button to navigate to the Selected List fragment
        Button buttonGoToSelectedList = view.findViewById(R.id.button_go_to_selected_list_from_org_event);
        buttonGoToSelectedList.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_org_event_to_org_event_selected_lst)
        );

        return view;

    }
}