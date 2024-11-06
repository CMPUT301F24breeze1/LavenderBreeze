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
 * Use the {@link OrgFacilityList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgFacilityList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrgFacilityList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrgFacilityList.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgFacilityList newInstance(String param1, String param2) {
        OrgFacilityList fragment = new OrgFacilityList();
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
        View view = inflater.inflate(R.layout.fragment_org_facility_list, container, false);

        Button buttonGoToAddFacility = view.findViewById(R.id.button_go_to_add_facility_from_facility_list);
        buttonGoToAddFacility.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_orgFacilityList_to_orgAddFacility));

//        Button buttonGoToEditFacility = view.findViewById(R.id.button_go_to_edit_facility_from_facility_list);
//        buttonGoToAddFacility.setOnClickListener(v ->
//                Navigation.findNavController(v).navigate(R.id.action_orgFacilityList_to_orgEditFacility));

        Button buttonGoToEventFacility = view.findViewById(R.id.button_go_to_org_event_list_from_facility_list);
        buttonGoToEventFacility.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_orgFacilityList_to_OrgEventLst));



        return view;
    }
}