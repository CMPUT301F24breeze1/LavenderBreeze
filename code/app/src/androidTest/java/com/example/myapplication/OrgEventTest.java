package com.example.myapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.myapplication.model.Event;

import org.junit.Rule;
import org.junit.Test;

public class OrgEventTest {
    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRead = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);
    private void navigate() throws InterruptedException {
        onView(withId(R.id.button_go_to_add_facility)).perform(click());
        onView(withId(R.id.buttonCreateFacility)).perform(click());
        Thread.sleep(1000);
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.event_list_view
        )).atPosition(0).perform(click());
        Thread.sleep(1000);
    }
    @Test
    public void testNavigateBackToEventListFromOrgEventDetails() throws InterruptedException {
        navigate();
        onView(withId(R.id.button_go_to_event_lst_from_org_event)).perform(click());
    }

    @Test
    public void testNavigateToEditEventFromOrgEventDetails() throws InterruptedException {
        navigate();
        onView(withId(R.id.button_go_to_edit_event_from_org_event)).perform(click());
    }

    @Test
    public void testViewQRCodeFromOrgEventDetails() throws InterruptedException {
        navigate();
        onView(withId(R.id.button_go_to_qrcode_from_org_event)).perform(click());
    }

    @Test
    public void testViewWaitingListFromOrgEventDetails() throws InterruptedException {
        navigate();
        onView(withId(R.id.button_go_to_waiting_list_from_org_event)).perform(click());
    }

    @Test
    public void testViewSelectedListFromOrgEventDetails() throws InterruptedException {
        navigate();
        onView(withId(R.id.button_go_to_selected_list_from_org_event)).perform(click());
    }
}