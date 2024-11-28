package com.example.myapplication.controller;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.model.User;

import java.util.ArrayList;

/**
 * A {@link ArrayAdapter<Bundle>} subclass.
 * Use the {@link UserAdapter(Context, ArrayList)} constructor to
 * create an instance of this fragment.
 *
 * This Class is not yet implemented, so there is no Javadoc for the methods within
 */
public class UserListAdapter extends ArrayAdapter<User> {

    private ArrayList<User> users;
    private Context context;

    public UserListAdapter(Context context, ArrayList<User> users){
        super(context,0,users);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.users_list_content, parent, false);
        }

        User user = users.get(position);
        String userStatus;
        LinearLayout background = view.findViewById(R.id.user_background);
        if(user.getIsAdmin()) {
            userStatus = "Admin";
            background.setBackgroundResource(R.drawable.rounded_box_red);
        } else if (user.getIsOrganizer()) {
            userStatus = "Organizer";
            background.setBackgroundResource(R.drawable.rounded_box_yellow);
        } else {
            userStatus = "Entrant";
            background.setBackgroundResource(R.drawable.rounded_box_green);
        }

        ImageView profilePicture = view.findViewById(R.id.user_profile_picture);
        TextView name = view.findViewById(R.id.user_name);
        TextView status = view.findViewById(R.id.user_status);
        TextView email = view.findViewById((R.id.user_email));
        TextView phone1 = view.findViewById(R.id.user_phone_1);
        TextView phone2 = view.findViewById(R.id.user_phone_2);
        TextView phone3 = view.findViewById(R.id.user_phone_3);

        user.loadProfilePictureInto(profilePicture,getContext());
        name.setText(user.getName());
        status.setText(userStatus);
        email.setText(user.getEmail());
        phone1.setText("("+user.getPhoneNumber().substring(+0,3)+")- ");
        phone2.setText(user.getPhoneNumber().substring(3,6)+"- ");
        phone3.setText(user.getPhoneNumber().substring(6,10));

        //Log.d("Kenny", event.getEventName());
        return view;
    }
}
