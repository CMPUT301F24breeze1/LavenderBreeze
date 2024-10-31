package com.example.myapplication.entrant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.ArrayList;

public class EventsListAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public EventsListAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
        this.events = events;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.events_list_content, parent, false);
        }

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.event_name);

        eventName.setText(event.getEventName());

        return view;
    }
}
