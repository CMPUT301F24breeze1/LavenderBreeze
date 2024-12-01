package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrgAddFacilityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testAddFacility() {
        // Navigate to OrgAddFacility screen by simulating user interaction
        onView(withId(R.id.button_go_to_add_facility)).perform(click());

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
