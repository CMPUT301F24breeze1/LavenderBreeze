package com.example.myapplication.view.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminEventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminEventDetailsFragment extends Fragment {

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
    private static final String ARG_PARAM13 = "declined";
    private static final String ARG_POSTER_URL = "posterURL";


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
    private ArrayList<String> declined;
    private String posterURL;
    private ImageView posterView;
    private FirebaseFirestore db;
    public AdminEventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminEventDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminEventDetailsFragment newInstance(String param1,ArrayList<String> param2,ArrayList<String> param3, int param4, String param5,
                                                        String param6, String param7, String param8, int param9, String param10, String param11, String param12,
                                                        ArrayList<String> param13, String posterUrl) {
        AdminEventDetailsFragment fragment = new AdminEventDetailsFragment();
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
        args.putStringArrayList(ARG_PARAM13, param13);
        args.putString(ARG_POSTER_URL, posterUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_event_details, container, false);

        // Declaring and setting all event information
        TextView eventNameTextView = view.findViewById(R.id.admin_event_name);
        TextView eventDescriptionTextView = view.findViewById(R.id.admin_event_description);
        TextView eventStartTextView = view.findViewById(R.id.admin_start_date);
        TextView eventEndTextView = view.findViewById(R.id.admin_end_date);
        TextView eventCapacityTextView = view.findViewById(R.id.admin_event_capacity);
        TextView eventPriceTextView = view.findViewById(R.id.admin_event_price);
        TextView eventRegistrationStartTextView = view.findViewById(R.id.admin_registration_start);
        TextView eventRegistrationEndTextView = view.findViewById(R.id.admin_registration_end);

        eventNameTextView.setText(eventName);
        eventDescriptionTextView.setText(eventDescription);
        eventStartTextView.setText(eventStart);
        eventEndTextView.setText(eventEnd);
        eventCapacityTextView.setText(String.valueOf(capacity));
        eventPriceTextView.setText(String.valueOf(price));
        eventRegistrationStartTextView.setText(registrationStart);
        eventRegistrationEndTextView.setText(registrationEnd);

        posterView = view.findViewById(R.id.admin_event_poster);

        // Fetching poster URL from Firestore and displaying
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && getView() != null) { // Ensure the fragment is attached
                        if (documentSnapshot.exists()) {
                            String fetchedPosterURL = documentSnapshot.getString("posterUrl");
                            if (fetchedPosterURL != null && !fetchedPosterURL.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(fetchedPosterURL)
                                        .into(posterView);
                            } else {
                                Log.d("OrgEvent", "Poster URL not available");
                            }
                        } else {
                            Log.d("OrgEvent", "Event not found in Firestore");
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("OrgEvent", "Error fetching event data", e));

        // Update event details in with dynamic updates
        db.collection("events").document(eventId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.e("OrgEvent", "Error listening for changes", error);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        // Retrieve and update the fields
                        String updatedEventName = snapshot.getString("eventName");
                        String updatedEventDescription = snapshot.getString("eventDescription");
                        Date updatedEventStart = snapshot.getTimestamp("eventStart").toDate(); // Convert to Date
                        Date updatedEventEnd = snapshot.getTimestamp("eventEnd").toDate();
                        int updatedCapacity = snapshot.getLong("capacity").intValue();
                        Double updatedPrice = snapshot.getDouble("price");
                        Date updatedRegistrationStart = snapshot.getTimestamp("registrationStart").toDate();
                        Date updatedRegistrationEnd = snapshot.getTimestamp("registrationEnd").toDate();

                        // Format the timestamps for display
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                        String formattedEventStart = dateFormat.format(updatedEventStart);
                        String formattedEventEnd = dateFormat.format(updatedEventEnd);
                        String formattedRegistrationStart = dateFormat.format(updatedRegistrationStart);
                        String formattedRegistrationEnd = dateFormat.format(updatedRegistrationEnd);

                        // Update the UI
                        eventNameTextView.setText(updatedEventName);
                        eventDescriptionTextView.setText(updatedEventDescription);
                        eventStartTextView.setText(formattedEventStart);
                        eventEndTextView.setText(formattedEventEnd);
                        eventCapacityTextView.setText(String.valueOf(updatedCapacity));
                        eventPriceTextView.setText(String.valueOf(updatedPrice));
                        eventRegistrationStartTextView.setText(formattedRegistrationStart);
                        eventRegistrationEndTextView.setText(formattedRegistrationEnd);
                    }
                });

        Button deleteEventButton = view.findViewById(R.id.delete_event);
        deleteEventButton.setOnClickListener(v -> {
            db.collection("events").document(eventId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed(); // Navigate back to the list
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error deleting event", Toast.LENGTH_SHORT).show();
                    });
        });

        Button deletePosterButton = view.findViewById(R.id.delete_poster);
        deletePosterButton.setOnClickListener(v -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("posterUrl", null);

            db.collection("events").document(eventId).update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Poster deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error deleting poster", Toast.LENGTH_SHORT).show();
                    });
        });

        Button deleteQrCodeButton = view.findViewById(R.id.delete_qrcode);
        deleteQrCodeButton.setOnClickListener(v -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("qrCodeData", null);

            db.collection("events").document(eventId).update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "QR Code deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error deleting QR Code", Toast.LENGTH_SHORT).show();
                    });
        });



        return view;
    }

}