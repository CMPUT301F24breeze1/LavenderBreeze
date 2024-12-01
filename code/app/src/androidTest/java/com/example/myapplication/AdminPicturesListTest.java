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
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.permission.UiAutomationPermissionGranter;

import com.example.myapplication.model.Facility;
import com.example.myapplication.model.User;
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
public class AdminPicturesListTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRead = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);


    @Before
    public void CreateUser() throws InterruptedException {
        // Perform navigation to the required screen before each test
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", "1");
        userData.put("email", "default@example.com");
        userData.put("phoneNumber", "1111111111");
        userData.put("isEntrant", false);
        userData.put("isOrganizer", false);
        userData.put("isAdmin", false);
        userData.put("isFacility", false);
        userData.put("profilePicture", "https://firebasestorage.googleapis.com/v0/b/lottery-breeze.appspot.com/o/event_posters%2F1732930373725.jpg?alt=media&token=1638502d-b5cf-49e7-927a-1a7aa7c7e77c");
        userData.put("requestedEvents", new ArrayList<>());
        userData.put("selectedEvents", new ArrayList<>());
        userData.put("cancelledEvents", new ArrayList<>());
        userData.put("acceptedEvents", new ArrayList<>());
        userData.put("toggleNotif", true);
        userData.put("deterministicPicture", true);

        FirebaseFirestore.getInstance().collection("users").document("UITest").set(userData).addOnSuccessListener(aVoid -> {
            Log.d("User", "DocumentSnapshot successfully written!");
        }).addOnFailureListener(e -> {
            Log.w("User", "Error writing document", e);
        });
        Thread.sleep(1000);

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
        eventData.put("qrCodeHash", "https://firebasestorage.googleapis.com/v0/b/lottery-breeze.appspot.com/o/event_posters%2F1732930373725.jpg?alt=media&token=1638502d-b5cf-49e7-927a-1a7aa7c7e77c");
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
    }

    @Before
    public void navigate() throws InterruptedException {
        // Perform navigation to the required screen before each test
        Thread.sleep(1000);
        onView(withId(R.id.button_go_to_admin)).perform(click());
        Thread.sleep(1000);
    }


    @Test
    public void filterButtonTest() throws InterruptedException {
        onView(withId(R.id.button_user_photos)).perform(click());
        onView(withId(R.id.button_event_posters)).perform(click());
        onView(withId(R.id.button_user_photos)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void navigateToPicture(){
        onData(is(instanceOf(User.class)))
                .inAdapterView(withId(R.id.adminPicturesList))
                .atPosition(0)
                .perform(click());
    }

    @Test
    public void deleteImageTest() throws InterruptedException {
        onView(withId(R.id.button_delete_photo)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.back_to_list_button)).perform(click());
    }
}
