// From chatgpt, openai, "write a java implementation of User Class", 2024-10-25
package com.example.myapplication;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String email;
    private String phoneNumber;
    private Boolean isEntrant;
    private Boolean isOrganizer;
    private Boolean isAdmin;
    private Boolean isFacility;
    private String deviceID;

    private FirebaseFirestore database;
    private CollectionReference users ;

    // Constructor
    public User(Context context) {
        // Extract the device ID
        this.deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Device ID", "Android ID: " + deviceID);
        this.database = FirebaseFirestore.getInstance();
        this.users = database.collection("users");

        // Check if user exists
        users.document(deviceID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, pull data
                        loadUserData(document);
                    } else {
                        // Document doesn't exist, create new user
                        createNewUser();
                    }
                }
            }
        });
    }

    // Load user data from Firestore
    private void loadUserData(DocumentSnapshot document) {
        this.name = document.getString("name");
        this.email = document.getString("email");
        this.phoneNumber = document.getString("phoneNumber");
        this.isEntrant = document.getBoolean("isEntrant");
        this.isOrganizer = document.getBoolean("isOrganizer");
        this.isAdmin = document.getBoolean("isAdmin");
        this.isFacility = document.getBoolean("isFacility");
    }

    // Create new user with default values
    private void createNewUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "Default Name");
        userData.put("email", "default@example.com");
        userData.put("phoneNumber", "0000000000");
        userData.put("isEntrant", false);
        userData.put("isOrganizer", false);
        userData.put("isAdmin", false);
        userData.put("isFacility", false);

        users.document(deviceID).set(userData);
        // Set local attributes to default values
        this.name = "Default Name";
        this.email = "default@example.com";
        this.phoneNumber = "0000000000";
        this.isEntrant = false;
        this.isOrganizer = false;
        this.isAdmin = false;
        this.isFacility = false;
    }

    // Getters that return local values

    /**
     * Get the device ID
     * @return the device ID
     */
    public String getName() {
        return name;
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

    // Setters with validation

    /**
     * Set the name
     * @param name
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name;
        users.document(deviceID).update("name", name);
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        this.email = email;
        users.document(deviceID).update("email", email);
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be a 10-digit number.");
        }
        this.phoneNumber = phoneNumber;
        users.document(deviceID).update("phoneNumber", phoneNumber);
    }

    public void setIsEntrant(Boolean isEntrant) {
        if (isEntrant == null) {
            throw new IllegalArgumentException("isEntrant cannot be null.");
        }
        this.isEntrant = isEntrant;
        users.document(deviceID).update("isEntrant", isEntrant);
    }

    public void setIsOrganizer(Boolean isOrganizer) {
        if (isOrganizer == null) {
            throw new IllegalArgumentException("isOrganizer cannot be null.");
        }
        this.isOrganizer = isOrganizer;
        users.document(deviceID).update("isOrganizer", isOrganizer);
    }

    public void setIsAdmin(Boolean isAdmin) {
        if (isAdmin == null) {
            throw new IllegalArgumentException("isAdmin cannot be null.");
        }
        this.isAdmin = isAdmin;
        users.document(deviceID).update("isAdmin", isAdmin);
    }

    public void setIsFacility(Boolean isFacility) {
        if (isFacility == null) {
            throw new IllegalArgumentException("isFacility cannot be null.");
        }
        this.isFacility = isFacility;
        users.document(deviceID).update("isFacility", isFacility);
    }
}
