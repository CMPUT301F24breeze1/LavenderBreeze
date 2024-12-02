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
import androidx.test.runner.permission.UiAutomationPermissionGranter;

import com.example.myapplication.model.Facility;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AdminFacilitiesListTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);
    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRead = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);



    @Before
    public void navigate() throws InterruptedException {
        // Perform navigation to the required screen before each test
        Thread.sleep(3000);
        onView(withId(R.id.button_go_to_admin)).perform(click());
        onView((withId(R.id.facilities))).perform(click());
        Thread.sleep(1000);
    }


    @Test
    public void CreateFacility() throws InterruptedException {
        // Perform navigation to the required screen before each test
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityName", "facilityName");
        facilityData.put("facilityAddress", "facilityAddress");
        facilityData.put("facilityEmail", "facilityEmail"); // Consistent key
        facilityData.put("facilityPhoneNumber", "0000000000");
        facilityData.put("organizerId", "organizerId");
        facilityData.put("events", new ArrayList<>());
        facilityData.put("profileImageUrl",null);
        FirebaseFirestore.getInstance().collection("facilities")
                .add(facilityData);
        Thread.sleep(1000);
    }

    @Test
    public void FacilityDeletionTest(){
        onData(is(instanceOf(Facility.class)))
                .inAdapterView(withId(R.id.list_view_admin_facilities_list))
                .atPosition(0)
                .perform(click());
    }
}
