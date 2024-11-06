package com.example.myapplication.controller;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

// NotificationSender.java
public class NotificationSender {
    private FirebaseFirestore db;

    public NotificationSender() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Send notification to a device
     */
    public void sendNotification(String deviceId, String title, String message,
                                 OnNotificationSentListener listener) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("msg", message);
        notificationData.put("timestamp", System.currentTimeMillis());

        db.collection("users")
                .document(deviceId)
                .set(notificationData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    public interface OnNotificationSentListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}