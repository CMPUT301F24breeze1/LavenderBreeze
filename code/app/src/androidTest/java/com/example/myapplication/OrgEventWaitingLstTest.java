package com.example.myapplication;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import com.example.myapplication.model.Event;

@RunWith(AndroidJUnit4.class)
public class OrgEventWaitingLstTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);
    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRead = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    @Before
    public void navigate() throws InterruptedException {
        // Perform navigation to the required screen before each test
        onView(withId(R.id.button_go_to_add_facility)).perform(click());
        onView(withId(R.id.buttonCreateFacility)).perform(click());
        Thread.sleep(3000); // Wait for the facility creation process (use IdlingResource instead of Thread.sleep in production)
        onData(is(instanceOf(Event.class)))
                .inAdapterView(withId(R.id.event_list_view))
                .atPosition(0)
                .perform(click());
        Thread.sleep(1000); // Wait for navigation (use IdlingResource in production)
        onView(withId(R.id.button_go_to_waiting_list_from_org_event)).perform(click());
    }

    @Test
    public void testWaitingListLayoutElements() {
        // Check if key elements are displayed
        onView(withId(R.id.waitlist_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("Waiting List")));

        // Verify back button
        onView(withId(R.id.button_go_to_event_from_org_event_waiting_lst))
                .check(matches(isDisplayed()));

        // Verify notification button
        onView(withId(R.id.button_go_to_notif_from_org_event_waiting_lst))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationButtons() {
        // Test "Go to Selected List" button
        onView(withId(R.id.button_go_to_selected_list_from_org_event_waiting_lst))
                .perform(click());

        // Add assertion to verify navigation to Selected List screen
        // This might involve checking a new activity or fragment title

        // Return to waiting list
        Espresso.pressBack();

        // Test "Go to Map" button
        onView(withId(R.id.button_go_to_map_from_org_event_waiting_lst))
                .perform(click());

        // Add assertion to verify navigation to Map screen

        // Return to waiting list
        Espresso.pressBack();
    }

    @Test
    public void testListViewInteraction() {
        // Verify ListView exists
        onView(withId(R.id.event_waitlist_listview))
                .check(matches(isDisplayed()));

        // Optional: If your list has items, test clicking an item
        // Note: This requires your ListView to be populated with test data
        // onData(anything())
        //     .inAdapterView(withId(R.id.event_waitlist_listview))
        //     .atPosition(0)
        //     .perform(click());
    }

    @Test
    public void testFloatingActionButton() {
        // Test the "Select Entrants" floating action button
        onView(withId(R.id.button_select_entrants))
                .check(matches(isDisplayed()))
                .perform(click());

        // Add assertions to verify the action of the FAB
        // This depends on what happens when the FAB is clicked in your app
    }

    @Test
    public void testNotificationButton() {
        // Click on notification button
        onView(withId(R.id.button_go_to_notif_from_org_event_waiting_lst))
                .perform(click());

        // Add assertions to verify navigation to notification screen
        // This might involve checking if a new activity or fragment is displayed
    }

    @Test
    public void testBackButton() {
        // Test back button functionality
        onView(withId(R.id.button_go_to_event_from_org_event_waiting_lst))
                .perform(click());

        // Add assertion to verify navigation back to previous screen
    }
}
