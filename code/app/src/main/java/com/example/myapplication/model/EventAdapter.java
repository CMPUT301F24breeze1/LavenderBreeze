// From chatgpt, openai, "write a java implementation with java documentation of EventAdapter
//class with methods to show the event details and option to navigate to the event
//given here is the xml code for it", 2024-11-02
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
/**
 * Custom ArrayAdapter to display a list of Event objects in a ListView. It shows each event's name,
 * organizer, and status, and provides navigation based on the event's status.
 */
public class EventAdapter extends ArrayAdapter<Event> {
    private String status;
    /**
     * Constructs an EventAdapter.
     * @param context the context in which the adapter is being used
     * @param events a list of Event objects to be displayed
     * @param status the status of the events, which determines the navigation action when an event is clicked
     */
    public EventAdapter(Context context, List<Event> events, String status) {
        super(context, 0, events);
        this.status = status; // Pass the status to determine the action on click
    }
    /**
     * Provides a view for an adapter view, displaying the event name, organizer, and status.
     * Sets up click listeners on the navigation arrow to direct the user to a different
     * screen based on the event's status.
     * @param position the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent the parent view that this view will eventually be attached to
     * @return the view corresponding to the data at the specified position
     */
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
