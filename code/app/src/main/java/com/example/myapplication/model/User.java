// From chatgpt, openai, "write a java implementation of User Class", 2024-10-25
package com.example.myapplication.model;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements java.io.Serializable {
    public interface OnUserDataLoadedListener {
        void onUserDataLoaded();
    }
    private String name;
    private String email;
    private String phoneNumber;
    private Boolean isEntrant;
    private Boolean isOrganizer;
    private Boolean isAdmin;
    private Boolean isFacility;
    private String deviceID;
    private String profilePicture;
    private List<String> requestedEvents;
    private List<String> selectedEvents;
    private List<String> cancelledEvents;
    private List<String> acceptedEvents;
    private static final long serialVersionUID = 1L;
    private String userID;

    private FirebaseFirestore database;
    private CollectionReference users ;

    /**
     * Constructor of the user class
     * @param context
     */
    // Constructor
    public User(Context context, OnUserDataLoadedListener listener) {
        // Extract the device ID
        this.deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Device ID", "Android ID: " + deviceID);
        database = FirebaseFirestore.getInstance();
        users = database.collection("users");

        // Check if user exists
        users.document(deviceID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        // Document exists, pull data
                        loadUserData(document,listener);
                    } else {
                        // Document doesn't exist, create new user
                        createNewUser();
                    }
                }
            }
        });
    }

    // adding this so to successfully retrieve userID
    // Constructor with callback
//    public User(Context context, UserIDCallback callback) {
//        // Extract the device ID
//        this.deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        Log.d("Device ID", "Android ID: " + deviceID);
//        this.database = FirebaseFirestore.getInstance();
//        this.users = database.collection("users");
//
//        // Check if user exists
//        users.document(deviceID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document != null && document.exists()) {
//                        // Document exists, pull data
//                        loadUser(document);
//                        userID = document.getId(); // Set the user ID from the document
//                        Log.d("User ID", "User ID: " + userID);
//                        callback.onUserIDLoaded(userID); // Notify that user ID is loaded
//                    } else {
//                        // Document doesn't exist, create new user
//                        createNewUser();
//                        callback.onNewUserCreated(); // Notify that a new user was created
//                    }
//                } else {
//                    Log.e("FirestoreError", "Error getting user document: ", task.getException());
//                }
//            }
//        });
//    }

    /**
     * Load user data from Firestore
     * @param document
     */
    // Load user data from Firestore
    private void loadUserData(DocumentSnapshot document,OnUserDataLoadedListener listener) {
        name = document.getString("name");
        email = document.getString("email");
        phoneNumber = document.getString("phoneNumber");
        isEntrant = document.getBoolean("isEntrant");
        isOrganizer = document.getBoolean("isOrganizer");
        isAdmin = document.getBoolean("isAdmin");
        isFacility = document.getBoolean("isFacility");
        requestedEvents = (List<String>) document.get("requestedEvents");
        selectedEvents = (List<String>) document.get("selectedEvents");
        cancelledEvents = (List<String>) document.get("cancelledEvents");
        acceptedEvents = (List<String>) document.get("acceptedEvents");
        //profilePicture = document.getString("profilePicture");
        if (listener != null) listener.onUserDataLoaded();

//                        profilePicture = document.getString("profilePicture");
    }
    private void loadUser(DocumentSnapshot document){
        name = document.getString("name");
        email = document.getString("email");
        phoneNumber = document.getString("phoneNumber");
        isEntrant = document.getBoolean("isEntrant");
        isOrganizer = document.getBoolean("isOrganizer");
        isAdmin = document.getBoolean("isAdmin");
        isFacility = document.getBoolean("isFacility");
    }

    /**
     * Create new user with default values
     */
    // Create new user with default values
    private void createNewUser() {
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", "Default Name");
        userData.put("email", "default@example.com");
        userData.put("phoneNumber", "0000000000");
        userData.put("isEntrant", false);
        userData.put("isOrganizer", false);
        userData.put("isAdmin", false);
        userData.put("isFacility", false);
        userData.put("profilePicture", "");
        userData.put("requestedEvents", new ArrayList<>());
        userData.put("selectedEvents", new ArrayList<>());
        userData.put("cancelledEvents", new ArrayList<>());
        userData.put("acceptedEvents", new ArrayList<>());

        users.document(deviceID).set(userData).addOnSuccessListener(aVoid -> {
            Log.d("User", "DocumentSnapshot successfully written!");
        }).addOnFailureListener(e -> {
            Log.w("User", "Error writing document", e);
        });
        // Set local attributes to default values
        this.name = "Default Name";
        this.email = "default@example.com";
        this.phoneNumber = "0000000000";
        this.isEntrant = false;
        this.isOrganizer = false;
        this.isAdmin = false;
        this.isFacility = false;
        this.profilePicture = "";
        this.requestedEvents = new ArrayList<>();
        this.selectedEvents = new ArrayList<>();
        this.cancelledEvents = new ArrayList<>();
        this.acceptedEvents = new ArrayList<>();
    }

    // Getters that return local values

    /**
     * Get the device ID
     * @return the device ID
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the phone number
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Get the isEntrant
     * @return the isEntrant
     */
    public Boolean getIsEntrant() {
        return isEntrant;
    }

    /**
     * Get the isOrganizer
     * @return the isOrganizer
     */
    public Boolean getIsOrganizer() {
        return isOrganizer;
    }

    /**
     * Get the isAdmin
     * @return the isAdmin
     */
    public Boolean getIsAdmin() {
        return isAdmin;
    }

    /**
     * Get the isFacility
     * @return the isFacility
     */
    public Boolean getIsFacility() {
        return isFacility;
    }

    public List<String> getRequestedEvents() {
        return requestedEvents;
    }

    public List<String> getSelectedEvents() {
        return selectedEvents;
    }

    public List<String> getCancelledEvents() {
        return cancelledEvents;
    }

    public List<String> getAcceptedEvents() {
        return acceptedEvents;
    }

    // Setters with validation

    /**
     * Set the name
     * @param name
     * @throws IllegalArgumentException if name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name;
        users.document(deviceID).update("name", name);
    }

    /**
     * Set the email
     * @param email
     * @throws IllegalArgumentException if email is null or does not contain an "@"
     */
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        this.email = email;
        users.document(deviceID).update("email", email);
    }

    /**
     * Set the phone number
     * @param phoneNumber
     * @throws IllegalArgumentException if phoneNumber is null or does not contain 10 digits
     */
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be a 10-digit number.");
        }
        this.phoneNumber = phoneNumber;
        users.document(deviceID).update("phoneNumber", phoneNumber);
    }

    /**
     * Set the isEntrant
     * @param isEntrant
     * @throws IllegalArgumentException if isEntrant is null
     */
    public void setIsEntrant(Boolean isEntrant) {
        if (isEntrant == null) {
            throw new IllegalArgumentException("isEntrant cannot be null.");
        }
        this.isEntrant = isEntrant;
        users.document(deviceID).update("isEntrant", isEntrant);
    }

    /**
     * Set the isOrganizer
     * @param isOrganizer
     * @throws IllegalArgumentException if isOrganizer is null
     */
    public void setIsOrganizer(Boolean isOrganizer) {
        if (isOrganizer == null) {
            throw new IllegalArgumentException("isOrganizer cannot be null.");
        }
        this.isOrganizer = isOrganizer;
        users.document(deviceID).update("isOrganizer", isOrganizer);
    }

    /**
     * Set the isAdmin
     * @param isAdmin
     * @throws IllegalArgumentException if isAdmin is null
     */
    public void setIsAdmin(Boolean isAdmin) {
        if (isAdmin == null) {
            throw new IllegalArgumentException("isAdmin cannot be null.");
        }
        this.isAdmin = isAdmin;
        users.document(deviceID).update("isAdmin", isAdmin);
    }

    /**
     * Set the isFacility
     * @param isFacility
     * @throws IllegalArgumentException if isFacility is null
     */
    public void setIsFacility(Boolean isFacility) {
        if (isFacility == null) {
            throw new IllegalArgumentException("isFacility cannot be null.");
        }
        this.isFacility = isFacility;
        users.document(deviceID).update("isFacility", isFacility);
    }
    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    // Add and remove methods for requestedEvents
    public void addRequestedEvent(String eventId) {
        addEventToFirestoreList(eventId, "requestedEvents", requestedEvents);
    }

    public void removeRequestedEvent(String eventId) {
        Log.d("User", "Removing event with ID: " + eventId);
        Log.d("User", "Current requestedEvents: " + requestedEvents);
        removeEventFromFirestoreList(eventId, "requestedEvents", requestedEvents);
    }

    // Add and remove methods for selectedEvents
    public void addSelectedEvent(String eventId) {
        addEventToFirestoreList(eventId, "selectedEvents", selectedEvents);
    }

    public void removeSelectedEvent(String eventId) {
        removeEventFromFirestoreList(eventId, "selectedEvents", selectedEvents);
    }

    // Add and remove methods for cancelledEvents
    public void addCancelledEvent(String eventId) {
        addEventToFirestoreList(eventId, "cancelledEvents", cancelledEvents);
    }

    public void removeCancelledEvent(String eventId) {
        removeEventFromFirestoreList(eventId, "cancelledEvents", cancelledEvents);
    }

    // Add and remove methods for acceptedEvents
    public void addAcceptedEvent(String eventId) {
        addEventToFirestoreList(eventId, "acceptedEvents", acceptedEvents);
    }

    public void removeAcceptedEvent(String eventId) {
        removeEventFromFirestoreList(eventId, "acceptedEvents", acceptedEvents);
    }

    // Helper method to add an event to a list and update Firestore
    private void addEventToFirestoreList(String eventId, String firestoreField, List<String> eventList) {
        if (eventList == null) {
            eventList = new ArrayList<>();
        }
        if (!eventList.contains(eventId)) {
            eventList.add(eventId);
            database.collection("users").document(deviceID)
                    .update(firestoreField, eventList)
                    .addOnSuccessListener(aVoid -> Log.d("User", "Event added to " + firestoreField))
                    .addOnFailureListener(e -> Log.e("User", "Error adding event to " + firestoreField, e));
        }
    }

    // Helper method to remove an event from a list and update Firestore
    private void removeEventFromFirestoreList(String eventId, String firestoreField, List<String> eventList) {
        Log.d("User", "Event removed from the list 1" + eventList);
        if (eventList != null && eventList.contains(eventId)) {
            eventList.remove(eventId);
            Log.d("User", "Event removed from the list 2" + eventList);
            database.collection("users").document(deviceID)
                    .update(firestoreField, eventList)
                    .addOnSuccessListener(aVoid -> Log.d("User", "Event removed from " + firestoreField))
                    .addOnFailureListener(e -> Log.e("User", "Error removing event from " + firestoreField, e));
        }
    }
}
