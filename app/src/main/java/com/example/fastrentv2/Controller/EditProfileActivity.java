package com.example.fastrentv2.Controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fastrentv2.Functional.Helper;
import com.example.fastrentv2.Model.Person;
import com.example.fastrentv2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView iv_back;
    private CircleImageView civ_profilePic;
    private TextInputEditText tiet_fullName,tiet_phoneNumber;
    private AutoCompleteTextView actv_cities;
    private AppCompatButton acb_updateProfile;

    // progress dialog
    private ProgressDialog progressDialog;

    // picture stuff
    private Uri uri;
    private boolean isImageInserted = false;

    // fire base stuff
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference realTimeReference;

    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        helper = new Helper(EditProfileActivity.this);

        // fire base stuff
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("persons");
        realTimeReference = FirebaseDatabase.getInstance().getReference("persons");

        iv_back = findViewById(R.id.editprofile_iv_back);
        civ_profilePic = findViewById(R.id.editprofile_civ_profilepic);
        tiet_fullName = findViewById(R.id.editprofile_tiet_fullname);
        tiet_phoneNumber = findViewById(R.id.editprofile_tiet_phonenumber);
        actv_cities = findViewById(R.id.editprofile_actv_city);
        acb_updateProfile = findViewById(R.id.editprofile_acb_updateprofile);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        civ_profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,1000);
            }
        });

        acb_updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });

        setcombobox();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                uri = data.getData();
                civ_profilePic.setImageURI(uri);
                isImageInserted = true;
            }
        }
    }

    private void uploadTheNewImage()
    {
        if(uri != null)
        {
            StorageReference fileRef = storageReference.child(mAuth.getCurrentUser().getUid() +".jpg");
            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditProfileActivity.this, "image uploaded successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "a problem appears in uploading your image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadData()
    {

        String fullNameText = tiet_fullName.getEditableText().toString().trim();
        String cityText = actv_cities.getEditableText().toString().trim();
        String phoneNumberText = tiet_phoneNumber.getEditableText().toString().trim();

        if(fullNameText.isEmpty())
        {
            tiet_fullName.setError("full name is required!");
            tiet_fullName.requestFocus();
            return;
        }

        if(cityText.isEmpty())
        {
            actv_cities.setError("city is required!");
            actv_cities.requestFocus();
            return;
        }

        if(phoneNumberText.isEmpty())
        {
            tiet_phoneNumber.setError("phone number is required!");
            tiet_phoneNumber.requestFocus();
            return;
        }

        if(!isImageInserted)
        {
            Toast.makeText(this, "insert new image before update the profile", Toast.LENGTH_SHORT).show();
            return;
        }

        // progress bar
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); // the progress dialog appear
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Person person = new Person(mAuth.getCurrentUser().getUid(),
                tiet_fullName.getText().toString().trim(),
                actv_cities.getText().toString().trim(),
                tiet_phoneNumber.getText().toString().trim(),
                null,
                null);

        HashMap data = new HashMap<>();

        data.put(Person.fullNameField,person.getFullName());
        data.put(Person.cityField,person.getCity());
        data.put(Person.phoneNumberField,person.getPhoneNumber());

        realTimeReference.child(person.getPersonId())
                .updateChildren(data)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            uploadTheNewImage();
                            Toast.makeText(EditProfileActivity.this, "your informations were updated successfuly", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "a problem apear in updating your information", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void setcombobox()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditProfileActivity.this,R.layout.cmb_text_view,helper.getCities());
        actv_cities.setAdapter(adapter);
    }
}