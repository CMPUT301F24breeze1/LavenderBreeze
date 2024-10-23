package com.example.myapplication;

import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseHelper {
    private static FirebaseFirestore databaseInstance;

    // Private constructor to enforce Singleton pattern
    private FireBaseHelper() {}

    public static FirebaseFirestore getFirestoreInstance() {
        if (databaseInstance == null) {
            databaseInstance = FirebaseFirestore.getInstance();
        }
        return databaseInstance;
    }
}
