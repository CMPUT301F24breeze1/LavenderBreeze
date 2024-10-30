package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantEditProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantEditProfile extends Fragment {
    private Button doneEditButton;
    private ImageButton editPhotoButton, editNameButton, editEmailButton, editPhoneButton;
    private ImageView profilePicture;
    private TextView personName, emailAddress, contactPhoneNumber;
    private SwitchCompat emailNotificationSwitch;
    User user;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Uri imagePath;

    public EntrantEditProfile() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntrantEditProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static EntrantEditProfile newInstance(String param1, String param2) {
        EntrantEditProfile fragment = new EntrantEditProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_edit_profile, container, false);
        user = new User(getActivity());
        // Initialize views
        doneEditButton = view.findViewById(R.id.doneEdit);
        editPhotoButton = view.findViewById(R.id.editPhotoButton);
        editNameButton = view.findViewById(R.id.editNameButton);
        editEmailButton = view.findViewById(R.id.editEmailButton);
        editPhoneButton = view.findViewById(R.id.editPhoneButton);
        profilePicture = view.findViewById(R.id.profilePicture);
        personName = view.findViewById(R.id.personName);
        emailAddress = view.findViewById(R.id.emailAddress);
        contactPhoneNumber = view.findViewById(R.id.contactPhoneNumber);
        emailNotificationSwitch = view.findViewById(R.id.emailNotificationSwitch);

        doneEditButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_entrantEditProfile_to_entrantProfile3)
        );

        editPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoIntent=new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,1);
            }
        });

        editNameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadImage();
            }
        });
        //profilePicture.setImageURI(user.getProfilePicture());
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
            imagePath=data.getData();
            getImageInImageView();
            uploadImage();

        }
    }

    private void getImageInImageView() {
        Bitmap bitmap= null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        profilePicture.setImageBitmap(bitmap);
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                updateProfilePicture(task.getResult().toString());
                            }
                        }
                    });
                    Toast.makeText(getActivity(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
                else {
                   Toast.makeText(getActivity(), "Image Not Uploaded", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded " + (int)progress + "%");
            }
        });
    }

    private void updateProfilePicture(String url) {
        FirebaseDatabase.getInstance().getReference("users").child("profilePicture").setValue(url);
    }
}

// Find the button and set an onClickListener to navigate to org_event_lst.xml
//        Button profile = view.findViewById(R.id.button_go_to_entrant_profile);
//        profile.setOnClickListener(v ->
//                Navigation.findNavController(v).navigate(R.id.action_entrantEditProfile_to_entrantProfile3)
//        );