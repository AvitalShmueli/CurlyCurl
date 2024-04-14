package com.example.curlycurl;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            login();
        } else {
            changeActivity(MainActivity.class);
        }

    }

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void login() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                //new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
                //new AuthUI.IdpConfig.FacebookBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                //.setTheme(R.style.LoginUIStyle)
                //.setLogo(R.drawable.full_logo_title)
                .setIsSmartLockEnabled(false)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            int x=1;
            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user = mAuth.getCurrentUser();
            if (user != null) {
                long creationTimestamp = Objects.requireNonNull(user.getMetadata()).getCreationTimestamp();
                long lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
                if (creationTimestamp == lastSignInTimestamp) {
                    //do create new user
                    FirebaseManager.getInstance().createUsersProfileInDB(user);
                    changeActivity(EditProfileInfoActivity.class);
                    //createUsersProfileInDB(user);
                } else {
                    //user is exists, just do login
                    changeActivity(MainActivity.class);
                }
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            changeActivity(OpeningScreenActivity.class);
        }


    }


    /*
    private void createUsersProfileInDB(FirebaseUser user){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User newUserProfile = new User().
                setUsername(user.getDisplayName())
                .setEmail(user.getEmail())
                .setUuid(user.getUid());

        db.collection("users").document(user.getUid()).set(newUserProfile);
        changeActivity(EditProfileInfoActivity.class);

    }*/


    private void changeActivity(Class<?> cls) {
        Intent destination = new Intent(this, cls);
        startActivity(destination);
        finish();
    }
}