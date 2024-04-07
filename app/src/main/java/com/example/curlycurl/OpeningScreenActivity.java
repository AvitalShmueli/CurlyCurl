package com.example.curlycurl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OpeningScreenActivity extends AppCompatActivity {

    private MaterialButton main_BTN_login;
    private MaterialButton main_BTN_Signup;
    private ShapeableImageView main_IMG_logo;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String phone = user.getPhoneNumber();
            String name = user.getDisplayName();
            String email = user.getEmail();
            changeActivity(true, user);

        } else {
            findViews();
            Glide
                    .with(this)
                    .load(R.drawable.cropped_logo_title)
                    .fitCenter()
                    .placeholder(R.drawable.background)
                    .into(main_IMG_logo);

            main_BTN_login.setOnClickListener(view -> changeActivity(false,null));
        }
    }

    private void changeActivity(boolean isConnected,FirebaseUser user) {
        Intent destination;
        if(isConnected) {
            destination = new Intent(this, MainActivity.class);
            destination.putExtra(MainActivity.KEY_USER, user.getDisplayName());
        }
        else destination = new Intent(this, LoginActivity.class);
        startActivity(destination);
        finish();
    }

    private void findViews() {
        main_BTN_login = findViewById(R.id.main_BTN_login);
        main_BTN_Signup = findViewById(R.id.main_BTN_signup);
        main_IMG_logo = findViewById(R.id.main_IMG_logo);
    }
}