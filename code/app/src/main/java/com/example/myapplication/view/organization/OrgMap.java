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

/**
 * A fragment that displays a Google Map with markers representing user locations for a specific event.
 * Fetches location data from Firestore and displays it on the map.
 */
public class OrgMap extends Fragment implements OnMapReadyCallback {

    /**
     * The event ID used to fetch location data from Firestore.
     */
    private String eventId;

    /**
     * The GoogleMap object used to display the map and markers.
     */
    private GoogleMap mMap;

    /**
     * Firestore instance used for database operations.
     */
    private FirebaseFirestore db;

    /**
     * Required empty public constructor.
     */
    public OrgMap() {
        // Required empty public constructor
    }

    /**
     * Called when the fragment is created. Initializes the event ID if passed via arguments.
     *
     * @param savedInstanceState The saved instance state of the fragment.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }
    }

    /**
     * Factory method to create a new instance of OrgMap with parameters.
     *
     * @param param1 The first parameter.
     * @param param2 The second parameter.
     * @return A new instance of OrgMap.
     */
    public static OrgMap newInstance(String param1, String param2) {
        OrgMap fragment = new OrgMap();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called to inflate the layout for the fragment.
     * Sets up the back button to navigate to a specified action.
     *
     * @param inflater           The LayoutInflater used to inflate the view.
     * @param container          The parent container of the fragment.
     * @param savedInstanceState The saved instance state.
     * @return The inflated view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_org_map, container, false);

        // Set up back button click listener
        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // Navigate back to the specified action
            Navigation.findNavController(v).navigate(R.id.action_OrgMap_to_OrgEventWaitingLst, getArguments());
        });
        return view;
    }

    /**
     * Called after the view has been created.
     * Initializes Firestore and sets up the map fragment to get notified when the map is ready.
     *
     * @param view               The view of the fragment.
     * @param savedInstanceState The saved instance state.
     */
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

    /**
     * Called when the Google Map is ready to be used.
     * Fetches location data from Firestore and adds markers to the map.
     *
     * @param googleMap The GoogleMap object.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Fetch coordinates from Firestore and display them on the map
        fetchCoordinatesAndMapMarkers();
    }

    /**
     * Fetches user location coordinates for the specified event from Firestore
     * and adds markers for each location to the Google Map.
     * Also adjusts the camera view to encompass all markers.
     */
    private void fetchCoordinatesAndMapMarkers() {
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
