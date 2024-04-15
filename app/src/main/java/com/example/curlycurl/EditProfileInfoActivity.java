package com.example.curlycurl;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


import com.example.curlycurl.Models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;


public class EditProfileInfoActivity extends AppCompatActivity {
    private TextInputEditText editProfile_TXT_userName;
    private TextInputEditText editProfile_TXT_email;
    private ShapeableImageView editProfile_IMG_ImageView;
    private MaterialButton editProfile_BTN_selectImage;
    private MaterialButton editProfile_BTN_save;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    ActivityResultLauncher<Intent> resultLauncher;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_info);
        Objects.requireNonNull(getSupportActionBar()).hide();

        findViews();
        initViews();

    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            registerResult();
            editProfile_TXT_userName.setText(mUser.getDisplayName());
            editProfile_TXT_email.setText(mUser.getEmail());
            editProfile_BTN_selectImage.setOnClickListener(v -> {
                //upload photo from gallery
                pickImage();
            });
            editProfile_BTN_save.setOnClickListener(v -> updateProfile());

        }
    }

    private void pickImage() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && android.os.ext.SdkExtensions.getExtensionVersion(android.os.Build.VERSION_CODES.R) >= 2) {
            intent = new Intent((MediaStore.ACTION_PICK_IMAGES));
            resultLauncher.launch(intent);
        }
    }


    private void updateProfile() {
        String strUsername = String.valueOf(editProfile_TXT_userName.getEditableText());
        String strEmail = String.valueOf(editProfile_TXT_email.getEditableText());
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        FirebaseManager.getInstance().updateUserProfile(
                new User()
                        .setUserId(mUser.getUid())
                        .setUsername(strUsername)
                        .setEmail(strEmail)
                        .setCity("Ramat-Gan")
                        .setCurlType(User.CurlType._3A)
        );


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String file_name = "user_profile_" + mUser.getUid() + ".jpg";
        StorageReference profileRef = storageRef.child(file_name);
        StorageReference profileImagesRef = storageRef.child("profile_images/" + file_name);

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .setCustomMetadata("ownerUID", mUser.getUid())
                .build();

        UploadTask uploadTask = storageRef.child("profile_images/" + imageUri.getLastPathSegment()).putFile(imageUri, metadata);
        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                // ...
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return profileImagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d(TAG, "download URL: " + downloadUri);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });


        changeActivity();
    }


    private void findViews() {

        editProfile_TXT_userName = findViewById(R.id.editProfile_TXT_userName);
        editProfile_TXT_email = findViewById(R.id.editProfile_TXT_email);
        editProfile_IMG_ImageView = findViewById(R.id.editProfile_IMG_ImageView);
        editProfile_BTN_selectImage = findViewById(R.id.editProfile_BTN_selectImage);
        editProfile_BTN_save = findViewById(R.id.editProfile_BTN_save);

    }

    private void changeActivity() {
        Intent destination = new Intent(this, MainActivity.class);
        startActivity(destination);
        finish();
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            imageUri = result.getData().getData();
                            editProfile_IMG_ImageView.setImageURI(imageUri);
                            editProfile_BTN_selectImage.setText(R.string.change_picture);

                        } catch (Exception e) {
                            Toast.makeText(EditProfileInfoActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                            editProfile_IMG_ImageView.setImageURI(null);
                        }
                    }
                }
        );
    }
}