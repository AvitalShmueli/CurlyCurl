package com.example.curlycurl.ui.community_post;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.R;
import com.example.curlycurl.Utilities.FirebaseManager;
import com.example.curlycurl.Utilities.SignalManager;
import com.example.curlycurl.databinding.FragmentNewCommunityPostBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class NewCommunityPostFragment extends Fragment {
    private FragmentNewCommunityPostBinding binding;
    private FirebaseManager firebaseManager;
    private FirebaseStorage storage;
    private User connectedUser;
    private ActivityResultLauncher<Intent> resultLauncher;
    private MaterialButton communityPost_BTN_selectImage, communityPost_BTN_removeImage, communityPost_BTN_post;
    private TextInputEditText communityPost_TXT_post, communityPost_TXT_addTags, communityPost_TXT_addLocation;
    private ShapeableImageView communityPost_IMG_back, communityPost_IMG_ImageView;
    private ProgressBar communityPost_progressBar;
    private Uri imageUri;
    private BottomNavigationView navBar;
    private ChipGroup communityPost_chipGroup_tags;

    private ArrayList<String> arrTags;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNewCommunityPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);

        firebaseManager = FirebaseManager.getInstance();
        storage = FirebaseStorage.getInstance();

        arrTags = new ArrayList<>();

        createBinding();
        initViews();

        return root;
    }

    private void createBinding() {
        communityPost_TXT_post = binding.communityPostTXTPost;
        communityPost_TXT_addTags = binding.communityPostTXTAddTags;
        communityPost_chipGroup_tags = binding.communityPostChipGroupTags;

        communityPost_BTN_selectImage = binding.communityPostBTNSelectImage;
        communityPost_BTN_removeImage = binding.communityPostBTNRemoveImage;
        communityPost_TXT_addLocation = binding.communityPostTXTAddLocation;
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

        communityPost_TXT_addTags.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Clear focus here from searchbox
                    communityPost_TXT_addTags.clearFocus();
                    Chip chip = new Chip(getContext());
                    String strTagValue = String.valueOf(communityPost_TXT_addTags.getEditableText()).trim();
                    if(!strTagValue.isEmpty() && !arrTags.contains(strTagValue)) {
                        chip.setText(strTagValue);
                        setChipStyle(chip);
                        communityPost_chipGroup_tags.addView(chip);
                        arrTags.add(strTagValue);
                    }
                    communityPost_TXT_addTags.setText("");
                }
                return false;
            }
        });

        communityPost_BTN_selectImage.setOnClickListener(view -> pickImage());
        communityPost_BTN_removeImage.setOnClickListener(view -> clearImage());


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
                            .setCity(String.valueOf(communityPost_TXT_addLocation.getEditableText()))
                            .setTags(arrTags);
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

    private void setChipStyle(Chip chip) {
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(requireContext(), null, 0, com.firebase.ui.auth.R.style.Widget_MaterialComponents_Chip_Entry);
        chip.setChipDrawable(chipDrawable);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setChipIconResource(R.drawable.baseline_tag_24);
        chip.setIconStartPadding(3f);
        chip.setChipIconTint(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_purple)));
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        chip.setPadding(50, 10, 50, 10);
        chip.setTextEndPadding(30f);
        chip.setTextStartPadding(30f);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrTags.remove(chip.getText().toString());
                communityPost_chipGroup_tags.removeView(chip);
            }
        });
    }

    private void uploadToFirebase(Uri uri, CommunityPost post) {
        StorageReference fileRef = storage.getReference().child("community_posts_images")
                .child(post.getPostId() + "_" + System.currentTimeMillis() + "." + getFileExtension(uri));
        communityPost_IMG_ImageView.setDrawingCacheEnabled(true);
        communityPost_IMG_ImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) communityPost_IMG_ImageView.getDrawable()).getBitmap();
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
                            communityPost_progressBar.setVisibility(View.GONE);
                            clearImage();
                            changeFragment(getView());
                        } else {
                            // Handle failures
                            SignalManager.getInstance().toast("Something went wrong");
                            communityPost_progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "Something went wrong | " + uri);
                        }
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
                SignalManager.getInstance().toast("Uploading failed");
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
        communityPost_BTN_selectImage.setText(R.string.add_a_picture);
        communityPost_IMG_ImageView.setVisibility(View.GONE);
        communityPost_BTN_removeImage.setVisibility(View.GONE);
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
                            communityPost_IMG_ImageView.setImageURI(imageUri);
                            communityPost_IMG_ImageView.setVisibility(View.VISIBLE);
                            communityPost_BTN_selectImage.setText(R.string.change_picture);
                            communityPost_BTN_selectImage.setVisibility(View.VISIBLE);
                            communityPost_BTN_removeImage.setVisibility(View.VISIBLE);
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
        Navigation.findNavController(v).navigate(R.id.action_navigation_return_to_community);
        navBar.setVisibility(View.VISIBLE);
    }

    private void resetInputControls() {
        communityPost_TXT_post.setText("");
        communityPost_TXT_addLocation.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetInputControls();
        binding = null;
    }

}