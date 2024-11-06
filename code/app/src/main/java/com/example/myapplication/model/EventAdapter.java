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
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            switch (status) {
                case "Selected":
                    Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantSelectedPage,bundle);
                    break;
                case "Requested":
                    Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantLeavePage2,bundle);
                    break;
                case "Cancelled":
                    Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entranteventdescription,bundle);
                    break;
                case "Accepted":
                    Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entranteventdescription,bundle);
                    break;
                default:
                    Navigation.findNavController(v).navigate(R.id.action_entrantEventsList_to_entrantEventPage);
                    break;
            }
            //Navigation.findNavController(v).navigate(actionId);
        });

        return convertView;
    }
}
