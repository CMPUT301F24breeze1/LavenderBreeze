package com.example.myapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.runner.permission.UiAutomationPermissionGranter;

import com.example.myapplication.model.Event;
import com.example.myapplication.model.Facility;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AdminEventsListTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

//    @Rule
//    public UiAutomationPermissionGranter permissionGranter = new UiAutomationPermissionGranter();
//
//    @Rule
//    public void setPermissionGranter(UiAutomationPermissionGranter permissionGranter) {
//        this.permissionGranter = permissionGranter;
//        permissionGranter.addPermissions("Manifest.permissions.ACCESS_FINE_LOCATION","Manifest.permissions.CAMERA");
//    }

    @Before
    public void CreateUser() throws InterruptedException {
        // Perform navigation to the required screen before each test
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", "UITest");
        eventData.put("eventDescription", "eventDescription");
        eventData.put("eventStart", Date.getDefaultInstance());
        eventData.put("eventEnd", Date.getDefaultInstance());
        eventData.put("registrationStart", Date.getDefaultInstance());
        eventData.put("registrationEnd", Date.getDefaultInstance());
        eventData.put("location", "UITest");
        eventData.put("capacity", 1);
        eventData.put("price", 1);
        eventData.put("posterUrl", "posterUrl");
        eventData.put("qrCodeHash", "");
        eventData.put("organizerId", "UITest");
        eventData.put("waitlist", new ArrayList<>());
        eventData.put("selectedEntrants", new ArrayList<>());
        eventData.put("acceptedEntrants", new ArrayList<>());
        eventData.put("declinedEntrants", new ArrayList<>());
        eventData.put("geolocationRequired", false);

        FirebaseFirestore.getInstance().collection("events").add(eventData).addOnSuccessListener(documentReference -> {
            String eventId = documentReference.getId();  // Get the new event ID
            Log.d("Event", "Event created with ID: " + eventId);
        }).addOnFailureListener(e -> {
            Log.e("Event", "Error creating event", e);
        });


        Thread.sleep(1000);
    }

    @Before
    public void navigate() throws InterruptedException {
        // Perform navigation to the required screen before each test
        Thread.sleep(1000);
        onView(withId(R.id.button_go_to_admin)).perform(click());
        onView((withId(R.id.events))).perform(click());
        Thread.sleep(1000);
    }



    @Test
    public void EventDeletionTest(){
        onData(is(instanceOf(Event.class)))
                .inAdapterView(withId(R.id.list_view_admin_events_list))
                .atPosition(0)
                .perform(click());
    }
}
