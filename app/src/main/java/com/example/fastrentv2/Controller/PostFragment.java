package com.example.fastrentv2.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fastrentv2.Functional.HomeRecyclerViewAdapter;
import com.example.fastrentv2.Functional.PostRecyclerViewAdapter;
import com.example.fastrentv2.Model.Person;
import com.example.fastrentv2.Model.Property;
import com.example.fastrentv2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class PostFragment extends Fragment {
    private View view;

    private ImageView iv_add;
    private RecyclerView rv;

    // firebase stuff
    private DatabaseReference personsReference;
    private DatabaseReference propertiesReference;
    private String userId;

    // progress dialog
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // firebase stuff
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        personsReference = FirebaseDatabase.getInstance().getReference("persons");
        propertiesReference = FirebaseDatabase.getInstance().getReference("properties");

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post, container, false);

        iv_add = view.findViewById(R.id.post_iv_add);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AddNewPostActivity.class));
            }
        });

        rv = view.findViewById(R.id.post_rv);

        ArrayList<String> propertiesId = new ArrayList<>();

        ArrayList<Property> properties = new ArrayList<>();

        // declare and show the progress dialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        personsReference.child(userId).child("properties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item:snapshot.getChildren())
                {
                    propertiesId.add(item.getValue(String.class));
                }
                propertiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot item:snapshot.getChildren())
                        {

                            Property p = item.getValue(Property.class);
                            if(propertiesId.contains(p.getPropertyId()))
                            {
                                properties.add(p);
                            }
                        }

                        PostRecyclerViewAdapter adapter = new PostRecyclerViewAdapter(getContext(),properties);

                        adapter.setOnItemClickAction(new PostRecyclerViewAdapter.MyActionListener() {
                            @Override
                            public void onItemClickAction(Property p, int action) {
                                switch(action)
                                {
                                    case 1:
                                        showProperty(p);
                                        break;
                                    case 2:
                                        editProperty(p);
                                        break;
                                    case 3:
                                        deleteProperty(p);
                                        break;
                                }
                            }
                        });

                        rv.setAdapter(adapter);

                        rv.setLayoutManager(new LinearLayoutManager(getContext()));

                        progressDialog.dismiss(); // the progress dialog disappear
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "a problem appears while getting the properties", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss(); // the progress dialog disappear

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "a problem appears while getting the list of the properties", Toast.LENGTH_SHORT).show();
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

    private void editProperty(Property p)
    {
        Intent intent = new Intent(getContext(),EditPostActivity.class);
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

    private void deleteProperty(Property p)
    {
        // progress bar
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); // the progress dialog appear
        progressDialog.setContentView(R.layout.custom_progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        int[] tempInt = new int[1];
        String[] tempString = {null};
        propertiesReference
                .child(p.getPropertyId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    personsReference
                            .child(p.getOwnerId())
                            .child("properties")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ArrayList<String> list = new ArrayList();
                                    for(DataSnapshot data:snapshot.getChildren())
                                    {
                                        list.add(data.getValue(String.class));
                                    };
                                    list.remove(p.getPropertyId());
                                    HashMap properties = new HashMap();
                                    properties.put(Person.propertiesField,list);
                                    personsReference
                                            .child(p.getOwnerId())
                                            .updateChildren(properties)
                                            .addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    Toast.makeText(getContext(), "your post was deleted successfully", Toast.LENGTH_SHORT).show();
                                                    StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(p.getImage());
                                                    imgRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getContext(), "the post with the image were deleted successfully", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                                }
                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "something wrong happened while deleting the post", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            }
        });
    }
}