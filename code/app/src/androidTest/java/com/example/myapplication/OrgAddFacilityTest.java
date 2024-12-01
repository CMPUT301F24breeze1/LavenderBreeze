package com.example.myapplication;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.os.SystemClock;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;


@RunWith(AndroidJUnit4.class)
public class OrgAddFacilityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRead = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    @Before
    public void navigate() {
        // Navigate to the OrgAddFacility screen by simulating user interaction
        onView(withId(R.id.button_go_to_add_facility)).perform(click());
    }
    @Test
    public void testAddFacility() {

        // Fill in the form fields
        onView(withId(R.id.editTextFacilityName))
                .perform(replaceText("Test Facility"), closeSoftKeyboard());
        onView(withId(R.id.editTextFacilityAddress))
                .perform(replaceText("123 Test Street, Test City, TC"), closeSoftKeyboard());
        onView(withId(R.id.editTextFacilityEmail))
                .perform(replaceText("testfacility@example.com"), closeSoftKeyboard());
        onView(withId(R.id.editTextFacilityPhone))
                .perform(replaceText("1234567890"), closeSoftKeyboard());

        // Click the Save button
        onView(withId(R.id.buttonCreateFacility)).perform(click());
    }
}
