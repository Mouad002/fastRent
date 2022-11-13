package com.example.fastrentv2.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastrentv2.Model.Person;
import com.example.fastrentv2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {
    private View view;
    private TextView tv_fullName,tv_email,tv_phoneNumber,tv_city,tv_updateProfile,tv_signOut;
    private CircleImageView civ_profilePic;

    // firebase stuff
    private FirebaseUser user;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // firebase stuff
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Persons");
        storageReference = FirebaseStorage.getInstance().getReference("profile/"+userId+".jpg");


        view = inflater.inflate(R.layout.fragment_account, container, false);

        tv_fullName = view.findViewById(R.id.account_tv_fullname);
        tv_email = view.findViewById(R.id.account_tv_email);
        tv_phoneNumber = view.findViewById(R.id.account_tv_phonenumber);
        tv_city = view.findViewById(R.id.account_tv_city);
        tv_updateProfile = view.findViewById(R.id.account_tv_updateprofile);
        tv_signOut = view.findViewById(R.id.account_tv_signout);
        civ_profilePic = view.findViewById(R.id.account_civ_profilepic);

        tv_updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),EditProfileActivity.class));
            }
        });

        tv_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(),SignInActivity.class));
                getActivity().finish();
            }
        });

        // declare and show the progress dialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Person person = snapshot.getValue(Person.class);
                if(person != null)
                {
                    String fullname = person.getFullName();
                    String email = person.getEmail();
                    String phoneNumber = person.getPhoneNumber();
                    String city = person.getCity();

                    tv_fullName.setText(fullname);
                    tv_email.setText(email);
                    tv_phoneNumber.setText(phoneNumber);
                    tv_city.setText(city);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        try {
            File localFile = File.createTempFile("tempFile",".jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            civ_profilePic.setImageBitmap(bitmap);
                            progressDialog.dismiss(); // the progress dialog disappear

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss(); // the progress dialog disappear
                            Toast.makeText(getContext(), "can't load the image", Toast.LENGTH_SHORT).show();
                        }
                    });


        } catch (IOException e) {
            e.printStackTrace();
        }


        // Inflate the layout for this fragment
        return view;
    }
}