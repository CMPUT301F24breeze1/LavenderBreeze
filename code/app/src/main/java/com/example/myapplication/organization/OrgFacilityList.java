package com.example.myapplication.organization;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Facility;
import com.example.myapplication.model.FacilityAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Fragment for displaying a list of facilities
 */
public class OrgFacilityList extends Fragment {

    private ListView facilityList;
    private ArrayList<Facility> facilityDataList;
    private FacilityAdapter facilityArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference facilities;

    public OrgFacilityList() {
        // Required empty public constructor
    }

    public static OrgFacilityList newInstance(String param1, String param2) {
        OrgFacilityList fragment = new OrgFacilityList();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes a reference to the facilities collection in FireBase DB
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        facilities = db.collection("facilities");
    }

    /**
     * Sets up proper buttons and views for the facility list page
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_facility_list, container, false);

        // Initialize button for adding facilities
        Button buttonGoToAddFacility = view.findViewById(R.id.button_go_to_add_facility_from_facility_list);
        buttonGoToAddFacility.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_orgFacilityList_to_orgAddFacility));

        // Button to navigate to event list
        Button buttonGoToEventFacility = view.findViewById(R.id.button_go_to_org_event_list_from_facility_list);
        buttonGoToEventFacility.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_orgFacilityList_to_OrgEventLst));


        // Initialize ListView and the data list
        facilityList = view.findViewById(R.id.facility_list_view);
        facilityDataList = new ArrayList<>();
        facilityArrayAdapter = new FacilityAdapter(getContext(), facilityDataList);
        facilityList.setAdapter(facilityArrayAdapter);

        // Load facilities from Firestore
        loadFacilitiesFromFirestore();

        return view;
    }

    /**
     * Gets ALL facilities from the DB to display in list
     */
    private void loadFacilitiesFromFirestore() {
        facilities.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String facilityName = document.getString("facilityName");
                            String facilityId = document.getId();  // Get the Firestore document ID
                            Facility facility = new Facility(requireContext());
                            facility.setFacilityName(facilityName);
                            facility.setFacilityId(facilityId);

                            // Add the facility to the list
                            facilityDataList.add(facility);
                        }

                        // Notify adapter that data has changed
                        facilityArrayAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load facilities", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
