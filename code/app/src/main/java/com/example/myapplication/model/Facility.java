package com.example.myapplication.model;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.DeviceUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facility {
    private String facilityName;
    private String facilityAddress;
    private String facilityEmail; // Consistent naming
    private String facilityPhoneNumber;
    private String organizerId;
    private List<Event> events;
    private String facilityId;
    private FirebaseFirestore db;
    private CollectionReference facilities;

    public Facility() {
        this.facilityName = "Default Name";
        this.facilityAddress = "Default Address";
        this.facilityEmail = "email@default.com";
        this.facilityPhoneNumber = "123456789";
    }

    public Facility(Context context) {
        this.facilityName = "Default Name";
        this.facilityAddress = "Default Address";
        this.facilityEmail = "email@default.com";
        this.facilityPhoneNumber = "123456789";
        this.organizerId = DeviceUtils.getDeviceId(context);

        initializeFirestore();
    }

    public Facility(String facilityName, String facilityAddress, String facilityEmail,
                    String facilityPhoneNumber, String organizerId) {
        this.facilityName = facilityName;
        this.facilityAddress = facilityAddress;
        this.facilityEmail = facilityEmail;
        this.facilityPhoneNumber = facilityPhoneNumber;
        this.organizerId = organizerId;

        initializeFirestore();
    }

    private void initializeFirestore() {
        db = FirebaseFirestore.getInstance();
        facilities = db.collection("facilities");
    }

    // Save facility to Firestore only if it doesn't already exist
    public void saveToFirestoreIfNotExists() {
        if (facilities != null) {
            // Check if a facility with the same name already exists
            facilities.whereEqualTo("facilityName", facilityName).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().isEmpty()) {
                            // Facility does not exist, so add it to Firestore
                            saveToFirestore();
                        } else {
                            Log.d("Firestore", "Facility already exists, skipping save.");
                        }
                    });
        } else {
            Log.e("Firestore Error", "Firestore database is not initialized.");
        }
    }

    public void saveToFirestore() {
        if (facilities != null) {
            Map<String, Object> facilityData = new HashMap<>();
            facilityData.put("facilityName", facilityName);
            facilityData.put("facilityAddress", facilityAddress);
            facilityData.put("facilityEmail", facilityEmail); // Consistent key
            facilityData.put("facilityPhoneNumber", facilityPhoneNumber);
            facilityData.put("organizerId", organizerId);

            facilities.add(facilityData)
                    .addOnSuccessListener(documentReference -> {
                        facilityId = documentReference.getId();
                        Log.d("Firestore", "Facility added with ID: " + facilityId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Database Error", "Failed to store to database", e);
                    });
        } else {
            Log.e("Firestore Error", "Firestore database is not initialized.");
        }
    }

    // Getter and Setter methods
    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityAddress() {
        return facilityAddress;
    }

    public void setFacilityAddress(String facilityAddress) {
        this.facilityAddress = facilityAddress;
    }

    public String getFacilityEmail() {
        return facilityEmail;
    }

    public void setFacilityEmail(String facilityEmail) {
        this.facilityEmail = facilityEmail;
    }

    public String getFacilityPhoneNumber() {
        return facilityPhoneNumber;
    }

    public void setFacilityPhoneNumber(String facilityPhoneNumber) {
        this.facilityPhoneNumber = facilityPhoneNumber;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String getFacilityId() {
        return facilityId;
    }
    public void setFacilityId(String facilityId){
        this.facilityId = facilityId;
    }
}
