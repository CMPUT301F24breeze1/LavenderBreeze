package com.example.myapplication.model;

import android.provider.Settings;
import com.example.myapplication.DeviceUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
public class Facility {
    private String facilityName;
    private String facilityAddress;
    private String facilityEmailAddress;
    private int facilityPhoneNumber;
    private String organizerId;
    private String facilityId;
    private List<Event> events;
    private FirebaseFirestore db;
    private CollectionReference facilities;

    public Facility(Context context){
        this.facilityName = "Default Name";
        this.facilityAddress = "Default Address";
        this.facilityEmailAddress = "email@default.com";
        this.facilityPhoneNumber = 123456789;
        this.organizerId = DeviceUtils.getDeviceId(context);
        savetoDB();
    }
    public Facility(String facilityName, String facilityAddress, String facilityEmailAddress, int facilityPhoneNumber,
                    String orgarnizerId) {
        this.facilityName = facilityName;
        this.facilityAddress = facilityAddress;
        this.facilityEmailAddress = facilityEmailAddress;
        this.facilityPhoneNumber = facilityPhoneNumber;
        this.organizerId = orgarnizerId;
        savetoDB();
    }

    private void savetoDB(){
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityName", facilityName);
        facilityData.put("facilityAddress", facilityAddress);
        facilityData.put("facilityEmailAddress", facilityEmailAddress);
        facilityData.put("facilityPhoneNumber", facilityPhoneNumber);
        facilityData.put("organizerId", organizerId);

        facilities.add(facilityData)
                .addOnSuccessListener(documentReference -> {
                    facilityId = documentReference.getId();
                })
                .addOnFailureListener(e -> {
                    Log.e("DB Storage", "Failed to store to database");
                });
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getFacilityAddress() {
        return facilityAddress;
    }

    public String getFacilityEmailAddress() {
        return facilityEmailAddress;
    }

    public int getFacilityPhoneNumber() {
        return facilityPhoneNumber;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setFacilityAddress(String facilityAddress) {
        this.facilityAddress = facilityAddress;
    }

    public void setFacilityEmailAddress(String facilityEmailAddress) {
        this.facilityEmailAddress = facilityEmailAddress;
    }

    public void setFacilityPhoneNumber(int facilityPhoneNumber) {
        this.facilityPhoneNumber = facilityPhoneNumber;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
