package com.example.myapplication;

import android.content.Context;
import android.provider.Settings;

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

    private FirebaseFirestore database = FireBaseHelper.getFirestoreInstance();
    private CollectionReference users = database.collection("users");

    // Constructor
    public User(Context context) {
        // Extract the device ID
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

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
        userData.put("deviceID", deviceID);

        users.document(deviceID).set(userData);
    }

    // Getter and Setter for 'name'
    public void setName(String name) {
        this.name = name;
        // Update Firebase
        users.document(deviceID).update("name", name);
    }

    public void getName(final FirestoreCallback callback) {
        users.document(deviceID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        callback.onCallback(name);  // Pass the value to the callback
                    } else {
                        callback.onCallback(null);
                    }
                }
            }
        });
    }

    // Getter and Setter for 'email'
    public void setEmail(String email) {
        this.email = email;
        // Update Firebase
        users.document(deviceID).update("email", email);
    }

    public void getEmail(final FirestoreCallback callback) {
        users.document(deviceID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String email = document.getString("email");
                        callback.onCallback(email);  // Pass the value to the callback
                    } else {
                        callback.onCallback(null);
                    }
                }
            }
        });
    }

    // Similarly, you can create getter and setter for other fields like 'phoneNumber', 'isEntrant', etc.

    // Interface for Firestore callback to handle async get operations
    public interface FirestoreCallback {
        void onCallback(String value);
    }
}
