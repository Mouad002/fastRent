package com.example.fastrentv2.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fastrentv2.Functional.HomeRecyclerViewAdapter;
import com.example.fastrentv2.Model.Property;
import com.example.fastrentv2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView rv;
    private EditText tv_search;


    private DatabaseReference propertiesReference;
    private String userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // firebase stuff
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        propertiesReference = FirebaseDatabase.getInstance().getReference("properties");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rv = view.findViewById(R.id.home_rv);

        tv_search = view.findViewById(R.id.home_et_search);

//        int[] images = {R.drawable.prod1,R.drawable.prod2,R.drawable.prod3,
//                R.drawable.prod4,R.drawable.prod5,R.drawable.prod6,
//                R.drawable.prod7,R.drawable.prod8,R.drawable.prod9};
//        ArrayList<Property> list = new ArrayList<>();
//        for(int i=0;i<9;i++)
//        {
//            list.add(new Property(
//                    "katkat nadya ba9i jdida",
//                    "katkat ba9i jdida taman zwin makaydorch fso9",
//                    "Taliouin",
//                    500,
//                    images[i]
//            ));
//        }
//
//        HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(getContext(),list);
//
//        rv.setAdapter(adapter);
//
//        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<Property> properties = new ArrayList<>();

        final HomeRecyclerViewAdapter[] adapter = {null};

        // declare and show the progress dialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        propertiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item:snapshot.getChildren())
                {
                    Property p = item.getValue(Property.class);
                    properties.add(p);

                }

                adapter[0] = new HomeRecyclerViewAdapter(getContext(),properties);

                adapter[0].setOnItemClickAction(new HomeRecyclerViewAdapter.MyActionListener() {
                    @Override
                    public void onItemClickAction(Property p) {
                        showProperty(p);
                    }
                });

                rv.setAdapter(adapter[0]);

                rv.setLayoutManager(new LinearLayoutManager(getContext()));

                tv_search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                            adapter[0].getFilter().filter(s.toString());
                    }
                });

                progressDialog.dismiss(); // the progress dialog disappear
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "a problem appears while getting the properties", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss(); // the progress dialog disappear

            }
        });



        return view;
    }

    public void showProperty(Property p)
    {
        Intent intent = new Intent(getContext(),ShowPostActivity.class);
        intent.putExtra("propertyId",p.getPropertyId());
        intent.putExtra("propertyTitle",p.getPropertyTitle());
        intent.putExtra("city",p.getCity());
        intent.putExtra("price",p.getPrice());
        intent.putExtra("description",p.getDescription());
        intent.putExtra("category",p.getCategory());
        intent.putExtra("image",p.getImage());
        intent.putExtra("ownerId",p.getOwnerId());

        startActivity(intent);
    }

}