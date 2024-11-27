package com.example.myapplication.view.organization;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class OrgMap extends Fragment implements OnMapReadyCallback {
    private String eventId;
    private GoogleMap mMap;
    private FirebaseFirestore db;

    public OrgMap() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            eventId = getArguments().getString("eventId");

        }
    }

    public static OrgMap newInstance(String param1, String param2) {
        OrgMap fragment = new OrgMap();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_map, container, false);

        // Set up back button click listener
        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // Navigate back to the specified action
            Navigation.findNavController(v).navigate(R.id.action_OrgMap_to_OrgEventWaitingLst, getArguments());
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Fetch coordinates from Firestore and display them on the map
        fetchCoordinatesAndMapMarkers();
    }

    private void fetchCoordinatesAndMapMarkers() {
        String eventId = "ClJvtRS4z8Vs3lBBYei3"; // Replace with the actual event ID or pass it as an argument
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> coordinates = (List<Map<String, Object>>) documentSnapshot.get("coordinates");
                        if (coordinates != null) {
                            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                            for (Map<String, Object> coord : coordinates) {
                                double latitude = (double) coord.get("latitude");
                                double longitude = (double) coord.get("longitude");
                                LatLng location = new LatLng(latitude, longitude);
                                // Add a marker for each coordinate
                                boundsBuilder.include(location);
                                mMap.addMarker(new MarkerOptions().position(location).title("User Location"));
                            }

                            // Optionally move the camera to the first location
                            if (!coordinates.isEmpty()) {
                                LatLngBounds bounds = boundsBuilder.build();
                                int padding = 100; // Padding around the edges in pixels
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                            }
                        } else {
                            Log.d("OrgMap", "No coordinates found in Firestore.");
                        }
                    } else {
                        Log.d("OrgMap", "Document does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgMap", "Failed to fetch coordinates: " + e.getMessage());
                });
    }
}
