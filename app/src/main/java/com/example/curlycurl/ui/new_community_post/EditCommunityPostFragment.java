package com.example.curlycurl.ui.new_community_post;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.curlycurl.FirebaseManager;
import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.R;
import com.example.curlycurl.Utilities.SignalManager;
import com.example.curlycurl.databinding.FragmentEditCommunityPostBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class EditCommunityPostFragment extends Fragment {
    private FragmentEditCommunityPostBinding binding;
    private FirebaseManager firebaseManager;
    private FirebaseStorage storage;
    private User connectedUser;
    private ActivityResultLauncher<Intent> resultLauncher;
    private MaterialTextView editCommunityPost_LBL_title;
    private MaterialButton editCommunityPost_BTN_addTags, editCommunityPost_BTN_selectImage, editCommunityPost_BTN_removeImage, editCommunityPost_BTN_addLocation, editCommunityPost_BTN_post;
    private TextInputEditText editCommunityPost_TXT_post;
    private ShapeableImageView editCommunityPost_IMG_back, editCommunityPost_IMG_ImageView;
    private LinearLayout editCommunityPost_CONTAINER_imageActions;
    private ProgressBar editCommunityPost_progressBar;
    private Uri imageUri;
    private CommunityPost post = null;
    private boolean isAuthor;
    private String frag;
    private BottomNavigationView navBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEditCommunityPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);

        firebaseManager = FirebaseManager.getInstance();
        storage = FirebaseStorage.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            post = new CommunityPost()
                    .setPostId(args.getString("post_id"))
                    .setPost(args.getString("post_post"))
                    .setAuthorUID(args.getString("post_authorUID"))
                    .setCity(args.getString("post_city"))
                    .setImageURL(args.getString("imageURL"))
                    .setUserName(args.getString("userName"));
            frag = args.getString("frag");
            assert post != null;
            Log.d(TAG, "post.getAuthorUID() " + post.getAuthorUID());
            isAuthor = post.getAuthorUID().equals(firebaseManager.getmUser().getUid());
        }

        createBinding();
        initViews();

        return root;
    }

    private void createBinding() {
        editCommunityPost_LBL_title = binding.editCommunityPostLBLTitle;
        editCommunityPost_TXT_post = binding.editCommunityPostTXTPost;
        editCommunityPost_BTN_addTags = binding.editCommunityPostBTNAddTags;
        editCommunityPost_BTN_selectImage = binding.editCommunityPostBTNSelectImage;
        editCommunityPost_BTN_removeImage = binding.editCommunityPostBTNRemoveImage;
        editCommunityPost_BTN_addLocation = binding.editCommunityPostBTNAddLocation;
        editCommunityPost_BTN_post = binding.editCommunityPostBTNPost;
        editCommunityPost_IMG_back = binding.editCommunityPostIMGBack;
        editCommunityPost_progressBar = binding.editCommunityPostProgressBar;
        editCommunityPost_IMG_ImageView = binding.editCommunityPostIMGImageView;
        editCommunityPost_CONTAINER_imageActions = binding.editCommunityPostCONTAINERImageActions;
    }

    private void initViews() {
        editCommunityPost_progressBar.setVisibility(View.VISIBLE);
        editCommunityPost_IMG_back.setOnClickListener(this::changeFragment);

        loadProductData();
        enableControlsIfOwner();
        if (isAuthor) {
            registerResult();

            editCommunityPost_TXT_post.addTextChangedListener(postWatcher);
            editCommunityPost_TXT_post.setOnFocusChangeListener(focusChangeListener);
            editCommunityPost_TXT_post.setMovementMethod(new ScrollingMovementMethod());

            editCommunityPost_BTN_addTags.setOnClickListener(view -> SignalManager.getInstance().toast("Tag"));

            editCommunityPost_BTN_selectImage.setOnClickListener(view -> pickImage());
            editCommunityPost_BTN_removeImage.setOnClickListener(view -> clearImage());

            editCommunityPost_BTN_addLocation.setOnClickListener(view -> SignalManager.getInstance().toast("Location"));

            DocumentReference refUser = firebaseManager.getRefCurrentUser();
            refUser.get().addOnSuccessListener(documentSnapshot -> {
                connectedUser = documentSnapshot.toObject(User.class);
                if (connectedUser != null) {
                    editCommunityPost_BTN_post.setOnClickListener(view -> {
                        UUID post_id = UUID.randomUUID();
                        CommunityPost post = new CommunityPost()
                                .setPostId(post_id.toString())
                                .setPost(String.valueOf(editCommunityPost_TXT_post.getEditableText()))
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
        editCommunityPost_progressBar.setVisibility(View.GONE);

    }


    private void loadProductData() {
        editCommunityPost_TXT_post.setText(post.getPost());
        Log.d(TAG, "post " + post.getPostId() + " | " + post.getImageURL());
        if (post.getImageURL() != null) {
            Glide
                    .with(requireContext())
                    .load(post.getImageURL())
                    .fitCenter()
                    .placeholder(R.drawable.baseline_image_24)
                    .into(editCommunityPost_IMG_ImageView);
            editCommunityPost_BTN_selectImage.setText(R.string.change_picture);
            editCommunityPost_BTN_removeImage.setVisibility(View.VISIBLE);
            editCommunityPost_IMG_ImageView.setVisibility(View.VISIBLE);

        } else {
            editCommunityPost_progressBar.setVisibility(View.GONE);
            editCommunityPost_IMG_ImageView.setImageURI(null);
            editCommunityPost_IMG_ImageView.setVisibility(View.GONE);
        }
        if (!isAuthor) {
            //SOMETHING???
        }
    }

    private void enableControlsIfOwner() {
        editCommunityPost_LBL_title.setText(isAuthor ? R.string.edit_post : R.string.post);
        editCommunityPost_TXT_post.setEnabled(isAuthor);
        editCommunityPost_CONTAINER_imageActions.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
        editCommunityPost_BTN_selectImage.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
        editCommunityPost_BTN_post.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
        editCommunityPost_BTN_post.setEnabled(isAuthor);
        editCommunityPost_BTN_removeImage.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
    }


    private void uploadToFirebase(Uri uri, CommunityPost post) {
        StorageReference fileRef = storage.getReference().child("community_posts_images")
                .child(post.getPostId() + "_" + System.currentTimeMillis() + "." + getFileExtension(uri));
        editCommunityPost_IMG_ImageView.setDrawingCacheEnabled(true);
        editCommunityPost_IMG_ImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) editCommunityPost_IMG_ImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return fileRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            post.setImageURL(downloadUri.toString());
                            firebaseManager.createNewCommunityPostInDB(post);
                            SignalManager.getInstance().toast("Uploaded successfully");
                            editCommunityPost_progressBar.setVisibility(View.GONE);
                            clearImage();
                            changeFragment(getView());
                        } else {
                            // Handle failures
                            SignalManager.getInstance().toast("Something went wrong");
                            editCommunityPost_progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "Something went wrong | " + uri);
                        }
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                editCommunityPost_progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SignalManager.getInstance().toast("Uploading failed");
                editCommunityPost_progressBar.setVisibility(View.GONE);
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
        editCommunityPost_IMG_ImageView.setImageURI(null);
        editCommunityPost_BTN_selectImage.setText(R.string.add_a_picture);
        editCommunityPost_IMG_ImageView.setVisibility(View.GONE);
        editCommunityPost_BTN_removeImage.setVisibility(View.GONE);
    }

    private void pickImage() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && android.os.ext.SdkExtensions.getExtensionVersion(android.os.Build.VERSION_CODES.R) >= 2) {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                            editCommunityPost_IMG_ImageView.setImageURI(imageUri);
                            Log.d(TAG, "test_imageURI " + imageUri);
                            editCommunityPost_IMG_ImageView.setVisibility(View.VISIBLE);
                            editCommunityPost_BTN_selectImage.setText(R.string.change_picture);
                            editCommunityPost_BTN_selectImage.setVisibility(View.VISIBLE);
                            editCommunityPost_BTN_removeImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            SignalManager.getInstance().toast("No Image Selected");
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
            String strPost = editCommunityPost_TXT_post.getEditableText().toString();
            editCommunityPost_BTN_post.setEnabled(!strPost.isEmpty());
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
        if (frag.equals("community"))
            Navigation.findNavController(v).navigate(R.id.action_editCommunityPostFragment_to_navigation_community);
        else {
            Bundle args = new Bundle();
            args.putInt("displayedFragment", 1);//community post fragment
            Navigation.findNavController(v).navigate(R.id.action_editCommunityPostFragment_to_navigation_explore, args);
        }
        navBar.setVisibility(View.VISIBLE);
    }

    private void resetInputControls() {
        editCommunityPost_TXT_post.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}