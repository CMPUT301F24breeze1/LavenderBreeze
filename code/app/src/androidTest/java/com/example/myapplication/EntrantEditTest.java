package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import android.util.Log;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.Thread;

@RunWith(AndroidJUnit4.class)
public class EntrantEditTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRead = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    @Test
    public void testEditUserName() {
        scenario.getScenario().onActivity(activity -> {
            // Navigate to EntrantProfile using NavController
            NavController navController = Navigation.findNavController(activity, R.id.fragmentContainerView);
            navController.navigate(R.id.entrantProfile3);
        });
        try {
            Thread.sleep(10000);
            Log.d("test", "testEditUserName: ");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.editButton)).perform(click());

        try {
            Thread.sleep(10000);
            Log.d("test", "testEditUserName: ");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
//
//        // Update the user name in the EntrantEditProfile fragment
//        Espresso.onView(ViewMatchers.withId(R.id.editNameButton))
//                .perform(ViewActions.clearText(), ViewActions.typeText("Test1"), ViewActions.closeSoftKeyboard());
//
//        // Click on the save button in EntrantEditProfile
//        Espresso.onView(ViewMatchers.withId(R.id.doneEdit))
//                .perform(ViewActions.click());
//
//        // Verify the updated name is displayed in EntrantProfile
//        Espresso.onView(ViewMatchers.withId(R.id.personName))
//                .check(ViewAssertions.matches(ViewMatchers.withText("Test1")));
//    }
//}
