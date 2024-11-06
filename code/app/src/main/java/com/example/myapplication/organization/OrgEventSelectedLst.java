package com.example.myapplication.organization;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrgEventSelectedLst extends Fragment {

    private FirebaseFirestore db;
    private static final String ARG_EVENT_ID = "eventId";
    private LinearLayout containerLayout;

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
        containerLayout = view.findViewById(R.id.container_layout);

        db = FirebaseFirestore.getInstance();
        loadSelectedEntrants();
    }

    private void loadSelectedEntrants() {
        db.collection("event").document(ARG_EVENT_ID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> selectedEntrants = (List<String>) document.get("selectedEntrants");
                    List<String> acceptedEntrants = (List<String>) document.get("acceptedEntrants");
                    List<String> declinedEntrants = (List<String>) document.get("declinedEntrants");

                    if (selectedEntrants != null) {
                        displayEntrants(selectedEntrants, acceptedEntrants, declinedEntrants);
                    }
                } else {
                    Toast.makeText(getContext(), "Event data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("OrgEventSelectedLst", "Error fetching event data", task.getException());
            }
        });
    }

    private void displayEntrants(List<String> selectedEntrants, List<String> acceptedEntrants, List<String> declinedEntrants) {
        for (String entrant : selectedEntrants) {
            TextView entrantView = new TextView(getContext());
            entrantView.setText(entrant);

            if (acceptedEntrants != null && acceptedEntrants.contains(entrant)) {
                entrantView.setBackgroundColor(Color.GREEN);
                entrantView.setText(entrant + " - Accepted");
            } else if (declinedEntrants != null && declinedEntrants.contains(entrant)) {
                entrantView.setBackgroundColor(Color.RED);
                entrantView.setText(entrant + " - Declined");
            } else {
                entrantView.setBackgroundColor(Color.YELLOW);
                entrantView.setText(entrant + " - Sent");
            }

            entrantView.setTextSize(18);
            entrantView.setPadding(16, 16, 16, 16);
            containerLayout.addView(entrantView);
        }
    }
}
