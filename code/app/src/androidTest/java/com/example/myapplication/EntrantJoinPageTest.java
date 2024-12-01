package com.example.myapplication;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.myapplication.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.os.Bundle;

@RunWith(AndroidJUnit4.class)
public class EntrantJoinPageTest {
    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRead = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testNavigateToEntrantJoinPageAndInteract() {
        ActivityScenario<MainActivity> scenario = activityScenarioRule.getScenario();

        scenario.onActivity(activity -> {
            // Navigate to EntrantJoinPage using NavController
            NavController navController = Navigation.findNavController(activity, R.id.fragmentContainerView);

            // Simulate navigation with eventID as argument
            Bundle qrCodeData = new Bundle();
            qrCodeData.putString("eventID", "4BygYhMbtz9TBqnoAq5b");
            navController.navigate(R.id.entrantJoinPage, qrCodeData);
        });

        // Assert that the Join Event button is displayed
        onView(withId(R.id.addButton))
                .check(matches(isDisplayed()))
                .check(matches(withText("Join Event")));

        // Click the Join Event button
        onView(withId(R.id.addButton)).perform(click());

        // Assert that the back button is displayed
        onView(withId(R.id.backArrowButton)).check(matches(isDisplayed()));

        // Click the back button to navigate back
        onView(withId(R.id.backArrowButton)).perform(click());
    }
}
