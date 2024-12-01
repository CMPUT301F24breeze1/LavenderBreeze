package com.example.myapplication.controller;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class for managing notifications and sending them to multiple devices.
 * This class interacts with Firebase Firestore to store notification data for specific users.
 */
public class NotificationHelper {

    private FirebaseFirestore db;
    public NotificationHelper() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Sends a notification to multiple devices.
     *
     * @param deviceIds A list of device IDs representing the recipients of the notification.
     * @param title     The title of the notification.
     * @param message   The message content of the notification.
     *
     * The method creates a map of notification data, including the title, message, and timestamp,
     * and stores it in the Firestore database for each recipient.
     */
    public void sendNotification(List<String> deviceIds, String title, String message) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("msg", message);
        notificationData.put("timestamp", System.currentTimeMillis());

        for (String deviceId : deviceIds) {
            db.collection("users")
                    .document(deviceId)
                    .set(notificationData, SetOptions.merge());
        }
    }
}
