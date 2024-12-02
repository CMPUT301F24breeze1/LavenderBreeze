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

import com.example.myapplication.model.Event;
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

public class OrgEventSelectedListTest {

    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRead = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);



    @Before
    public void navigate() throws InterruptedException {

        Thread.sleep(10000);
        // Perform navigation to the required screen before each test
        onView(withId(R.id.button_go_to_add_facility)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.buttonCreateFacility)).perform(click());
        Thread.sleep(3000); // Wait for the facility creation process (use IdlingResource instead of Thread.sleep in production)
        onData(is(instanceOf(Event.class)))
                .inAdapterView(withId(R.id.event_list_view))
                .atPosition(0)
                .perform(click());
        Thread.sleep(2000); // Wait for navigation (use IdlingResource in production)
        onView(withId(R.id.button_go_to_waiting_list_from_org_event)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.button_go_to_selected_list_from_org_event_waiting_lst)).perform(click());
    }

    @Test
    public void testNavigateBackToEventFromOrgEventSelectedList() {
        onView(withId(R.id.button_go_to_event_from_org_event_selected_lst)).perform(click());
    }

    @Test
    public void testNavigateToNotificationFromOrgEventSelectedList() {
        onView(withId(R.id.button_go_to_notif_from_org_event_selected_lst)).perform(click());
    }

    @Test
    public void testFilterSelected() {
        onView(withId(R.id.filterAll)).perform(click());
    }

    @Test
    public void testFilterAccepted() {
        onView(withId(R.id.filterAccepted)).perform(click());
    }

    @Test
    public void testFilterCancelled() {
        onView(withId(R.id.filterCancelled)).perform(click());
    }
}
