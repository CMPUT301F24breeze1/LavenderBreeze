package com.example.myapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.app.AlertDialog;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.permission.UiAutomationPermissionGranter;

import com.example.myapplication.model.Event;
import com.example.myapplication.model.Facility;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AdminEventsListTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRead = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);



    @Before
    public void navigate() throws InterruptedException {
        // Perform navigation to the required screen before each test
        Thread.sleep(2000);
        onView(withId(R.id.button_go_to_admin)).perform(click());
        Thread.sleep(1000);
        onView((withId(R.id.events))).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void CreateEvent() throws InterruptedException {
        // Perform navigation to the required screen before each test


        Event event = new Event("String eventId", "String eventName", "String eventDescription", Date.from(Instant.now()), Date.from(Instant.now()),
                Date.from(Instant.now()), Date.from(Instant.now()), "", 5, 5,
                "", "", "", new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false);

        FirebaseFirestore.getInstance().collection("events").add(event.toMap());

        Thread.sleep(1000);



        onView((withId(R.id.facilities))).perform(click());
        Thread.sleep(500);
        onView((withId(R.id.events))).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void EventDeletionTest() throws InterruptedException {
        onData(is(instanceOf(Event.class)))
                .inAdapterView(withId(R.id.list_view_admin_events_list))
                .atPosition(0)
                .perform(click());
        Thread.sleep(1000);
        onView(withId(android.R.id.button1)).perform(click());
    }
}
