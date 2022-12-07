package com.example.fastrentv2.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastrentv2.Model.Person;
import com.example.fastrentv2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShowPostActivity extends AppCompatActivity {
    private ImageView iv_post,iv_back;
    private TextView tv_category,tv_title,tv_price,tv_city,tv_description,tv_email,tv_phonenumber;

    // fire base stuff
    private FirebaseAuth mAuth;
    private DatabaseReference personsReference;

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_post);

        // fire base stuff
        mAuth = FirebaseAuth.getInstance();
        personsReference = FirebaseDatabase.getInstance().getReference("persons");

        intent = getIntent();

        iv_post = findViewById(R.id.showpost_iv_post);
        tv_category = findViewById(R.id.showpost_tv_category);
        tv_title = findViewById(R.id.showpost_tv_title);
        tv_price = findViewById(R.id.showpost_tv_price);
        tv_city = findViewById(R.id.showpost_tv_city);
        tv_description = findViewById(R.id.showpost_tv_description);
        tv_email = findViewById(R.id.showpost_tv_email);
        tv_phonenumber = findViewById(R.id.showpost_tv_phonenumber);
        iv_back = findViewById(R.id.showpost_iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setValues();
    }

    private void setValues()
    {

        Picasso.get().load(intent.getStringExtra("image")).into(iv_post);
        tv_title.setText(intent.getStringExtra("propertyTitle"));
        tv_price.setText(intent.getDoubleExtra("price",0)+"");
        tv_city.setText(intent.getStringExtra("city"));
        tv_description.setText(intent.getStringExtra("description"));
        tv_category.setText(intent.getStringExtra("category"));
        personsReference.child(intent.getStringExtra("ownerId"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Person person = snapshot.getValue(Person.class);
                if(person != null)
                {
                    String email = person.getEmail();
                    String phoneNumber = person.getPhoneNumber();

                    tv_email.setText(email);
                    tv_phonenumber.setText(phoneNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowPostActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}