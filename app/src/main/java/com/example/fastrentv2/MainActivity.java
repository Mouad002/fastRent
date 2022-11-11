package com.example.fastrentv2;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fastrentv2.Model.AccountFragment;
import com.example.fastrentv2.View.HomeFragment;
import com.example.fastrentv2.View.PostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // the buttom navigation menu
        BottomNavigationView bnv = findViewById(R.id.navigation_bar);

        // initilize the fragments
        Fragment home = new HomeFragment();
        Fragment post = new PostFragment();
        Fragment account = new AccountFragment();

        bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = account;
                switch(item.getItemId())
                {
                    case R.id.nav_home:
                        fragment = home;
                        break;
                    case R.id.nav_post:
                        fragment = post;
                        break;
                    case R.id.nav_account:
                        fragment = account;
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragments_container,fragment).commit();
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragments_container,home).commit();
    }
}



