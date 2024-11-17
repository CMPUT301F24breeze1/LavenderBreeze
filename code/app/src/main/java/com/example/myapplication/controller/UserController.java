package com.example.myapplication.controller;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.myapplication.model.User;

public class UserController {
    private User user;
    private String deviceID;
    private FirebaseFirestore database;
    private CollectionReference users;
    /**
     * Interface for callback when user data is loaded.
     */
    public interface OnUserDataLoadedListener {
        void onUserDataLoaded();
    }
    /**
     * Interface for callback when user data is loaded.
     */
    public interface OnUserLoadedListener {
        void onUserLoaded(User user); // Dedicated listener for User object
    }

    // Constructor for creating a User with a specified deviceID
    public UserController(String deviceID, OnUserLoadedListener listener) {
        this.deviceID = deviceID;
        database = FirebaseFirestore.getInstance();
        users = database.collection("users");

        // Check if the user document with deviceID exists
        users.document(deviceID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    // Load user data if document exists
                    DocumentSnapshot document = task.getResult();
                    UserData(document, listener);
                } else {
                    listener.onUserLoaded(null);
                    Log.d("User", "User document does not exist for deviceID: " + deviceID);
                }
            }
        });
    }

    public UserController(Context context, OnUserDataLoadedListener listener) {
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



    // Load user data from Firestore
    private void loadUserData(DocumentSnapshot document, OnUserDataLoadedListener listener) {
        this.setUserName(document.getString("name"));
        this.setUserEmail(document.getString("email"));
        this.setUserPhoneNumber(document.getString("phoneNumber"));
        this.setUserIsEntrant(document.getBoolean("isEntrant"));
        this.setUserIsOrganizer(document.getBoolean("isOrganizer"));
        this.setUserIsAdmin(document.getBoolean("isAdmin"));
        this.setUserIsFacility(document.getBoolean("isFacility"));
        this.setUserRequestedEvents((List<String>) document.get("requestedEvents"));
        this.setUserSelectedEvents((List<String>) document.get("selectedEvents"));
        this.setUserCancelledEvents((List<String>) document.get("cancelledEvents"));
        this.setUserAcceptedEvents((List<String>) document.get("acceptedEvents"));
        this.setUserProfilePicture(document.getString("profilePicture"));
        this.setUserDeviceID(deviceID);

        if (listener != null) {
            listener.onUserDataLoaded();
        }
    }

    /**
     * Loads user data from a Firestore document.
     * @param document
     * @param listener
     */
    private void UserData(DocumentSnapshot document, OnUserLoadedListener listener) {
        this.setUserName(document.getString("name"));
        this.setUserEmail(document.getString("email"));
        this.setUserPhoneNumber(document.getString("phoneNumber"));
        this.setUserIsEntrant(document.getBoolean("isEntrant"));
        this.setUserIsOrganizer(document.getBoolean("isOrganizer"));
        this.setUserIsAdmin(document.getBoolean("isAdmin"));
        this.setUserIsFacility(document.getBoolean("isFacility"));
        this.setUserRequestedEvents((List<String>) document.get("requestedEvents"));
        this.setUserSelectedEvents((List<String>) document.get("selectedEvents"));
        this.setUserCancelledEvents((List<String>) document.get("cancelledEvents"));
        this.setUserAcceptedEvents((List<String>) document.get("acceptedEvents"));
        this.setUserProfilePicture(document.getString("profilePicture"));
        this.setUserDeviceID(deviceID);

        if (listener != null) {
            listener.onUserLoaded(user);
        }
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
    }

    // Getters that return local values

    /**
     * Get the device ID
     * @return the device ID
     */
    public String getUserName() {
        return user.getName();
    }

    /**
     * Get the email
     * @return the email
     */
    public String getUserEmail() {
        return user.getEmail();
    }

    /**
     * Get the phone number
     * @return the phone number
     */
    public String getUserPhoneNumber() {
        return user.getPhoneNumber();
    }

    /**
     * Get the isEntrant
     * @return the isEntrant
     */
    public Boolean getUserIsEntrant() {
        return user.getIsEntrant();
    }

    /**
     * Get the isOrganizer
     * @return the isOrganizer
     */
    public Boolean getUserIsOrganizer() {
        return user.getIsOrganizer();
    }

    /**
     * Get the isAdmin
     * @return the isAdmin
     */
    public Boolean getUserIsAdmin() {
        return user.getIsAdmin();
    }

    /**
     * Get the isFacility
     * @return the isFacility
     */
    public Boolean getUserIsFacility() {
        return user.getIsFacility();
    }

    /**
     * Get the requested events
     * @return the requested events
     */
    public List<String> getUserRequestedEvents() {
        return user.getRequestedEvents();
    }

    /**
     * Get the selected events
     * @return the selected events
     */
    public List<String> getUserSelectedEvents() {
        return user.getSelectedEvents();
    }

    /**
     * Get the cancelled events
     * @return the cancelled events
     */
    public List<String> getUserCancelledEvents() {
        return user.getCancelledEvents();
    }

    /**
     * Get the accepted events
     * @return the accepted events
     */
    public List<String> getUserAcceptedEvents() {
        return user.getAcceptedEvents();
    }
    /**
     * Get the device ID
     * @return the device ID
     */
    public String getUserDeviceID() {
        return user.getDeviceID();
    }
    // Setters with validation

    /**
     * Set the name
     * @param name
     * @throws IllegalArgumentException if name is null or empty
     */
    public void setUserName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        user.setName(name);
        users.document(deviceID).update("name", name);
    }

    /**
     * Set the email
     * @param email
     * @throws IllegalArgumentException if email is null or does not contain an "@"
     */
    public void setUserEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        user.setEmail(email);
        users.document(deviceID).update("email", email);
    }

    /**
     * Set the phone number
     * @param phoneNumber
     * @throws IllegalArgumentException if phoneNumber is null or does not contain 10 digits
     */
    public void setUserPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be a 10-digit number.");
        }
        user.setPhoneNumber(phoneNumber);
        users.document(deviceID).update("phoneNumber", phoneNumber);
    }

    /**
     * Set the isEntrant
     * @param isEntrant
     * @throws IllegalArgumentException if isEntrant is null
     */
    public void setUserIsEntrant(Boolean isEntrant) {
        if (isEntrant == null) {
            throw new IllegalArgumentException("isEntrant cannot be null.");
        }
        user.setIsEntrant(isEntrant);
        users.document(deviceID).update("isEntrant", isEntrant);
    }

    /**
     * Set the isOrganizer
     * @param isOrganizer
     * @throws IllegalArgumentException if isOrganizer is null
     */
    public void setUserIsOrganizer(Boolean isOrganizer) {
        if (isOrganizer == null) {
            throw new IllegalArgumentException("isOrganizer cannot be null.");
        }
        user.setIsOrganizer(isOrganizer);
        users.document(deviceID).update("isOrganizer", isOrganizer);
    }

    /**
     * Set the isAdmin
     * @param isAdmin
     * @throws IllegalArgumentException if isAdmin is null
     */
    public void setUserIsAdmin(Boolean isAdmin) {
        if (isAdmin == null) {
            throw new IllegalArgumentException("isAdmin cannot be null.");
        }
        user.setIsAdmin(isAdmin);
        users.document(deviceID).update("isAdmin", isAdmin);
    }

    /**
     * Set the isFacility
     * @param isFacility
     * @throws IllegalArgumentException if isFacility is null
     */
    public void setUserIsFacility(Boolean isFacility) {
        if (isFacility == null) {
            throw new IllegalArgumentException("isFacility cannot be null.");
        }
        user.setIsFacility(isFacility);
        users.document(deviceID).update("isFacility", isFacility);
    }

    /**
     * Get the profile picture
     * @return the profile picture
     */
    public String getUserProfilePicture() {
        return user.getProfilePicture();
    }

    /**
     * Set the profile picture
     * @param profilePicture
     */
    public void setUserProfilePicture(String profilePicture) {
        user.setProfilePicture(profilePicture);
    }
    public void setUserDeviceID(String deviceID) { this.deviceID = deviceID; }
    public void setUserRequestedEvents(List<String> requestedEvents) {
        user.setRequestedEvents(requestedEvents);
    }

    public void setUserSelectedEvents(List<String> selectedEvents) {
        user.setSelectedEvents(selectedEvents);
    }

    public void setUserCancelledEvents(List<String> cancelledEvents) {
        user.setCancelledEvents(cancelledEvents);
    }

    public void setUserAcceptedEvents(List<String> acceptedEvents) {
        user.setAcceptedEvents(acceptedEvents);
    }
    /**
     * Add method for requestedEvents
     * @param eventId
     */
    public void addRequestedEvent(String eventId) {
        List<String> requestedEvents = this.getUserRequestedEvents();
        addEventToFirestoreList(eventId, "requestedEvents", requestedEvents);
        if (requestedEvents == null) {
            requestedEvents = new ArrayList<>();
        }
        if (!requestedEvents.contains(eventId)) {
            Boolean result=requestedEvents.add(eventId);
            if(result){ this.setUserRequestedEvents(requestedEvents);}
        }
    }

    /**
     * Remove method for requestedEvents
     * @param eventId
     */
    public void removeRequestedEvent(String eventId) {
        Log.d("User", "Removing event with ID: " + eventId);
        Log.d("User", "Current requestedEvents: " + this.getUserRequestedEvents());
        List<String> requestedEvents = this.getUserRequestedEvents();
        removeEventFromFirestoreList(eventId, "requestedEvents", requestedEvents);
        if (requestedEvents != null && requestedEvents.contains(eventId)) {
            Boolean result=requestedEvents.remove(eventId);
            if(result){ this.setUserRequestedEvents(requestedEvents);}
        }
    }

    /**
     * Add method for selectedEvents
     * @param eventId
     */
    public void addSelectedEvent(String eventId) {
        List<String> selectedEvents = this.getUserSelectedEvents();
        addEventToFirestoreList(eventId, "selectedEvents", selectedEvents);
        if (selectedEvents == null) {
            selectedEvents = new ArrayList<>();
        }
        if (!selectedEvents.contains(eventId)) {
            Boolean result=selectedEvents.add(eventId);
            if(result){ this.setUserSelectedEvents(selectedEvents);}
        }
    }

    /**
     * Remove method for selectedEvents
     * @param eventId
     */
    public void removeSelectedEvent(String eventId) {
        List<String> selectedEvents = this.getUserSelectedEvents();
        removeEventFromFirestoreList(eventId, "selectedEvents",selectedEvents);
        if (selectedEvents != null && selectedEvents.contains(eventId)) {
            Boolean result=selectedEvents.remove(eventId);
            if(result){ this.setUserSelectedEvents(selectedEvents);}
        }
    }

    /**
     * Add method for cancelledEvents
     * @param eventId
     */
    public void addCancelledEvent(String eventId) {
        List<String> cancelledEvents = this.getUserCancelledEvents();
        addEventToFirestoreList(eventId, "cancelledEvents", cancelledEvents);
        if (cancelledEvents == null) {
            cancelledEvents = new ArrayList<>();
        }
        if (!cancelledEvents.contains(eventId)) {
            Boolean result=cancelledEvents.add(eventId);
            if(result){ this.setUserCancelledEvents(cancelledEvents);}
        }
    }

    /**
     * Remove method for cancelledEvents
     * @param eventId
     */
    public void removeCancelledEvent(String eventId) {
        List<String> cancelledEvents = this.getUserCancelledEvents();
        removeEventFromFirestoreList(eventId, "cancelledEvents", cancelledEvents);
        if (cancelledEvents != null && cancelledEvents.contains(eventId)) {
            Boolean result=cancelledEvents.remove(eventId);
            if(result){ this.setUserCancelledEvents(cancelledEvents);}
        }
    }

    /**
     * Add method for acceptedEvents
     * @param eventId
     */
    public void addAcceptedEvent(String eventId) {
        List<String> eventList = this.getUserAcceptedEvents();
        addEventToFirestoreList(eventId, "acceptedEvents", eventList);
        if (eventList == null) {
            eventList = new ArrayList<>();
        }
        if (!eventList.contains(eventId)) {
            Boolean result=eventList.add(eventId);
            if(result){ this.setUserAcceptedEvents(eventList);}
        }
    }

    /**
     * Remove method for acceptedEvents
     * @param eventId
     */
    public void removeAcceptedEvent(String eventId) {
        List<String> acceptedEvents = this.getUserAcceptedEvents();
        removeEventFromFirestoreList(eventId, "acceptedEvents", acceptedEvents );
        if (acceptedEvents  != null && acceptedEvents .contains(eventId)) {
            Boolean result=acceptedEvents.remove(eventId);
            if(result){ this.setUserAcceptedEvents(acceptedEvents);}
        }
    }

    /**
     * Helper method to add an event to a list and update Firestore
     * @param eventId
     * @param firestoreField
     * @param eventList
     */
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

    /**
     * Helper method to remove an event from a list and update Firestore
     * @param eventId
     * @param firestoreField
     * @param eventList
     */
    private void removeEventFromFirestoreList(String eventId, String firestoreField, List<String>eventList) {
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

    /**
     * Update the profile picture in the database
     */
    public void updateProfilePictureInDatabase(String profilePicture) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(deviceID)
                .update("profilePicture", profilePicture)
                .addOnSuccessListener(aVoid -> Log.d("User", "Profile picture updated in Firestore"))
                .addOnFailureListener(e -> Log.e("User", "Error updating profile picture", e));
    }

    /**
     * Load the profile picture into an ImageView
     * @param imageView
     * @param context
     */
    public void loadProfilePictureInto(ImageView imageView, Context context,String profilePicture) {
        if (profilePicture != null && !profilePicture.isEmpty()) {
            Glide.with(imageView.getContext())
                    .load(profilePicture)
                    .placeholder(R.drawable.account_circle) // Placeholder if image is loading
                    .transform(new CircleCrop())            // Make image circular
                    .into(imageView);
        } else {
            // Set a default placeholder image if no profile picture is available
            imageView.setImageResource(R.drawable.account_circle);
        }
    }
}
