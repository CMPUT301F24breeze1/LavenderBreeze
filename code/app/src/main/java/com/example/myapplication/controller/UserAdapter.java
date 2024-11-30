package com.example.myapplication.controller;

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
import com.example.myapplication.model.Event;
import com.example.myapplication.model.User;

import java.util.List;
/**
 * A {@link ArrayAdapter< User >} subclass.
 * Use the {@link UserAdapter(Context, List, String, String, Bundle)} constructor to
 * create an instance of this fragment.
 */
public class UserAdapter extends ArrayAdapter<User> {
    private String status;
    private String eventId;
    private Event event;
    private List<String> waitlist;
    private User replacement;
    private User replaced;
    private Bundle arguments;

    /**
     * Constructs an an instance of the user adapter with a list of users
     * eventId, and bundle which goes on to have a user put into it when a
     * user in the list gets clicked
     *
     * @param context
     * @param users
     * @param status
     * @param eventId
     * @param bundle
     */
    public UserAdapter(Context context, List<User> users, String status, String eventId, Bundle bundle) {
        super(context, 0,users);
        this.status = status;
        this.eventId = eventId;
        this.arguments = bundle;
    // Pass the status to determine the action on click
    }

    /**
     * Provides a view for an adapter view, displaying the user's name, status,
     * and a button.
     * Sets up click listeners on the button for each user that will cancel the user if clicked
     * and draw a replacement, the cancellation will not occur if there is nobody in the waitlist
     *
     * @param position the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent the parent view that this view will eventually be attached to
     * @return the view corresponding to the data at the specified position
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.selected_list_item, parent, false);
        }

        User user = getItem(position);
        TextView eventName = convertView.findViewById(R.id.eventNameTextView);
        TextView organizerName = convertView.findViewById(R.id.organizerNameTextView);
        TextView statusTag = convertView.findViewById(R.id.statusTagTextView);
        ImageView arrowImage = convertView.findViewById(R.id.arrowImageView);

        eventName.setText(user.getName());
        organizerName.setText("User Name: " + user.getEmail());
        statusTag.setText(status);

        // Set arrow click listener based on status
        arrowImage.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            replaced = (User) bundle.getSerializable("user");

            // Load event data from passed eventId
            event = new Event(eventId);
            event.loadEventDataAsync(new Event.OnEventDataLoadedListener() {
                @Override
                public void onEventDataLoaded(Event loadedEvent) {
                    if (loadedEvent != null) {
                        waitlist = loadedEvent.getWaitlist();

                    }
                    Log.d("OrgEventSelectedLst", "Received Accepted List: ");

                    // Check if a replacement entrant can be drawn
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
