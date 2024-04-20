package com.example.curlycurl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private LottieAnimationView lottie_LOTTIE_animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();

        findViews();
        lottie_LOTTIE_animation.resumeAnimation();
        lottie_LOTTIE_animation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                TransactToOpeningScreenActivity();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }

    private void findViews() {
        lottie_LOTTIE_animation = findViewById(R.id.lottie_LOTTIE_animation);
    }

    private void TransactToOpeningScreenActivity() {
        startActivity(new Intent(this,OpeningScreenActivity.class));
        finish();
    }
}