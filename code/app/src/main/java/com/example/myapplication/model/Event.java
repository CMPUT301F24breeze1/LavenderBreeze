package com.example.myapplication.model;

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

/**
 * The Event class represents an event object with attributes: Id, name, desciption, start and end date, registration period
 * location, capacity, price, poster url, qrcode, organizer ID, and 4 lists associate with it.
 * This class manages event data persistence with Firestore and provides methods to interact with event-related data.
 */
public class Event implements java.io.Serializable {
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
    private List<String> acceptedEntrants;
    private List<String> declinedEntrants;
    private String organizerId;
    private static final long serialVersionUID = 1L;

    private FirebaseFirestore database;
    private CollectionReference events;

    /**
     * Interface for callback when event data is loaded.
     */
    public interface OnEventDataLoadedListener {
        void onEventDataLoaded(Event loadedEvent);
    }

    /**
     * Constructor retrieves data for an existing event using eventId
     */
    public Event(String eventId) {
        this.eventId = eventId;
        this.database = FirebaseFirestore.getInstance();
        this.events = database.collection("events");

        loadEventData();
    }

    /**
     * Constructs an Event object with a specified eventID.
     * @param eventId
     * @param listener
     */
    public Event(String eventId, Event.OnEventDataLoadedListener listener) {
        this.eventId = eventId;
        database = FirebaseFirestore.getInstance();
        events = database.collection("events");

        // Check if the event document with eventId exists
        events.document(eventId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    // Load event data if document exists
                    DocumentSnapshot document = task.getResult();
                    EventData(document, listener);
                } else {
                    listener.onEventDataLoaded(null);
                    Log.d("User", "Event document does not exist for eventId: " + eventId);
                }
            }
        });
    }

    /**
     * Constructors for creating a new Event
     */
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
        this.acceptedEntrants = new ArrayList<>();
        this.declinedEntrants = new ArrayList<>();
        this.organizerId = organizerId;
        this.database = FirebaseFirestore.getInstance();
        this.events = database.collection("events");
    }
    /**
     * Constructors for creating a new Event with no location parameter
     */
    public Event(String eventName, String eventDescription, Date eventStart, Date eventEnd,
                 Date registrationStart, Date registrationEnd, int capacity, int price,
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
        this.acceptedEntrants = new ArrayList<>();
        this.declinedEntrants = new ArrayList<>();
        this.organizerId = organizerId;
        this.database = FirebaseFirestore.getInstance();
        this.events = database.collection("events");
    }

    /**
     * This loads event data from Firestore.
     * @param document Firestore document containing event data
     * @param listener callback for when data is loaded
     */
    public void EventData(DocumentSnapshot document, Event.OnEventDataLoadedListener listener) {
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
        this.acceptedEntrants = (List<String>) document.get("acceptedEntrants");
        this.declinedEntrants = (List<String>) document.get("declinedEntrants");
        if (listener != null) {
            listener.onEventDataLoaded(this);
        }
    }

    /**
     * This fetches data asynchronously with Firestore
     * @param listener
     */
    public void loadEventDataAsync(OnEventDataLoadedListener listener) {
        database.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Parse the document into an Event object or fields
                        // Populate this event instance
                        listener.onEventDataLoaded(this);  // Pass back the loaded event
                    } else {
                        listener.onEventDataLoaded(null);  // Indicate loading failure
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Event", "Failed to load event data: ", e);
                    listener.onEventDataLoaded(null);
                });
    }

    /**
     * This loads event data from Firestore
     */
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

    /**
     * This reads fields from the document and sets them to the object's attributes
     * Called by loadEventData()
     */
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
        this.acceptedEntrants = (List<String>) document.get("acceptedEntrants");
        this.declinedEntrants = (List<String>) document.get("declinedEntrants");
    }

    /**
     * This saves or updates the current state of an Event object to Firestore
     */
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
        eventData.put("acceptedEntrants", acceptedEntrants);
        eventData.put("declinedEntrants", declinedEntrants);

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

    /**
     * Get the event ID
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Get the event name
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Get the event desciption
     * @return the event description
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * Get the event start date
     * @return the event start date
     */
    public Date getEventStart() {
        return eventStart;
    }

    /**
     * Get the event end date
     * @return the event end date
     */
    public Date getEventEnd() {
        return eventEnd;
    }

    /**
     * Get the event registration start date
     * @return the event registration start date
     */
    public Date getRegistrationStart() {
        return registrationStart;
    }

    /**
     * Get the event registration end date
     * @return the event registration end date
     */
    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    /**
     * Get the event location
     * @return the event location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Get the event capacity
     * @return the event capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Get the event price
     * @return the event price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Get the event poster url
     * @return the event poster url
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Get the event QR code
     * @return the event QR code
     */
    public String getQrCodeHash() {
        return qrCodeHash;
    }

    /**
     * Get the event's organizer ID
     * @return the event's organizer ID
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Get the event waitlist
     * @return the event waitlist
     */
    public List<String> getWaitlist() {
        return waitlist;
    }

    /**
     * Get the event selected entrants list
     * @return the event selected entrants list
     */
    public List<String> getSelectedEntrants() {
        return selectedEntrants;
    }

    /**
     * Get the event accepted entrants list
     * @return the event accepted entrants list
     */
    public List<String> getAcceptedEntrants() { return acceptedEntrants; }

    /**
     * Get the event declined entrants list
     * @return the event declined entrants list
     */
    public List<String> getDeclinedEntrants() { return declinedEntrants; }


    // Setters (updates corresponding field in Firestore)

    /**
     * Set the event name
     * @param eventName
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
        events.document(eventId).update("eventName", eventName);
    }

    /**
     * Set the event description
     * @param eventDescription
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
        events.document(eventId).update("eventDescription", eventDescription);
    }

    /**
     * Set the event start date
     * @param eventStart
     */
    public void setEventStart(Date eventStart) {
        this.eventStart = eventStart;
        events.document(eventId).update("eventStart", eventStart);
    }

    /**
     * Set the event end date
     * @param eventEnd
     */
    public void setEventEnd(Date eventEnd) {
        this.eventEnd = eventEnd;
        events.document(eventId).update("eventEnd", eventEnd);
    }

    /**
     * Set the event registration start date
     * @param registrationStart
     */
    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
        events.document(eventId).update("registrationStart", registrationStart);
    }

    /**
     * Set the event registration end date
     * @param registrationEnd
     */
    public void setRegistrationEnd(Date registrationEnd) {
        this.registrationEnd = registrationEnd;
        events.document(eventId).update("registrationEnd", registrationEnd);
    }

    /**
     * Set the event location
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
        events.document(eventId).update("location", location);
    }

    /**
     * Set the event capacity
     * @param capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
        events.document(eventId).update("capacity", capacity);
    }

    /**
     * Set the event price
     * @param price
     */
    public void setPrice(int price) {
        this.price = price;
        events.document(eventId).update("price", price);
    }

    /**
     * Set the event poster url
     * @param posterUrl
     */
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
        events.document(eventId).update("posterUrl", posterUrl);
    }

    /**
     * Set the event QR code
     * @param qrCodeHash
     */
    public void setQrCodeHash(String qrCodeHash) {
        this.qrCodeHash = qrCodeHash;
        events.document(eventId).update("qrCodeHash", qrCodeHash);
    }

    /**
     * Set the event's organizer ID
     * @param organizerId
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
        events.document(eventId).update("organizerId", organizerId);
    }

    /**
     * Set the event waitlist
     * @param waitlist
     */
    public void setWaitlist(List<String> waitlist) {
        if(waitlist == null || waitlist.isEmpty()){
            waitlist = new ArrayList<String>();
        } else {
            this.waitlist = waitlist;
        }
        events.document(eventId).update("waitlist", waitlist);
    }

    /**
     * Set the event selected entrants list
     * @param selectedEntrants
     */
    public void setSelectedEntrants(List<String> selectedEntrants) {
        if(selectedEntrants == null || selectedEntrants.isEmpty()){
            selectedEntrants = new ArrayList<String>();
        } else {
            this.selectedEntrants = selectedEntrants;
        }
        events.document(eventId).update("selectedEntrants", selectedEntrants);
    }

    /**
     * Set the event accepted entrants list
     * @param acceptedEntrants
     */
    public void setAcceptedEntrants(List<String> acceptedEntrants) {
        if(acceptedEntrants == null || acceptedEntrants.isEmpty()){
            acceptedEntrants = new ArrayList<String>();
        } else {
            this.acceptedEntrants = acceptedEntrants;
        }
        events.document(eventId).update("acceptedEntrants", acceptedEntrants);
    }

    /**
     * Set the event declined entrants list
     * @param declinedEntrants
     */
    public void setDeclinedEntrants(List<String> declinedEntrants) {
        if (declinedEntrants == null || declinedEntrants.isEmpty()){
            declinedEntrants = new ArrayList<String>();
        } else {
            this.declinedEntrants = declinedEntrants;
        }
        events.document(eventId).update("selectedEntrants", declinedEntrants);
    }

    /**
     * Add entrants to the waiting list
     * @param userId
     */
    public void addToWaitlist(String userId) {
        if (waitlist == null) {
            waitlist = new ArrayList<>();  // Initialize if null
        }
        if (!waitlist.contains(userId)) {
            waitlist.add(userId);
            events.document(eventId).update("waitlist", waitlist);
        }
    }

    /**
     * Remove entrants from the waiting list
     * @param userId
     */
    public void removeFromWaitlist(String userId) {
        if (waitlist == null) {
            waitlist = new ArrayList<>();  // Initialize if null
        }
        if (waitlist.contains(userId)) {
            waitlist.remove(userId);
            events.document(eventId).update("waitlist", waitlist);
        }
    }

    /**
     * Add entrants to the selected list
     * @param userId
     */
    public void addToSelectedlist(String userId) {
        if (selectedEntrants == null) {
            selectedEntrants = new ArrayList<>();  // Initialize if null
        }
        if (!selectedEntrants.contains(userId)) {
            selectedEntrants.add(userId);
            events.document(eventId).update("selectedEntrants", selectedEntrants);
        }
    }

    /**
     * Remove entrants from the selected list
     * @param userId
     */
    public void removeFromSelectedlist(String userId) {
        if (selectedEntrants == null) {
            selectedEntrants = new ArrayList<>();  // Initialize if null
        }
        if (selectedEntrants.contains(userId)) {
            selectedEntrants.remove(userId);
            events.document(eventId).update("selectedEntrants", selectedEntrants);
        }
    }

    /**
     * Add entrants to the accepted list
     * @param userId
     */
    public void addToAcceptedlist(String userId) {
        if (acceptedEntrants == null) {
            acceptedEntrants = new ArrayList<>();  // Initialize if null
        }
        if (!acceptedEntrants.contains(userId)) {
            acceptedEntrants.add(userId);
            events.document(eventId).update("acceptedEntrants", acceptedEntrants);
        }
    }

    /**
     * Remove entrants from the accepted list
     * @param userId
     */
    public void removeFromAcceptedlist(String userId) {
        if (acceptedEntrants == null) {
            acceptedEntrants = new ArrayList<>();  // Initialize if null
        }
        if (acceptedEntrants.contains(userId)) {
            acceptedEntrants.remove(userId);
            events.document(eventId).update("acceptedEntrants", acceptedEntrants);
        }
    }

    /**
     * Add entrants to the declined list
     * @param userId
     */
    public void addToDeclinedlist(String userId) {
        if (declinedEntrants == null) {
            declinedEntrants = new ArrayList<>();  // Initialize if null
        }
        if (!declinedEntrants.contains(userId)) {
            declinedEntrants.add(userId);
            events.document(eventId).update("declinedEntrants", declinedEntrants);
        }
    }

    /**
     * Remove entrants from the declined list
     * @param userId
     */
    public void removeFromDeclinedlist(String userId) {
        if (declinedEntrants == null) {
            declinedEntrants = new ArrayList<>();  // Initialize if null
        }
        if (declinedEntrants.contains(userId)) {
            declinedEntrants.remove(userId);
            events.document(eventId).update("declinedEntrants", declinedEntrants);
        }
    }

    /**
     * Converts the Event object to a Map to be stored in Firestore.
     * @return A Map representing the Event object.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> eventMap = new HashMap<>();

        eventMap.put("eventName", eventName);
        eventMap.put("eventDescription", eventDescription);
        eventMap.put("eventStart", eventStart);
        eventMap.put("eventEnd", eventEnd);
        eventMap.put("registrationStart", registrationStart);
        eventMap.put("registrationEnd", registrationEnd);
        eventMap.put("location", location);
        eventMap.put("capacity", capacity);
        eventMap.put("price", price);
        eventMap.put("posterUrl", posterUrl);
        eventMap.put("qrCode", qrCodeHash);
        eventMap.put("organizerId", organizerId);

        return eventMap;
    }
}