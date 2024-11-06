package com.example.myapplication.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.controller.NotificationSender;

import java.util.ArrayList;
import java.util.List;

public class OrgEventSelectedLst extends Fragment {

    private List<String> selectedList;  // List of device IDs for selected users
    private List<String> declinedList;  // List of device IDs for declined users

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_org_event_selected_lst, container, false);

        // Initialize lists
        selectedList = new ArrayList<>();
        declinedList = new ArrayList<>();

        // Retrieve lists of selected and declined user device IDs from arguments
        if (getArguments() != null) {
            selectedList = getArguments().getStringArrayList("selectedList");
            declinedList = getArguments().getStringArrayList("declined");

            // Ensure both lists are initialized if null
            if (selectedList == null) {
                selectedList = new ArrayList<>();
            }
            if (declinedList == null) {
                declinedList = new ArrayList<>();
            }
        }

        // Set up the notification button for selected participants
        Button notifySelectedButton = view.findViewById(R.id.button_notify_top_right_selected_lst);
        notifySelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationToSelected();
                Toast.makeText(getActivity(), "Notification sent to selected users", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the notification button for declined participants
        Button notifyDeclinedButton = view.findViewById(R.id.button_notify_top_right_cancelled_lst);
        notifyDeclinedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationToDeclined();
                Toast.makeText(getActivity(), "Notification sent to declined users", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Method to send a notification to all users in the selected list
     */
    private void sendNotificationToSelected() {
        if (selectedList != null && !selectedList.isEmpty()) {
            NotificationSender notificationSender = new NotificationSender();
            notificationSender.sendNotification(
                    selectedList,                // List of device IDs
                    "Event Update",              // Notification title
                    "You've been selected for the event!" // Notification message
            );
        }
    }

    /**
     * Method to send a notification to all users in the declined list
     */
    private void sendNotificationToDeclined() {
        if (declinedList != null && !declinedList.isEmpty()) {
            NotificationSender notificationSender = new NotificationSender();
            notificationSender.sendNotification(
                    declinedList,                // List of device IDs
                    "Event Update",              // Notification title
                    "We're sorry, but your registration has been declined." // Notification message
            );
        }
    }
}
