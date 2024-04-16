package com.example.curlycurl.ui.new_community_post;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.curlycurl.FirebaseManager;
import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentNewCommunityPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class NewCommunityPostFragment extends Fragment {
    private FragmentNewCommunityPostBinding binding;
    private FirebaseManager firebaseManager;
    private FirebaseStorage storage;
    private User connectedUser;
    private ActivityResultLauncher<Intent> resultLauncher;
    private MaterialButton communityPost_BTN_addTags, communityPost_BTN_selectImage, communityPost_BTN_removeImage, communityPost_BTN_addLocation, communityPost_BTN_post;
    private TextInputEditText communityPost_TXT_post;
    private ShapeableImageView communityPost_IMG_back, communityPost_IMG_ImageView;
    private ProgressBar communityPost_progressBar;
    private Uri imageUri;
    private BottomNavigationView navBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewCommunityPostViewModel newCommunityPostViewModel =
                new ViewModelProvider(this).get(NewCommunityPostViewModel.class);

        binding = FragmentNewCommunityPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);

        firebaseManager = FirebaseManager.getInstance();
        storage = FirebaseStorage.getInstance();

        createBinding();
        initViews();

        return root;
    }

    private void createBinding() {
        communityPost_TXT_post = binding.communityPostTXTPost;
        communityPost_BTN_addTags = binding.communityPostBTNAddTags;
        communityPost_BTN_selectImage = binding.communityPostBTNSelectImage;
        communityPost_BTN_removeImage = binding.communityPostBTNRemoveImage;
        communityPost_BTN_addLocation = binding.communityPostBTNAddLocation;
        communityPost_BTN_post = binding.communityPostBTNPost;
        communityPost_IMG_back = binding.communityPostIMGBack;
        communityPost_progressBar = binding.communityPostProgressBar;
        communityPost_IMG_ImageView = binding.communityPostIMGImageView;
    }

    private void initViews() {
        communityPost_progressBar.setVisibility(View.GONE);
        communityPost_IMG_ImageView.setImageURI(null);
        communityPost_IMG_ImageView.setVisibility(View.GONE);
        registerResult();

        communityPost_IMG_back.setOnClickListener(this::changeFragment);

        communityPost_TXT_post.addTextChangedListener(postWatcher);
        communityPost_TXT_post.setOnFocusChangeListener(focusChangeListener);

        communityPost_BTN_addTags.setOnClickListener(view -> toast("Tag"));

        communityPost_BTN_selectImage.setOnClickListener(view -> pickImage());
        communityPost_BTN_removeImage.setOnClickListener(view -> clearImage());

        communityPost_BTN_addLocation.setOnClickListener(view -> toast("Location"));


        DocumentReference refUser = firebaseManager.getRefCurrentUser();
        refUser.get().addOnSuccessListener(documentSnapshot -> {
            connectedUser = documentSnapshot.toObject(User.class);
            if (connectedUser != null) {
                communityPost_BTN_post.setOnClickListener(view -> {
                    UUID post_id = UUID.randomUUID();
                    CommunityPost post = new CommunityPost()
                            .setPostId(post_id.toString())
                            .setPost(String.valueOf(communityPost_TXT_post.getEditableText()))
                            .setAuthorUID(connectedUser.getUserId())
                            .setUserName(connectedUser.getUsername())
                            .setCity(connectedUser.getCity());
                    if (imageUri != null) {
                        uploadToFirebase(imageUri, post);
                    } else {
                        firebaseManager.createNewCommunityPostInDB(post);
                        changeFragment(view);
                    }

                });
            }
        });

    }

    private void uploadToFirebase(Uri uri, CommunityPost post) {
        StorageReference fileRef = storage.getReference().child("community_posts_images")
                .child(post.getPost() + "_" + System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        post.setImageURL(uri.toString());
                        firebaseManager.createNewCommunityPostInDB(post);
                        toast("Uploaded successfully");
                        communityPost_progressBar.setVisibility(View.GONE);
                        clearImage();
                        changeFragment(getView());
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                communityPost_progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast("Uploading failed");
                communityPost_progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Uploading failed | " + uri);
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void clearImage() {
        communityPost_IMG_ImageView.setImageURI(null);
        communityPost_IMG_ImageView.setVisibility(View.GONE);
        communityPost_BTN_removeImage.setVisibility(View.GONE);
    }

    private void pickImage() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && android.os.ext.SdkExtensions.getExtensionVersion(android.os.Build.VERSION_CODES.R) >= 2) {
            intent = new Intent((MediaStore.ACTION_PICK_IMAGES));
            resultLauncher.launch(intent);
        }
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            imageUri = result.getData().getData();
                            communityPost_IMG_ImageView.setImageURI(imageUri);
                            Log.d(TAG, "test_imageURI " + imageUri);
                            communityPost_IMG_ImageView.setVisibility(View.VISIBLE);
                            communityPost_BTN_selectImage.setText(R.string.change_picture);
                            communityPost_BTN_selectImage.setVisibility(View.VISIBLE);
                            communityPost_BTN_removeImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            toast("No Image Selected");
                        }
                    }
                }
        );
    }

    TextWatcher postWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String strPost = communityPost_TXT_post.getEditableText().toString();
            communityPost_BTN_post.setEnabled(!strPost.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };
    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    };

    private void changeFragment(View v) {
        resetInputControls();
        Navigation.findNavController(v).navigate(R.id.action_navigation_return_to_community);
        navBar.setVisibility(View.VISIBLE);
    }

    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void resetInputControls() {
        communityPost_TXT_post.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}