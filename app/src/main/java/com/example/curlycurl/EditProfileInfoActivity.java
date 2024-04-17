package com.example.curlycurl;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.curlycurl.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Objects;


public class EditProfileInfoActivity extends AppCompatActivity {
    private TextInputEditText editProfile_TXT_userName, editProfile_TXT_email, editProfile_TXT_city;
    private ShapeableImageView editProfile_IMG_ImageView;
    private MaterialButton editProfile_BTN_selectImage, editProfile_BTN_save;
    private AutoCompleteTextView editProfile_DD_curlType;
    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseStorage storage;
    private Uri imageUri;
    private ArrayList<String> itemsCurlType;
    private User.CurlType selectedCurlType = null;

    private ProgressBar editProfile_progressBar;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_info);
        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseManager = FirebaseManager.getInstance();
        storage = FirebaseStorage.getInstance();

        findViews();
        initDropDownValues();
        initViews();

    }

    private void initDropDownValues() {
        itemsCurlType = new ArrayList<>();
        for (User.CurlType curlType : User.CurlType.values()) {
            itemsCurlType.add(curlType.name().substring(1)); // remove the _ in display
        }
        Log.d(TAG, "dropdown items (curl type) = " + itemsCurlType.toString());
    }

    private void findViews() {
        editProfile_TXT_userName = findViewById(R.id.editProfile_TXT_userName);
        editProfile_TXT_email = findViewById(R.id.editProfile_TXT_email);
        editProfile_TXT_city = findViewById(R.id.editProfile_TXT_city);
        editProfile_DD_curlType = findViewById(R.id.editProfile_DD_curlType);
        editProfile_IMG_ImageView = findViewById(R.id.editProfile_IMG_ImageView);
        editProfile_BTN_selectImage = findViewById(R.id.editProfile_BTN_selectImage);
        editProfile_BTN_save = findViewById(R.id.editProfile_BTN_save);
        editProfile_progressBar = findViewById(R.id.editProfile_progressBar);
    }

    private void initViews() {
        editProfile_progressBar.setVisibility(View.VISIBLE);
        editProfile_TXT_userName.addTextChangedListener(postWatcher);
        editProfile_TXT_email.addTextChangedListener(postWatcher);
        editProfile_TXT_city.addTextChangedListener(postWatcher);

        editProfile_TXT_userName.setOnFocusChangeListener(focusChangeListener);
        editProfile_TXT_email.setOnFocusChangeListener(focusChangeListener);
        editProfile_TXT_city.setOnFocusChangeListener(focusChangeListener);

        ArrayAdapter<String> adapterItems_curlType = new ArrayAdapter<>(this, R.layout.dropdown_item, itemsCurlType);
        editProfile_DD_curlType.setAdapter(adapterItems_curlType);
        editProfile_DD_curlType.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = "_"+adapterView.getItemAtPosition(position).toString();
            selectedCurlType = User.CurlType.valueOf(item);
        });


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
        editProfile_progressBar.setVisibility(View.GONE);
    }

    private void updateProfile() {
        String strUsername = String.valueOf(editProfile_TXT_userName.getEditableText());
        String strEmail = String.valueOf(editProfile_TXT_email.getEditableText());
        String strCity =  String.valueOf(editProfile_TXT_city.getEditableText());

        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        User user = new User()
                .setUserId(mUser.getUid())
                .setUsername(strUsername)
                .setEmail(strEmail)
                .setCity(strCity)
                .setCurlType(selectedCurlType);
        if (imageUri != null) {
            uploadToFirebase(imageUri, user);
        } else {
            FirebaseManager.getInstance().updateUserProfile(user);
            changeActivity();
        }
    }

    private void uploadToFirebase(Uri uri, User user) {
        StorageReference fileRef = storage.getReference().child("profile_images").child(user.getUserId() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        user.setImageURL(uri.toString());
                        firebaseManager.updateUserProfile(user);
                        toast("Uploaded successfully");
                        editProfile_progressBar.setVisibility(View.GONE);
                        changeActivity();
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                editProfile_progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("Uploading failed");
                editProfile_progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Uploading failed | " + uri);
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
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

    private void pickImage() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && android.os.ext.SdkExtensions.getExtensionVersion(android.os.Build.VERSION_CODES.R) >= 2) {
            intent = new Intent((MediaStore.ACTION_PICK_IMAGES));
            resultLauncher.launch(intent);
        }
    }

    private void clearImage() {
        editProfile_IMG_ImageView.setImageURI(null);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    TextWatcher postWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String strUsername = editProfile_TXT_userName.getEditableText().toString();
            String strEmail = editProfile_TXT_email.getEditableText().toString();

            editProfile_BTN_save.setEnabled(!strUsername.isEmpty() && !strEmail.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };
    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        clearImage();
    }
}