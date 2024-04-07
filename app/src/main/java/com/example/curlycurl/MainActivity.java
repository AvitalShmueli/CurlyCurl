package com.example.curlycurl;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.curlycurl.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_USER = "KEY_USER";

    //private NavController navController;
    private ActivityMainBinding binding;
    private NavController navController;
    private String connectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        Intent previousScreen = getIntent();
        connectedUser = previousScreen.getStringExtra(KEY_USER);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_profile, R.id.navigation_new_post, R.id.navigation_explore,R.id.navigation_community)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    public String getConnectedUser(){
        return connectedUser;
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        navController.navigate(R.id.navigation_explore);
    }*/

/*
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
    */
}