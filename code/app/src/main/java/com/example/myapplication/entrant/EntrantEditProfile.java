package com.example.myapplication.entrant;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantEditProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantEditProfile extends Fragment {
    private Button doneEditButton;
    private ImageButton editPhotoButton, editNameButton, editEmailButton, editPhoneButton;
    private ImageView profilePicture;
    private TextView personName, emailAddress, contactPhoneNumber;
    private SwitchCompat emailNotificationSwitch;
    private User user;
    private UserViewModel userViewModel;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public EntrantEditProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntrantEditProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static EntrantEditProfile newInstance(String param1, String param2) {
        EntrantEditProfile fragment = new EntrantEditProfile();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_edit_profile, container, false);

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        // Find the views
        doneEditButton = view.findViewById(R.id.doneEdit);
        editPhotoButton = view.findViewById(R.id.editPhotoButton);
        editNameButton = view.findViewById(R.id.editNameButton);
        editEmailButton = view.findViewById(R.id.editEmailButton);
        editPhoneButton = view.findViewById(R.id.editPhoneButton);
        profilePicture = view.findViewById(R.id.profilePicture);
        personName = view.findViewById(R.id.personName);
        emailAddress = view.findViewById(R.id.emailAddress);
        contactPhoneNumber = view.findViewById(R.id.contactPhoneNumber);
        emailNotificationSwitch = view.findViewById(R.id.emailNotificationSwitch);

        updateUserData();
        doneEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
                Bundle result = new Bundle();
                result.putSerializable("updated_user", user);
                getParentFragmentManager().setFragmentResult("requestKey", result);
                Navigation.findNavController(v).navigate(R.id.action_entrantEditProfile_to_entrantProfile3);
            }
        });

        // Set up edit buttons to open dialogs for editing
        editNameButton.setOnClickListener(v -> showEditDialog("Name", user.getName(), newValue -> {
            user.setName(newValue);
            updateUserData();
        }));

        editEmailButton.setOnClickListener(v -> showEditDialog("Email", user.getEmail(), newValue -> {
            user.setEmail(newValue);
            updateUserData();
        }));

        editPhoneButton.setOnClickListener(v -> showEditDialog("Phone Number", user.getPhoneNumber(), newValue -> {
            user.setPhoneNumber(newValue);
            updateUserData();
        }));

        // Set up the email notification switch
        emailNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(requireContext(), "Email Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Email Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });
        // Find the button and set an onClickListener to navigate to org_event_lst.xml
//        Button profile = view.findViewById(R.id.button_go_to_entrant_profile);
//        profile.setOnClickListener(v ->
//                Navigation.findNavController(v).navigate(R.id.action_entrantEditProfile_to_entrantProfile3)
//        );
        return view;
    }

    private void updateUserData() {
        // Update the TextViews and Switch with User's data
        //Log.d("User", "Entrant profile: " + user.getName());
        personName.setText(user.getName());
        emailAddress.setText(user.getEmail());
        contactPhoneNumber.setText(user.getPhoneNumber());

        // Example of setting an email notification switch (if stored in User class)
        //emailNotificationSwitch.setChecked(user.getIsEntrant());
    }
    private void showEditDialog(String fieldName, String currentValue, EditDialogFragment.OnEditCompleteListener listener) {
        EditDialogFragment dialog = EditDialogFragment.newInstance(fieldName, currentValue);
        dialog.setOnEditCompleteListener(listener);
        dialog.show(getParentFragmentManager(), "EditDialogFragment");
    }
}