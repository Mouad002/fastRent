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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EditPostActivity extends AppCompatActivity {
    // the component
    private ImageView iv_back;
    private ShapeableImageView siv_picture;
    private EditText et_title,et_price,et_city,et_description;
    private AppCompatButton acb_edit;
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

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        helper = new Helper(EditPostActivity.this);

        intent = getIntent();

        // fire base stuff
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("properties");
        propertiesReference = FirebaseDatabase.getInstance().getReference("properties");
        personsReference = FirebaseDatabase.getInstance().getReference("persons");

        // initilize component
        iv_back = findViewById(R.id.editpost_iv_back);
        siv_picture = findViewById(R.id.editpost_siv_picture);
        et_title = findViewById(R.id.editpost_et_title);
        et_price = findViewById(R.id.editpost_et_price);
        actv_cities = findViewById(R.id.editpost_actv_city);
        actv_categories = findViewById(R.id.editpost_actv_category);
        et_description = findViewById(R.id.editpost_et_description);
        acb_edit = findViewById(R.id.editpost_acb_edit);


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

        acb_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }
        });

        et_title.setText(intent.getStringExtra("propertyTitle"));
        et_price.setText(intent.getDoubleExtra("price",0)+"");
        actv_cities.setText(intent.getStringExtra("city"));
        et_description.setText(intent.getStringExtra("description"));
        actv_categories.setText(intent.getStringExtra("category"));

        setcombobox();
    }

    private void setcombobox()
    {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(EditPostActivity.this,R.layout.cmb_text_view,helper.getCities());
        actv_cities.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(EditPostActivity.this,R.layout.cmb_text_view,helper.getCategories());
        actv_categories.setAdapter(adapter2);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3000)
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
        startActivityForResult(galleryIntent,3000);
    }

    private void edit()
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
        progressDialog = new ProgressDialog(EditPostActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); // the progress dialog appear
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String propertyId = intent.getStringExtra("propertyId");
        String lastImage = intent.getStringExtra("image");

        Property newProperty = new Property(
                propertyId,
                title,
                city,
                Double.parseDouble(price),
                description,
                category,
                null,
                mAuth.getCurrentUser().getUid()
        );

        if(uri != null)
        {
            StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(lastImage);
            imgRef.delete();

            StorageReference reference = storageReference.child(propertyId+".jpg");
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl()
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String imageUrl = task.getResult().toString();

                                    HashMap data = new HashMap();
                                    data.put("propertyId",newProperty.getPropertyId());
                                    data.put("propertyTitle",newProperty.getCategory()+" : "+newProperty.getPropertyTitle());
                                    data.put("city",newProperty.getCity());
                                    data.put("price",newProperty.getPrice());
                                    data.put("description",newProperty.getDescription());
                                    data.put("category",newProperty.getCategory());
                                    data.put("image",imageUrl);
                                    data.put("ownerId",newProperty.getOwnerId());

                                    propertiesReference.child(newProperty.getPropertyId())
                                            .updateChildren(data)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(EditPostActivity.this, "property was modified", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnCanceledListener(new OnCanceledListener() {
                                                @Override
                                                public void onCanceled() {
                                                    Toast.makeText(EditPostActivity.this, "a problem appears while modifying the property", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditPostActivity.this, "a problem while getting the image", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(EditPostActivity.this, "a problem while uploading the image", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else
        {
            Toast.makeText(this, "uri is null", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }


}