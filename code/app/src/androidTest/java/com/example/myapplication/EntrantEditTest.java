package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.util.Log;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.Thread;

/**
 * UI test class for testing the edit functionality in the EntrantProfile screen.
 * This class uses Espresso and UI navigation to verify that the user can
 * successfully edit their name and save the changes.
 */
@RunWith(AndroidJUnit4.class)
public class EntrantEditTest {
    /**
     * Rule to launch the main activity for testing.
     * Provides access to the activity lifecycle and ensures that the test
     * environment is properly set up before each test.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRead = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    /**
     * Tests the editing functionality for the user's name.
     * The test navigates to the EntrantProfile screen, opens the edit name dialog,
     * updates the name to "TestName", and verifies that the new name is displayed
     * correctly on the profile.
     */
    @Test
    public void testEditUserName() {
        scenario.getScenario().onActivity(activity -> {
            // Navigate to EntrantProfile using NavController
            NavController navController = Navigation.findNavController(activity, R.id.fragmentContainerView);
            navController.navigate(R.id.entrantProfile3);
        });
        try {
            Thread.sleep(2500);
            Log.d("test", "testEditUserName: ");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withId(R.id.editButton)).perform(click());
        onView(withId(R.id.editNameButton)).perform(click());
        onView(withId(R.id.editText)).perform(ViewActions.clearText(),typeText("TestName"),closeSoftKeyboard());
        onView(withId(R.id.dialog_positive_button)).perform(click());
        onView(withId(R.id.doneEdit)).perform(click());
        onView(withId(R.id.personName)).check(ViewAssertions.matches(ViewMatchers.withText("TestName")));
        try {
            Thread.sleep(2500);
            Log.d("test", "testEditUserName: ");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
