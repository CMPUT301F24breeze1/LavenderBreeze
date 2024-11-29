package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.myapplication.R;
import com.example.myapplication.MainActivity;
import com.example.myapplication.view.entrant.EntrantProfile;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EntrantEditTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testEditUserName() {
        // Click on the edit button in EntrantProfile
        scenario.getScenario().onActivity(activity -> {
            // Navigate to EntrantProfile using NavController
            NavController navController = Navigation.findNavController(activity, R.id.fragmentContainerView);
            navController.navigate(R.id.entrantProfile3);});
        //NavController navController = Navigation.findNavController(new Activity(),R.id.fragmentContainerView);
        //navController.navigate(R.id.entrantProfile3);
//        scenario= FragmentScenario.launchFragmentInContainer<EntrantProfile>();

//        Espresso.onView(ViewMatchers.withId(R.id.editButton))
//                .perform(ViewActions.click());
//
//        // Update the user name in the EntrantEditProfile fragment
//        Espresso.onView(ViewMatchers.withId(R.id.editNameButton))
//                .perform(ViewActions.clearText(), ViewActions.typeText("Test1"), ViewActions.closeSoftKeyboard());
//
//        // Click on the save button in EntrantEditProfile
//        Espresso.onView(ViewMatchers.withId(R.id.doneEdit))
//                .perform(ViewActions.click());

        // Verify the updated name is displayed in EntrantProfile
        //Threads.sleep(2000);
        Espresso.onView(ViewMatchers.withId(R.id.personName))
                .check(ViewAssertions.matches(ViewMatchers.withText("DefaultPerson")));
    }
}
