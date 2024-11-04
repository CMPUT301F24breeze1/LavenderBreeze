package com.example.myapplication.model;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import com.example.myapplication.R;
import com.example.myapplication.model.Event;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {
    private String status;

    public EventAdapter(Context context, List<Event> events, String status) {
        super(context, 0, events);
        this.status = status; // Pass the status to determine the action on click
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item, parent, false);
        }

        Event event = getItem(position);
        TextView eventName = convertView.findViewById(R.id.eventNameTextView);
        TextView organizerName = convertView.findViewById(R.id.organizerNameTextView);
        TextView statusTag = convertView.findViewById(R.id.statusTagTextView);
        ImageView arrowImage = convertView.findViewById(R.id.arrowImageView);

        eventName.setText(event.getEventName());
        organizerName.setText("Organized by: " + event.getOrganizerId());
        statusTag.setText(status);

        // Set arrow click listener based on status
        arrowImage.setOnClickListener(v -> {
            Integer actionId = null;
            switch (status) {
                case "Selected":
                    //actionId = R.id.action_selectedList_to_declineInvitationFragment;
                    break;
                case "Requested":
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("event", event);
                    Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantLeavePage2,bundle);
                    //actionId = R.id.action_entrantEventsList_to_entrantLeavePage2;
                    break;
                case "Cancelled":
                    //actionId = R.id.action_cancelledList_to_rejoinFragment;
                    break;
                case "Accepted":
                    //actionId = R.id.action_acceptedList_to_leaveEventFragment;
                    break;
                default:
                    actionId = R.id.action_entrantEventsList_to_home;
                    break;
            }
            //Navigation.findNavController(v).navigate(actionId);
        });

        return convertView;
    }
}
