package com.example.curlycurl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class OpeningScreenActivity extends AppCompatActivity {

    private MaterialButton main_BTN_login;
    private MaterialButton main_BTN_Signup;
    private ShapeableImageView main_IMG_logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);
        getSupportActionBar().hide();
        findViews();

        Glide
                .with(this)
                .load(R.drawable.cropped_logo_title)
                .centerCrop()
                .placeholder(R.drawable.background)
                .into(main_IMG_logo);

        main_BTN_login.setOnClickListener(view -> changeActivity());
    }

    private void changeActivity() {
        Intent MainActivity = new Intent(this, MainActivity.class);
        startActivity(MainActivity);
        finish();
    }

    private void findViews() {
        main_BTN_login = findViewById(R.id.main_BTN_login);
        main_BTN_Signup = findViewById(R.id.main_BTN_signup);
        main_IMG_logo = findViewById(R.id.main_IMG_logo);
    }
}