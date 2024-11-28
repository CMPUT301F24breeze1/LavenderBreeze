package com.example.myapplication.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.model.Event;

import java.util.ArrayList;

/**
 * A {@link ArrayAdapter<Event>} subclass.
 * Use the {@link UserAdapter(Context, ArrayList)} constructor to
 * create an instance of this fragment.
 */
public class EventsListAdapter extends ArrayAdapter<com.example.myapplication.model.Event> {
    private ArrayList<com.example.myapplication.model.Event> events;
    private Context context;
    private boolean isAdmin;

    /**
     * Constructs an EventAdapter.
     * @param context the context in which the adapter is being used
     * @param events a list of Event objects to be displayed
     */
    public EventsListAdapter(Context context, ArrayList<com.example.myapplication.model.Event> events, boolean isAdmin){
        super(context, 0, events);
        this.events = events;
        this.context = context;
        this.isAdmin = isAdmin;
    }
    /**
     * Provides a view for an adapter view, displaying the Event name
     *
     * @param position the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent the parent view that this view will eventually be attached to
     * @return the view corresponding to the data at the specified position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null && isAdmin) {
            view = LayoutInflater.from(context).inflate(R.layout.events_list_content, parent, false);
        } else if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.events_list_content_organizer,parent,false);
        }

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.event_name);

        eventName.setText(event.getEventName());
        //Log.d("Kenny", event.getEventName());

        return view;
    }
}
