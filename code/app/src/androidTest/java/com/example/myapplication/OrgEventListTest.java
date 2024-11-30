package com.example.myapplication;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.anything;

import android.app.UiAutomation;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class OrgEventListTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);
    @BeforeClass
    public static void disableAnimations() {
        if (SDK_INT >= JELLY_BEAN_MR1) {
            UiAutomation automation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
            automation.executeShellCommand("settings put global window_animation_scale 0");
            automation.executeShellCommand("settings put global transition_animation_scale 0");
            automation.executeShellCommand("settings put global animator_duration_scale 0");
        }
    }

    private void navigate() throws InterruptedException {
        // Simulate user interaction to navigate to the desired screen

        // Click the button to go to Facility screen from OrgEventList
        onView(withId(R.id.button_go_to_add_facility)).perform(click());

        // Click the "Create Facility" button to navigate further
        onView(withId(R.id.buttonCreateFacility)).perform(click());

    }

    @Test
    public void testNavigateToAddEventFromOrgEventList() throws InterruptedException {
        navigate();
        onView(withId(R.id.button_go_to_add_event_from_org_events_lst)).perform(click());
    }

    @Test
    public void testNavigateToHomeFromOrgEventList() throws InterruptedException {
        navigate();
        onView(withId(R.id.button_go_to_home_from_org_event_list)).perform(click());
    }
}