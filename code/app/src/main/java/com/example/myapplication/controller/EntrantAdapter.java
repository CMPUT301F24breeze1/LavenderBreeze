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
import com.example.myapplication.model.User;

import java.util.List;
/**
 * A {@link ArrayAdapter< User >} subclass.
 * Use the {@link UserAdapter(Context, List)} constructor to
 * create an instance of this fragment.
 */
public class EntrantAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> entrants; // List of entrants

    /**
     * Constructs an an instance of the Entrant Adapter with a list of users
     *
     * @param context
     * @param entrants
     */    public EntrantAdapter(@NonNull Context context, List<User> entrants) {
        super(context, 0, entrants);
        this.context = context;
        this.entrants = entrants;
    }

    /**
     * Provides a view for an adapter view, displaying the user's name
     *
     * @param position the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent the parent view that this view will eventually be attached to
     * @return the view corresponding to the data at the specified position
     */    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            // Inflate the custom layout for each list item
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_waitlist_entrant, parent, false);
        }

        // Get the User (entrant) object for this position
        User entrant = getItem(position);

        // Find the TextViews in the custom layout
        TextView nameTextView = convertView.findViewById(R.id.entrantName);

        // Populate the TextViews with the User data
        if (entrant != null) {
            nameTextView.setText(entrant.getName());;
        }

        // Return the completed view to be displayed in the ListView
        return convertView;
    }
}