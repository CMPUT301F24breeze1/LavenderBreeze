// From chatgpt, openai, "write a java implementation with java documentation of User Class
// that contains the user data like name, email, phone number, device id
// requested events, selected events, cancelled events, and accepted events", 2024-10-25
package com.example.myapplication.model;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.example.myapplication.R;
import com.example.myapplication.controller.DeviceUtils;
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
/**
 * Purpose: The User class represents a user in the application
 * Design rationale: The User class represents a user in the application with properties such as name, email,
 * phone number, roles, profile picture, and lists of events they are associated with. This class
 * manages user data persistence in Firebase Firestore and provides methods to interact with user-related data.
 * Outstanding issues: None
 */
public class User implements java.io.Serializable {
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
    /**
     * Interface for callback when profile picture URL is fetched.
     */
    public interface OnFetchCompleteListener {
        /**
         * Callback method when profile picture URL is fetched.
         * @param url
         */
        void onFetchComplete(String url);
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
    private boolean toggleNotif;
    private List<String> requestedEvents=new ArrayList<>();
    private List<String> selectedEvents=new ArrayList<>();
    private List<String> cancelledEvents=new ArrayList<>();
    private List<String> acceptedEvents=new ArrayList<>();
    private static final long serialVersionUID = 1L;
    private Boolean deterministicPicture= true;
    private String userID;

    private Long timestamp;
    private String msg;
    private String title;

    private transient FirebaseFirestore database;
    private transient CollectionReference users;

    /**
     * Constructs a User object with a specified deviceID.
     * @param deviceID
     * @param listener
     */
    // Constructor for creating a User with a specified deviceID
    public User(String deviceID, OnUserLoadedListener listener) {
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
    /**
     * Constructs a User object and initializes data from Firestore if it exists, or creates
     * a new user record if none is found.
     * @param context the context from which the user is created
     * @param listener a callback interface for when user data is loaded
     */
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
                        createNewUser(listener);
                    }
                }
            }
        });
    }


    /**
     * Loads user data from a Firestore document.
     * @param document Firestore document containing user data
     * @param listener callback for when data is loaded
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
        toggleNotif = document.getBoolean("toggleNotif");
        profilePicture = document.getString("profilePicture");
        deterministicPicture = document.getBoolean("deterministicPicture");

        if (listener != null) {
            listener.onUserDataLoaded();
        }
    }

    /**
     * Loads user data from a Firestore document.
     * @param document
     * @param listener
     */
    private void UserData(DocumentSnapshot document,OnUserLoadedListener listener) {
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
        profilePicture = document.getString("profilePicture");
        toggleNotif = document.getBoolean("toggleNotif");
        deterministicPicture = document.getBoolean("deterministicPicture");

        if (listener != null) {
            listener.onUserLoaded(this);
        }
    }

    /**
     * Loads user data from a Firestore document.
     * @param document
     */
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
     * Update the notification toggle in the database
     * @param isEnabled Boolean to toggle notification
     * @param deviceID Device ID of the user
     */
    public void updateToggleNotifInDatabase(boolean isEnabled, String deviceID) {
        toggleNotif = isEnabled;

        database.collection("users").document(deviceID)
                .update("toggleNotif", isEnabled)
                .addOnSuccessListener(aVoid -> Log.d("User", "Notification toggle updated successfully."))
                .addOnFailureListener(e -> Log.e("User", "Failed to update notification toggle.", e));
    }

    /**
     * Create new user with default values
     */
    // Create new user with default values
    private void createNewUser(OnUserDataLoadedListener listener) {
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
        userData.put("toggleNotif", true);
        userData.put("deterministicPicture", true);

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
        this.toggleNotif = true;
        this.deterministicPicture = true;
        if (listener != null) {
            listener.onUserDataLoaded();
        }
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

    /**
     * Get the requested events
     * @return the requested events
     */
    public List<String> getRequestedEvents() {
        return requestedEvents;
    }

    /**
     * Get the selected events
     * @return the selected events
     */
    public List<String> getSelectedEvents() {
        return selectedEvents;
    }

    /**
     * Get the cancelled events
     * @return the cancelled events
     */
    public List<String> getCancelledEvents() {
        return cancelledEvents;
    }

    /**
     * Get the accepted events
     * @return the accepted events
     */
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
     * Get the device ID
     * @return the device ID
     */
    public String getDeviceID() {
        return deviceID;
    }
    /**
     * Get the deterministic picture
     * @return the deterministic picture
     */
    public Boolean getDeterministicPicture() {
        return deterministicPicture;
    }

    /**
     * Set the deterministic picture
     * @param deterministicPicture
     */
    public void setDeterministicPicture(Boolean deterministicPicture) {
        this.deterministicPicture = deterministicPicture;
        users.document(deviceID).update("deterministicPicture", deterministicPicture);
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

    /**
     * Get the profile picture
     * @return the profile picture
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * Set the profile picture
     * @param profilePicture
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Add method for requestedEvents
     * @param eventId
     */
    public void addRequestedEvent(String eventId) {
        addEventToFirestoreList(eventId, "requestedEvents", requestedEvents);
    }

    /**
     * Remove method for requestedEvents
     * @param eventId
     */
    public void removeRequestedEvent(String eventId) {
        Log.d("User", "Removing event with ID: " + eventId);
        Log.d("User", "Current requestedEvents: " + requestedEvents);
        removeEventFromFirestoreList(eventId, "requestedEvents", requestedEvents);
    }

    /**
     * Add method for selectedEvents
     * @param eventId
     */
    public void addSelectedEvent(String eventId) {
        addEventToFirestoreList(eventId, "selectedEvents", selectedEvents);
    }

    /**
     * Remove method for selectedEvents
     * @param eventId
     */
    public void removeSelectedEvent(String eventId) {
        removeEventFromFirestoreList(eventId, "selectedEvents", selectedEvents);
    }

    /**
     * Add method for cancelledEvents
     * @param eventId
     */
    public void addCancelledEvent(String eventId) {
        addEventToFirestoreList(eventId, "cancelledEvents", cancelledEvents);
    }

    /**
     * Remove method for cancelledEvents
     * @param eventId
     */
    public void removeCancelledEvent(String eventId) {
        removeEventFromFirestoreList(eventId, "cancelledEvents", cancelledEvents);
    }

    /**
     * Add method for acceptedEvents
     * @param eventId
     */
    public void addAcceptedEvent(String eventId) {
        addEventToFirestoreList(eventId, "acceptedEvents", acceptedEvents);
    }

    /**
     * Remove method for acceptedEvents
     * @param eventId
     */
    public void removeAcceptedEvent(String eventId) {
        removeEventFromFirestoreList(eventId, "acceptedEvents", acceptedEvents);
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

    /**
     * Update the profile picture in the database
     */
    public void updateProfilePictureInDatabase() {
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
    public void loadProfilePictureInto(ImageView imageView,Context context) {
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
    /**
     * Load the deterministic profile picture into an ImageView
     * @param imageView Takes the ImageView as a parameter
     * @param context   Takes the Context as a parameter
     */
    public void loadDeterministicProfilePictureInto(ImageView imageView,Context context){
        char firstLetter = this.name.toUpperCase().charAt(0);
        fetchDefaultProfilePictureUrl(firstLetter, url -> {
            if (url != null) {
                int startIndex = url.indexOf("/file/d/") + "/file/d/".length();
                int endIndex = url.indexOf("/view");
                if (endIndex != -1) {
                    String fileId = url.substring(startIndex, endIndex);
                    String directUrl = "https://drive.google.com/uc?export=view&id="+fileId;

                    Glide.with(context)
                            .load(directUrl)
                            .transform(new CircleCrop())            // Make image circular
                            .into(imageView);
                }
            } else {
                Toast.makeText(context, "Failed to generate profile picture.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fetch the default profile picture URL for a given first letter
     * @param firstLetter Takes the first letter of the name as a parameter
     * @param callback  Takes the OnFetchCompleteListener as a parameter
     */
    private void fetchDefaultProfilePictureUrl(char firstLetter, OnFetchCompleteListener callback) {
        String letter = String.valueOf(firstLetter).toUpperCase();

        database.collection("defaultProfilePictures").document("letters")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String url = documentSnapshot.getString(letter);
                        callback.onFetchComplete(url);
                    } else {
                        callback.onFetchComplete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFetchComplete(null);
                });
    }

    /**
     * Get the notification toggle
     * @return the notification toggle
     */
    public boolean isToggleNotif() {
        return toggleNotif;
    }

    /**
     * Set the notification toggle
     * @param toggleNotif the notification toggle
     */
    public void setToggleNotif(boolean toggleNotif) {
        this.toggleNotif = toggleNotif;
    }
}
