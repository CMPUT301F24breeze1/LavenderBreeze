package com.example.myapplication.view.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.controller.FacilityAdapter;
import com.example.myapplication.model.Event;
import com.example.myapplication.model.Facility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminFacilitiesList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminFacilitiesList extends Fragment {

    private ListView facilityList;
    private ArrayList<Facility> facilityDataList = new ArrayList<>();
    private FacilityAdapter facilityArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;
    private List<DocumentSnapshot> facilities;



    public AdminFacilitiesList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdminFacilitiesList.
     */
    public static AdminFacilitiesList newInstance() {
        AdminFacilitiesList fragment = new AdminFacilitiesList();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_facilities_list, container, false);

        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");

        facilityList = view.findViewById(R.id.list_view_admin_facilities_list);
        facilityDataList = new ArrayList<>();
        facilityArrayAdapter = new FacilityAdapter(getContext(), facilityDataList);
        facilityList.setAdapter(facilityArrayAdapter);

        Task<QuerySnapshot> task = facilitiesRef.get();
        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                facilities = task.getResult().getDocuments();
                Log.d("Firestore", "Documents Retrieved");
                for(int i = 0; i < facilities.size(); i++){

                    facilityDataList.add(new Facility(facilities.get(i).getString("facilityName"),facilities.get(i).getString("facilityAddress"),
                            facilities.get(i).getString("facilityEmail"),facilities.get(i).getString("facilityPhoneNumber"),
                            facilities.get(i).getString("organizerId"),facilities.get(i).getString("profileImageUrl")));
                }
                facilityArrayAdapter.notifyDataSetChanged();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}