package com.example.myapplication.organization;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrgEventSelectedLst extends Fragment {

    private FirebaseFirestore db;
    private static final String ARG_EVENT_ID = "eventId";
    private ListView listViewSelectedEntrants;
    private List<String> selectedEntrants = new ArrayList<>();
    private List<String> acceptedEntrants = new ArrayList<>();
    private List<String> declinedEntrants = new ArrayList<>();

    private OrgEventSelectedLstAdapter adapter;  // Adapter for RecyclerView


    public OrgEventSelectedLst() {
        // Required empty public constructor
    }
    public static OrgEventSelectedLst newInstance(String eventId) {
        OrgEventSelectedLst fragment = new OrgEventSelectedLst();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_org_event_selected_lst, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        listViewSelectedEntrants = view.findViewById(R.id.list_view_event_selected_list);

        loadSelectedEntrants();

        // Set the initial filter (e.g., all entrants) when the fragment is first created
        adapter = new OrgEventSelectedLstAdapter(getContext(), selectedEntrants, acceptedEntrants, declinedEntrants);
        listViewSelectedEntrants.setAdapter(adapter);

        // Set up filter buttons
        view.findViewById(R.id.filterAll).setOnClickListener(v -> displayFilteredEntrants("all"));
        view.findViewById(R.id.filterAccepted).setOnClickListener(v -> displayFilteredEntrants("accepted"));
        view.findViewById(R.id.filterCancelled).setOnClickListener(v -> displayFilteredEntrants("cancelled"));

        Button backButton = view.findViewById(R.id.button_go_to_event_from_org_event_selected_lst);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        Button sendNotificationButton = view.findViewById(R.id.button_go_to_notif_from_org_event_selected_lst);
        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationToEntrants();
            }
        });
    }

    private void loadSelectedEntrants() {
        db.collection("events").document(ARG_EVENT_ID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    selectedEntrants = (List<String>) document.get("selectedEntrants");
                    acceptedEntrants = (List<String>) document.get("acceptedEntrants");
                    declinedEntrants = (List<String>) document.get("declinedEntrants");

                    // Ensure lists are not null
                    if (selectedEntrants == null) selectedEntrants = new ArrayList<>();
                    if (acceptedEntrants == null) acceptedEntrants = new ArrayList<>();
                    if (declinedEntrants == null) declinedEntrants = new ArrayList<>();


                    // Display all entrants by default
                    displayFilteredEntrants("all");

                } else {
                    Toast.makeText(getContext(), "Event data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("OrgEventSelectedLst", "Error fetching event data", task.getException());
            }
        });
    }

    private void displayFilteredEntrants(String filterType) {
        List<String> filteredEntrants = new ArrayList<>();
        // Filter entrants based on the selected filter type
        switch (filterType) {
            case "accepted":
                filteredEntrants.addAll(acceptedEntrants);
                break;
            case "declined":
                filteredEntrants.addAll(declinedEntrants);
                break;
            default:
                filteredEntrants.addAll(selectedEntrants);
                break;
        }

        // Update the adapter with the filtered list and the current filter type
        adapter.updateList(filteredEntrants, filterType);
    }

    private void sendNotificationToEntrants() {
        Toast.makeText(getContext(), "Notification sent to entrants in the selected list", Toast.LENGTH_SHORT).show();
    }


}
