// From chatgpt, openai, "write a java implementation with java documentation of EntrantProfile
//class with methods to show the profile of the entrant
//given here is the xml code for it", 2024-11-02
package com.example.myapplication.entrant;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.myapplication.R;

import java.io.Serializable;

/**
 * A fragment that displays the profile information of an entrant, including name, email,
 * phone number, and profile picture. It allows navigation to an edit profile page and
 * manages updates to the profile data.
 */
public class EntrantProfile extends Fragment {
    private UserController user;
    private TextView personName, emailAddress, contactPhoneNumber;
    private ImageButton edit, notifications;
    private ImageView profilePicture;
    private SwitchCompat emailNotificationSwitch;
    Button homeButton, profileButton, eventsButton;
    /**
     * Default constructor required for instantiating the fragment.
     */
    public EntrantProfile() {
        // Required empty public constructor
    }
    /**
     * Called after the fragment's view has been created. It listens for updated user data
     * and initializes the bottom navigation buttons.
     * @param view the fragment's root view
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            UserController updatedUser = (UserController) bundle.getSerializable("updated_user");
            if (updatedUser != null) {
                user = updatedUser; // Update the local user instance
                updateUserData(); // Refresh the displayed user data
            }
        });
        if(user == null) {
            user = new UserController(requireContext(), () -> updateUserData());
        }
        // Update UI with new user data
        intializeBottomNavButton(view);
    }
    /**
     * Inflates the view for the fragment, sets up the UI components, and configures
     * the edit button to navigate to the profile editing page.
     * @param inflater LayoutInflater to inflate the view
     * @param container ViewGroup container for the fragment
     * @param savedInstanceState Bundle containing the saved instance state
     * @return the inflated view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_profile, container, false);

        // Find the button and set an onClickListener to navigate to org_event_lst.xml
        edit = view.findViewById(R.id.editButton);

        edit.setOnClickListener(v ->
        {
            Bundle result = new Bundle();
            result.putSerializable("updated_user", (Serializable) user);
            Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_entrantEditProfile,result);
        });

        // Reference the views
        personName = view.findViewById(R.id.personName);
        emailAddress = view.findViewById(R.id.emailAddress);
        contactPhoneNumber = view.findViewById(R.id.contactPhoneNumber);
        emailNotificationSwitch = view.findViewById(R.id.emailNotificationSwitch);
        profilePicture = view.findViewById(R.id.profilePicture);
        return view;
    }
    /**
     * Updates the displayed user data in the UI components.
     */
    private void updateUserData() {
        // Update the TextViews and Switch with User's data
        if (user != null) {
            personName.setText(user.getUserName());
            emailAddress.setText(user.getUserEmail());
            contactPhoneNumber.setText(user.getUserPhoneNumber());
            user.loadProfilePictureInto(profilePicture,requireContext(),user.getUserProfilePicture());
        }
        // Example of setting an email notification switch (if stored in User class)
        //emailNotificationSwitch.setChecked(user.getIsEntrant());
    }
    /**
     * Initializes the bottom navigation buttons.
     * @param view the fragment's root view
     */
    public void intializeBottomNavButton(View view){
        homeButton = view.findViewById(R.id.homeButton);
        profileButton = view.findViewById(R.id.profileButton);
        eventsButton = view.findViewById(R.id.eventsButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_home); // ID of the destination in nav_graph.xml
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_self); // ID of the destination in nav_graph.xml
            }
        });
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_entrantProfile3_to_entrantEventsList); // ID of the destination in nav_graph.xml
            }
        });
    }
}