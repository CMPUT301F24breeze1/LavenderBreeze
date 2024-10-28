package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    private String eventId;
    private String eventName;
    private String eventDescription;
    private Date eventStart;
    private Date eventEnd;
    private Date registrationStart;
    private Date registrationEnd;
    private String location;
    private int capacity;
    private int price;
    private String posterUrl;
    private String qrCodeHash;
    private List<String> waitlist;
    private List<String> selectedEntrants;
    private String organizerId;

    private FirebaseFirestore database;
    private CollectionReference events;

    // Constructor retrieves data for an existing event using eventId
    public Event(String eventId) {
        this.eventId = eventId;
        this.database = FirebaseFirestore.getInstance();
        this.events = database.collection("events");

        loadEventData();
    }

    // Constructors for creating a new Event
    public Event(String eventName, String eventDescription, Date eventStart, Date eventEnd,
                 Date registrationStart, Date registrationEnd, String location, int capacity, int price,
                 String posterUrl, String qrCodeHash, String organizerId) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
        this.location = location;
        this.capacity = capacity;
        this.price = price;
        this.posterUrl = posterUrl;
        this.qrCodeHash = qrCodeHash;
        this.waitlist = new ArrayList<>();
        this.selectedEntrants = new ArrayList<>();
        this.organizerId = organizerId;
        this.database = FirebaseFirestore.getInstance();
        this.events = database.collection("events");
    }

    // Load event data from Firestore
    private void loadEventData() {
        DocumentReference docRef = events.document(eventId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        populateEventData(document);
                    } else {
                        Log.d("Event", "No such event in Firestore");
                    }
                }
            }
        });
    }

    // Called by loadEventData()
    // Reads fields from the document and sets them to the object's attributes
    private void populateEventData(DocumentSnapshot document) {
        this.eventName = document.getString("eventName");
        this.eventDescription = document.getString("eventDescription");
        this.eventStart = document.getDate("eventStart");
        this.eventEnd = document.getDate("eventEnd");
        this.registrationStart = document.getDate("registrationStart");
        this.registrationEnd = document.getDate("registrationEnd");
        this.location = document.getString("location");
        this.capacity = document.getLong("capacity").intValue();
        this.price = document.getLong("price").intValue();
        this.posterUrl = document.getString("posterUrl");
        this.qrCodeHash = document.getString("qrCodeHash");
        this.organizerId = document.getString("organizerId");
        this.waitlist = (List<String>) document.get("waitlist");
        this.selectedEntrants = (List<String>) document.get("selectedEntrants");
    }

    // Save or update the current state of an Event object to Firestore
    public void saveEvent() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName);
        eventData.put("eventDescription", eventDescription);
        eventData.put("eventStart", eventStart);
        eventData.put("eventEnd", eventEnd);
        eventData.put("registrationStart", registrationStart);
        eventData.put("registrationEnd", registrationEnd);
        eventData.put("location", location);
        eventData.put("capacity", capacity);
        eventData.put("price", price);
        eventData.put("posterUrl", posterUrl);
        eventData.put("qrCodeHash", qrCodeHash);
        eventData.put("organizerId", organizerId);
        eventData.put("waitlist", waitlist);
        eventData.put("selectedEntrants", selectedEntrants);

        if (eventId == null || eventId.isEmpty()) {
            // Create a new event
            events.add(eventData).addOnSuccessListener(documentReference -> {
                eventId = documentReference.getId();  // Get the new event ID
                Log.d("Event", "Event created with ID: " + eventId);
            }).addOnFailureListener(e -> {
                Log.e("Event", "Error creating event", e);
            });
        } else {
            // Update existing event
            events.document(eventId).update(eventData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Event", "Event updated successfully");
                    }).addOnFailureListener(e -> {
                        Log.e("Event", "Error updating event", e);
                    });
        }
    }

    // Getters
    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public Date getEventStart() {
        return eventStart;
    }

    public Date getEventEnd() {
        return eventEnd;
    }

    public Date getRegistrationStart() {
        return registrationStart;
    }

    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPrice() {
        return price;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getQrCodeHash() {
        return qrCodeHash;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public List<String> getWaitlist() {
        return waitlist;
    }

    public List<String> getSelectedEntrants() {
        return selectedEntrants;
    }

    // Setters (updates corresponding field in Firestore)
    public void setEventName(String eventName) {
        this.eventName = eventName;
        events.document(eventId).update("eventName", eventName);
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
        events.document(eventId).update("eventDescription", eventDescription);
    }

    public void setEventStart(Date eventStart) {
        this.eventStart = eventStart;
        events.document(eventId).update("eventStart", eventStart);
    }

    public void setEventEnd(Date eventEnd) {
        this.eventEnd = eventEnd;
        events.document(eventId).update("eventEnd", eventEnd);
    }

    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
        events.document(eventId).update("registrationStart", registrationStart);
    }

    public void setRegistrationEnd(Date registrationEnd) {
        this.registrationEnd = registrationEnd;
        events.document(eventId).update("registrationEnd", registrationEnd);
    }

    public void setLocation(String location) {
        this.location = location;
        events.document(eventId).update("location", location);
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        events.document(eventId).update("capacity", capacity);
    }

    public void setPrice(int price) {
        this.price = price;
        events.document(eventId).update("price", price);
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
        events.document(eventId).update("posterUrl", posterUrl);
    }

    public void setQrCodeHash(String qrCodeHash) {
        this.qrCodeHash = qrCodeHash;
        events.document(eventId).update("qrCodeHash", qrCodeHash);
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
        events.document(eventId).update("organizerId", organizerId);
    }

    public void setWaitlist(List<String> waitlist) {
        this.waitlist = waitlist;
        events.document(eventId).update("waitlist", waitlist);
    }

    public void setSelectedEntrants(List<String> selectedEntrants) {
        this.selectedEntrants = selectedEntrants;
        events.document(eventId).update("selectedEntrants", selectedEntrants);
    }


    /**
     * Add/remove methods for waitlist/selected entrants
     */

    public void addToWaitlist(String userId) {
        if (!waitlist.contains(userId)) {
            waitlist.add(userId);
            events.document(eventId).update("waitlist", waitlist);
        }
    }

    public void removeFromWaitlist(String userId) {
        if (waitlist.contains(userId)) {
            waitlist.remove(userId);
            events.document(eventId).update("waitlist", waitlist);
        }
    }

    public void addToSelectedlist(String userId) {
        if (!selectedEntrants.contains(userId)) {
            selectedEntrants.add(userId);
            events.document(eventId).update("selectedEntrants", selectedEntrants);
        }
    }

    public void removeFromSelectedlist(String userId) {
        if (selectedEntrants.contains(userId)) {
            selectedEntrants.remove(userId);
            events.document(eventId).update("selectedEntrants", selectedEntrants);
        }
    }






}

