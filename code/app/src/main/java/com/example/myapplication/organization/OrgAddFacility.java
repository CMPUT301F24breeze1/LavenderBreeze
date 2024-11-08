package com.example.myapplication.organization;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.DeviceUtils;
import com.example.myapplication.R;
import com.example.myapplication.model.Facility;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrgAddFacility#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrgAddFacility extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText editFacilityName;
    private EditText editFacilityAddress;
    private EditText editFacilityEmail;
    private EditText editFacilityPhoneNumber;
    private Button createFacilityButton;
    private String organizerId;
    private Facility existingFacility;
    private FirebaseFirestore db;

    public OrgAddFacility() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrgAddFacility.
     */
    // TODO: Rename and change types and number of parameters
    public static OrgAddFacility newInstance(String param1, String param2) {
        OrgAddFacility fragment = new OrgAddFacility();
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
        organizerId = DeviceUtils.getDeviceId(requireContext());
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_add_facility, container, false);

        editFacilityName = view.findViewById(R.id.editTextFacilityName);
        editFacilityAddress = view.findViewById(R.id.editTextFacilityAddress);
        editFacilityEmail = view.findViewById(R.id.editTextFacilityEmail);
        editFacilityPhoneNumber = view.findViewById(R.id.editTextFacilityPhone);
        createFacilityButton = view.findViewById(R.id.buttonCreateFacility);

        fetchExistingFacility();

        createFacilityButton.setOnClickListener(v -> {
            String facilityName = editFacilityName.getText().toString().trim();
            String facilityAddress = editFacilityAddress.getText().toString().trim();
            String facilityEmail = editFacilityEmail.getText().toString().trim();
            String facilityPhoneNumber = editFacilityPhoneNumber.getText().toString().trim();

            if (facilityName.isEmpty() || facilityAddress.isEmpty() || facilityEmail.isEmpty() || facilityPhoneNumber.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!facilityPhoneNumber.matches("\\d{7,15}")) {
                Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingFacility != null) {
                // Update existing facility
                existingFacility.setFacilityName(facilityName);
                existingFacility.setFacilityAddress(facilityAddress);
                existingFacility.setFacilityEmail(facilityEmail);
                existingFacility.setFacilityPhoneNumber(facilityPhoneNumber);
                updateFacilityInFirestore();
            } else {
                // Create new facility
                Facility facility = new Facility(facilityName, facilityAddress, facilityEmail, facilityPhoneNumber, organizerId);
                facility.saveToFirestore();
                Navigation.findNavController(v).navigate(R.id.action_orgAddFacility_to_OrgEventLst);
            }
        });

        Button buttonGoToHome = view.findViewById(R.id.button_go_to_home_from_add_facility);
        buttonGoToHome.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_orgAddFacility_to_home));

        return view;
    }
    private void fetchExistingFacility() {
        db.collection("facilities")
                .whereEqualTo("organizerId", organizerId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        existingFacility = document.toObject(Facility.class);

                        if (existingFacility != null) {
                            existingFacility.setFacilityId(document.getId());  // Store document ID
                            editFacilityName.setText(existingFacility.getFacilityName());
                            editFacilityAddress.setText(existingFacility.getFacilityAddress());
                            editFacilityEmail.setText(existingFacility.getFacilityEmail());
                            editFacilityPhoneNumber.setText(existingFacility.getFacilityPhoneNumber());
                        } else {
                            Log.d("OrgAddFacility", "existingFacility is null after conversion.");
                        }
                    } else {
                        Log.d("OrgAddFacility", "No facility found with organizerId: " + organizerId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OrgAddFacility", "Error fetching facility details", e);
                    Toast.makeText(getContext(), "Error fetching facility details", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFacilityInFirestore() {
        String facilityId = existingFacility.getFacilityId();
        if (facilityId != null) {
            db.collection("facilities").document(facilityId)
                    .set(existingFacility)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Facility updated successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.action_orgAddFacility_to_OrgEventLst);
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update facility", Toast.LENGTH_SHORT).show());
        } else {
            Log.e("OrgAddFacility", "Facility ID is null, cannot update");
            Toast.makeText(getContext(), "Facility ID is missing", Toast.LENGTH_SHORT).show();
        }
    }
}