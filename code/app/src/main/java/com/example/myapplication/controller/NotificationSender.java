package com.example.myapplication.controller;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// NotificationSender.java
public class NotificationSender {
    private FirebaseFirestore db;

    public NotificationSender() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Send notification to multiple devices
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
