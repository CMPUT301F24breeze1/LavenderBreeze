package com.example.myapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.location.GnssAntennaInfo;
import android.util.Log;

import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.runner.permission.UiAutomationPermissionGranter;

import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
public class AdminUsersListTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

//    @Before
//    public void setPermissionGranter() {
//        UiAutomationPermissionGranter permissionGranter = new UiAutomationPermissionGranter();
//        permissionGranter.addPermissions("Manifest.permissions.ACCESS_FINE_LOCATION","Manifest.permissions.CAMERA");
//    }

    @Before
    public void CreateUser() throws InterruptedException {
        // Perform navigation to the required screen before each test
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", "1");
        userData.put("email", "default@example.com");
        userData.put("phoneNumber", "1111111111");
        userData.put("isEntrant", false);
        userData.put("isOrganizer", false);
        userData.put("isAdmin", false);
        userData.put("isFacility", false);
        userData.put("profilePicture", "");
        userData.put("requestedEvents", new ArrayList<>());
        userData.put("selectedEvents", new ArrayList<>());
        userData.put("cancelledEvents", new ArrayList<>());
        userData.put("acceptedEvents", new ArrayList<>());
        userData.put("toggleNotif", true);
        userData.put("deterministicPicture", true);

        FirebaseFirestore.getInstance().collection("users").document("UITest").set(userData).addOnSuccessListener(aVoid -> {
            Log.d("User", "DocumentSnapshot successfully written!");
        }).addOnFailureListener(e -> {
            Log.w("User", "Error writing document", e);
        });
        Thread.sleep(1000);
    }

    @Before
    public void navigate() throws InterruptedException {
        // Perform navigation to the required screen before each test
        Thread.sleep(1000);
        onView(withId(R.id.button_go_to_admin)).perform(click());
        Thread.sleep(1000);
    }



    @Test
    public void userDeletionTest(){
        onData(is(instanceOf(User.class)))
                .inAdapterView(withId(R.id.list_view_admin_users_list))
                .atPosition(0)
                .perform(click());
    }

}
