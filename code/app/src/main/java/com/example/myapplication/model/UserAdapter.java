package com.example.myapplication.model;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import com.example.myapplication.R;
import com.example.myapplication.organization.OrgEventSelectedLst;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    private String status;
    private String eventId;
    private Event event;
    private List<String> waitlist;
    private List<String> selectedList;
    private List<String> acceptedList;
    private List<String> canceledList;
    private User replacement;
    private User replaced;
    private Bundle arguments;

    public UserAdapter(Context context, List<User> users, String status, String eventId, Bundle bundle) {
        super(context, 0,users);
        this.status = status;
        this.eventId = eventId;
        this.arguments = bundle;
    // Pass the status to determine the action on click
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item, parent, false);
        }

        User user = getItem(position);
        TextView eventName = convertView.findViewById(R.id.eventNameTextView);
        TextView organizerName = convertView.findViewById(R.id.organizerNameTextView);
        TextView statusTag = convertView.findViewById(R.id.statusTagTextView);
        ImageView arrowImage = convertView.findViewById(R.id.arrowImageView);

        eventName.setText(user.getName());
        organizerName.setText("Organized by: " + user.getEmail());
        statusTag.setText(status);

        // Set arrow click listener based on status
        arrowImage.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            replaced = (User) bundle.getSerializable("user");
            event = new Event(eventId);
            event.loadEventDataAsync(new Event.OnEventDataLoadedListener() {
                @Override
                public void onEventDataLoaded(Event loadedEvent) {
                    if (loadedEvent != null) {
                        selectedList = loadedEvent.getSelectedEntrants();
                        acceptedList = loadedEvent.getAcceptedEntrants();
                        canceledList = loadedEvent.getDeclinedEntrants();
                        waitlist = loadedEvent.getWaitlist();

                    }
                    Log.d("OrgEventSelectedLst", "Received Accepted List: ");
                    if(waitlist.isEmpty()){
                        Toast.makeText(v.getContext(), "There is nobody to replace this entrant", Toast.LENGTH_SHORT).show();
                    } else {
                        replacement = new User(waitlist.get(0),loadedUser -> {
                            if (loadedUser != null) {
                                Log.d("UserAdapter", "Loaded User: " + loadedUser.getName());
                            }

                        });
                        switch (status) {
                            case "all":
                                // move event in the profile of the entrant being cancelled
                                replaced.removeSelectedEvent(event.getEventId());
                                replaced.addCancelledEvent(event.getEventId());

                                // move event in the profile of the entrant being drawn
                                replacement.addSelectedEvent(event.getEventId());
                                replacement.removeRequestedEvent(event.getEventId());

                                // move entrants in event lists
                                event.removeFromWaitlist(replacement.getDeviceID());
                                event.addToSelectedlist(replacement.getDeviceID());
                                event.removeFromSelectedlist(replaced.getDeviceID());
                                event.addToDeclinedlist(replaced.getDeviceID());
                                notifyDataSetChanged();
                                break;
                            case "accepted":
                                // move event in the profile of the entrant being cancelled
                                replaced.removeAcceptedEvent(event.getEventId());
                                replaced.addCancelledEvent(event.getEventId());

                                // move event in the profile of the entrant being drawn
                                replacement.addSelectedEvent(event.getEventId());
                                replacement.removeRequestedEvent(event.getEventId());

                                // move entrants in event lists
                                event.removeFromWaitlist(replacement.getDeviceID());
                                event.addToSelectedlist(replacement.getDeviceID());
                                event.removeFromAcceptedlist(replaced.getDeviceID());
                                event.addToDeclinedlist(replaced.getDeviceID());
                                notifyDataSetChanged();
                                break;
                            case "canceled":
                                Toast.makeText(v.getContext(), "This entrant has already been cancelled", Toast.LENGTH_SHORT).show();
                                break;
                            default:

                                break;
                        }
                        Navigation.findNavController(v).navigate(R.id.action_OrgEventSelectedLst_self,arguments);
                    }
                }
            });

            //Navigation.findNavController(v).navigate(actionId);
        });

        return convertView;
    }
}
