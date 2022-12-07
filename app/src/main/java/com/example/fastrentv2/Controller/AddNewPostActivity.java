package com.example.fastrentv2.Controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fastrentv2.Functional.Helper;
import com.example.fastrentv2.Model.Person;
import com.example.fastrentv2.Model.Property;
import com.example.fastrentv2.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AddNewPostActivity extends AppCompatActivity {
    // the component
    private ImageView iv_back;
    private ShapeableImageView siv_picture;
    private EditText et_title,et_price,et_city,et_description;
    private AppCompatButton acb_publish;
    private AutoCompleteTextView actv_cities,actv_categories;

    // functional variables
    private Uri uri;
    private boolean isImageInserted = false;

    // fire base stuff
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference propertiesReference;
    private DatabaseReference personsReference;

    // progress dialog
    private ProgressDialog progressDialog;

    private Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        helper = new Helper(AddNewPostActivity.this);

        // fire base stuff
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("properties");
        propertiesReference = FirebaseDatabase.getInstance().getReference("properties");
        personsReference = FirebaseDatabase.getInstance().getReference("persons");

        // initilize component
        iv_back = findViewById(R.id.addnewpost_iv_back);
        siv_picture = findViewById(R.id.addnewpost_siv_picture);
        et_title = findViewById(R.id.addnewpost_et_title);
        et_price = findViewById(R.id.addnewpost_et_price);
        actv_cities = findViewById(R.id.addnewpost_actv_city);
        actv_categories = findViewById(R.id.addnewpost_actv_category);
        et_description = findViewById(R.id.addnewpost_et_description);
        acb_publish = findViewById(R.id.addnewpost_acb_publish);

        setcombobox();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        siv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pictureAction();
            }
        });

        acb_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000)
        {
            if(resultCode==RESULT_OK)
            {
                uri = data.getData();
                siv_picture.setImageURI(uri);
                isImageInserted = true;
            }
        }
    }

    private void pictureAction()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,2000);
    }

    private void publish()
    {
        String title = et_title.getText().toString().trim();
        String price = et_price.getText().toString().trim();
        String city = actv_cities.getText().toString().trim();
        String description = et_description.getText().toString().trim();
        String category = actv_categories.getText().toString().trim();

        if(title.isEmpty())
        {
            et_title.setError("title is required!");
            et_title.requestFocus();
            return;
        }

        if(price.isEmpty())
        {
            et_price.setError("price is required!");
            et_price.requestFocus();
            return;
        }

        if(city.isEmpty())
        {
            actv_cities.setError("city is required!");
            actv_cities.requestFocus();
            return;
        }

        if(description.isEmpty())
        {
            et_description.setError("description is required!");
            et_description.requestFocus();
            return;
        }

        if(category.isEmpty())
        {
            actv_categories.setError("category is required!");
            actv_categories.requestFocus();
            return;
        }

        if(!isImageInserted)
        {
            Toast.makeText(this, "the image is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // progress bar
        progressDialog = new ProgressDialog(AddNewPostActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); // the progress dialog appear
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Property newProperty = new Property(
                UUID.randomUUID().toString(),
                null,
                city,
                Double.parseDouble(price),
                description,
                category,
                null,
                mAuth.getCurrentUser().getUid()
        );

        newProperty.setPropertyTitle(newProperty.getCategory()+" : "+title);
        if(uri != null)
        {
            StorageReference reference = storageReference.child(UUID.randomUUID().toString()+".jpg");
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl()
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

//                                    Toast.makeText(AddNewPostActivity.this, "the image was uploaded", Toast.LENGTH_SHORT).show();

                                    String imageUrl = task.getResult().toString();
                                    newProperty.setImage(imageUrl);
                                    propertiesReference.child(newProperty.getPropertyId())
                                            .setValue(newProperty)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    Toast.makeText(AddNewPostActivity.this, "property was uploaded", Toast.LENGTH_SHORT).show();
                                                    uploadData(newProperty.getPropertyId());
                                                }
                                            }).addOnCanceledListener(new OnCanceledListener() {
                                                @Override
                                                public void onCanceled() {
                                                    Toast.makeText(AddNewPostActivity.this, "a problem appears while uploading the property", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddNewPostActivity.this, "a problem while getting the image", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(AddNewPostActivity.this, "a problem while uploading the image", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else
        {
            Toast.makeText(this, "uri is null", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }



    private void uploadData(String propertyId)
    {
        personsReference.child(mAuth.getCurrentUser().getUid()).child("properties")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> properties = new ArrayList();

                        for(DataSnapshot item:snapshot.getChildren())
                        {
                            properties.add(item.getValue(String.class));
                        }

                        properties.add(propertyId);


                        HashMap data = new HashMap();

                        data.put(Person.propertiesField,properties);

                        personsReference.child(mAuth.getCurrentUser().getUid())
                                .updateChildren(data)
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(AddNewPostActivity.this, "your added a new post successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                            progressDialog.dismiss();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddNewPostActivity.this, "a problem apear in updating your information", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

    private void setcombobox()
    {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(AddNewPostActivity.this,R.layout.cmb_text_view,helper.getCities());
        actv_cities.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(AddNewPostActivity.this,R.layout.cmb_text_view,helper.getCategories());
        actv_categories.setAdapter(adapter2);
    }
}