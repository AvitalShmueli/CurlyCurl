package com.example.curlycurl;

import android.os.Bundle;

import com.example.curlycurl.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.curlycurl.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private User connectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();



        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_profile, R.id.navigation_new_post, R.id.navigation_explore, R.id.navigation_community)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //navView.setOnItemSelectedListener(this::onItemSelectedListener);



    }


    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_UUID, connectedUser.getUuid());
        outState.putString(KEY_USERNAME, connectedUser.getUsername());
    }
*/


    /*
    private boolean onItemSelectedListener(MenuItem item){
        if (item.getItemId() == R.id.navigation_profile) {
            Fragment frg = ProfileFragment.newInstance(connectedUser);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, frg)
                    .commit();
            return true;
        }
        throw new IllegalArgumentException("item not implemented : " + item.getItemId());

    }
    */
}