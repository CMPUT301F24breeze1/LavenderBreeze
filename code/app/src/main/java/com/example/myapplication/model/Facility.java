package com.example.myapplication.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.util.Log;


import com.example.myapplication.controller.DeviceUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The Facility class represents a facility in the application with properties such as name, address,
 * email, phone number, organizer Id, facility picture, and list of events which are planned at the facility.
 * This class manages facility data persistence in Firebase Firestore and provides methods to interact with
 * facility-related data.
 */
public class Facility{
    private String facilityName;
    private String facilityAddress;
    private String facilityEmail;
    private String facilityPhoneNumber;
    private String organizerId;
    private List<String> events;
    private String facilityId;
    private FirebaseFirestore db;
    private CollectionReference facilities;

    private String profileImageUrl;
    /**
     * Constructs a facility with all default values
     */
    public Facility() {
        this.facilityName = "Default Name";
        this.facilityAddress = "Default Address";
        this.facilityEmail = "email@default.com";
        this.facilityPhoneNumber = "123456789";
    }
    /**
     * @param context context from which to get the device ID
     *
     * Constructs a facility using the deviceId form the given context
     */
    public Facility(Context context) {
        this.facilityName = "Default Name";
        this.facilityAddress = "Default Address";
        this.facilityEmail = "email@default.com";
        this.facilityPhoneNumber = "123456789";
        this.organizerId = DeviceUtils.getDeviceId(context);

        initializeFirestore();
    }
    /**
     * @param facilityName Facility name to be set
     * @param facilityAddress Facility Address to be set
     * @param facilityEmail Facility Email to be set
     * @param facilityPhoneNumber Facility Phone Number to be set
     * @param organizerId Facility Organizer Id to be set
     *
     * Constructs a facility with a specified Name, Address, Email, Phone Number,
     * and Organizer ID
     */
    public Facility(String facilityName, String facilityAddress, String facilityEmail,
                    String facilityPhoneNumber, String organizerId) {
        this.facilityName = facilityName;
        this.facilityAddress = facilityAddress;
        this.facilityEmail = facilityEmail;
        this.facilityPhoneNumber = facilityPhoneNumber;
        this.organizerId = organizerId;

        initializeFirestore();
    }
    /**
     * @param facilityName Facility name to be set
     * @param facilityAddress Facility Address to be set
     * @param facilityEmail Facility Email to be set
     * @param facilityPhoneNumber Facility Phone Number to be set
     * @param organizerId Facility Organizer Id to be set
     * @param profileImageUrl Facility Profile Picture URL to be set
     *
     * Constructs a facility with a specified Name, Address, Email, Phone Number, Organizer ID
     * and Profile Picture
     */
    public Facility(String facilityName, String facilityAddress, String facilityEmail, String facilityPhoneNumber, String organizerId, String profileImageUrl) {
        this.facilityName = facilityName;
        this.facilityAddress = facilityAddress;
        this.facilityEmail = facilityEmail;
        this.facilityPhoneNumber = facilityPhoneNumber;
        this.organizerId = organizerId;
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * Initializes the firestore database and gets a collection reference for the facilities collection
     */
    private void initializeFirestore() {
        db = FirebaseFirestore.getInstance();
        facilities = db.collection("facilities");
    }

//    // Save facility to Firestore only if it doesn't already exist
//    public void saveToFirestoreIfNotExists() {
//        if (facilities != null) {
//            // Check if a facility with the same name already exists
//            facilities.whereEqualTo("facilityName", facilityName).get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful() && task.getResult() != null && task.getResult().isEmpty()) {
//                            // Facility does not exist, so add it to Firestore
//                            saveToFirestore();
//                        } else {
//                            Log.d("Firestore", "Facility already exists, skipping save.");
//                        }
//                    });
//        } else {
//            Log.e("Firestore Error", "Firestore database is not initialized.");
//        }
//    }

    /**
     * Saves the facility to firestore
     */
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
    /**
     * Returns the Facility Name
     * @return facilityName
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * @param facilityName Facility Name to be set
     *
     * Sets the Facility Name
     */

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    /**
     * Returns the Facility Address
     * @return facilityAddress
     */
    public String getFacilityAddress() {
        return facilityAddress;
    }

    /**
     * @param facilityAddress Facility Address to be set
     *
     * Sets the Facility Address
     */
    public void setFacilityAddress(String facilityAddress) {
        this.facilityAddress = facilityAddress;
    }

    /**
     * Returns the Facility Email
     * @return facilityEmail
     */
    public String getFacilityEmail() {
        return facilityEmail;
    }

    /**
     * @param facilityEmail Facility Email to be set
     *
     * Sets the Facility Email
     */
    public void setFacilityEmail(String facilityEmail) {
        this.facilityEmail = facilityEmail;
    }

    /**
     * Returns the Facility Phone Number
     * @return facilityPhonenumber
     */
    public String getFacilityPhoneNumber() {
        return facilityPhoneNumber;
    }

    /**
     * @param facilityPhoneNumber Facility Phone Number to be set
     *
     * Sets the Facility Phone Number
     */
    public void setFacilityPhoneNumber(String facilityPhoneNumber) {
        this.facilityPhoneNumber = facilityPhoneNumber;
    }

    /**
     * Returns the Organizer ID
     * @return organizerId
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * @param organizerId Facility Organizer ID to be set
     *
     * Sets the Organizer ID
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /**
     * Returns the List of Events at the Facility
     * @return events
     */
    public List<String> getEvents() {
        return events;
    }

    /**
     * @param events List of facility events to be set
     *
     * Sets the list of events at the Facility
     */
    public void setEvents(List<String> events) {
        this.events = events;
    }

    /**
     * Returns the Facility ID
     * @return facilityId
     */
    public String getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId Facility ID to be set
     *
     * Sets the Facility ID
     */
    public void setFacilityId(String facilityId){
        this.facilityId = facilityId;
    }

    /**
     * Returns the Facility Profile Picture
     * @return profileImageUrl
     */
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    /**
     * @param profileImageUrl Facility Profile Picture URL to be set
     *
     * Sets the Facility Profile Picture
     */
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
