package com.example.curlycurl.ui.community_post;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.curlycurl.Models.Comment;
import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.R;
import com.example.curlycurl.Utilities.FirebaseManager;
import com.example.curlycurl.Utilities.SignalManager;
import com.example.curlycurl.databinding.FragmentEditCommunityPostBinding;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class EditCommunityPostFragment extends Fragment {
    public enum PostMode {
        EDIT,
        COMMENT
    }

    private FragmentEditCommunityPostBinding binding;
    private FirebaseManager firebaseManager;
    private FirebaseStorage storage;
    private User connectedUser;
    private ActivityResultLauncher<Intent> resultLauncher;
    private MaterialTextView editCommunityPost_LBL_title, editCommunityPost_LBL_CommentsTitle;
    private MaterialButton editCommunityPost_BTN_selectImage, editCommunityPost_BTN_removeImage, editCommunityPost_BTN_post, editCommunityPost_BTN_addComment, editCommunityPost_BTN_submitComment;
    private TextInputEditText editCommunityPost_TXT_post, editCommunityPost_TXT_comment, editCommunityPost_TXT_addLocation, editCommunityPost_TXT_addTags;
    private TextInputLayout editCommunityPost_TXT_layout_addLocation, editCommunityPost_TXT_layout_addTags;
    private ShapeableImageView editCommunityPost_IMG_back, editCommunityPost_IMG_ImageView;
    private LinearLayout editCommunityPost_CONTAINER_imageActions, editCommunityPost_CONTAINER_commentActions;
    private ChipGroup editCommunityPost_chipGroup_tags;
    private ProgressBar editCommunityPost_progressBar;
    private Uri imageUri;
    private CommunityPost post = null;
    private boolean isAuthor;
    private ArrayList<String> arrTags;
    private String frag;
    private BottomNavigationView navBar;
    private View divider;
    private PostMode mode;


    public EditCommunityPostFragment() {
        this.mode = PostMode.COMMENT;
    }

    public EditCommunityPostFragment(PostMode mode) {
        this.mode = mode;
    }

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
                    .setUserName(args.getString("userName"))
                    .setTags(args.getStringArrayList("tags"));
            frag = args.getString("frag");
            mode = PostMode.valueOf(args.getString("mode"));
            assert post != null;
            Log.d(TAG, "post.getAuthorUID() " + post.getAuthorUID() +" | mode: "+mode);
            isAuthor = post.getAuthorUID().equals(firebaseManager.getmUser().getUid());
        }

        createBinding();
        initViews();

        return root;
    }

    private void createBinding() {
        editCommunityPost_LBL_title = binding.editCommunityPostLBLTitle;
        editCommunityPost_TXT_post = binding.editCommunityPostTXTPost;
        editCommunityPost_BTN_selectImage = binding.editCommunityPostBTNSelectImage;
        editCommunityPost_BTN_removeImage = binding.editCommunityPostBTNRemoveImage;
        editCommunityPost_TXT_layout_addLocation = binding.editCommunityPostTXTLayoutAddLocation;
        editCommunityPost_TXT_addLocation = binding.editCommunityPostTXTAddLocation;
        editCommunityPost_TXT_addTags = binding.editCommunityPostTXTAddTags;
        editCommunityPost_chipGroup_tags = binding.editCommunityPostChipGroupTags;
        editCommunityPost_TXT_layout_addTags = binding.editCommunityPostTXTLayoutAddTags;

        editCommunityPost_BTN_post = binding.editCommunityPostBTNPost;
        editCommunityPost_IMG_back = binding.editCommunityPostIMGBack;
        editCommunityPost_progressBar = binding.editCommunityPostProgressBar;
        editCommunityPost_IMG_ImageView = binding.editCommunityPostIMGImageView;
        editCommunityPost_CONTAINER_imageActions = binding.editCommunityPostCONTAINERImageActions;

        divider = binding.divider;
        editCommunityPost_BTN_addComment = binding.editCommunityPostBTNAddComment;
        editCommunityPost_CONTAINER_commentActions = binding.editCommunityPostCONTAINERCommentActions;
        editCommunityPost_TXT_comment = binding.editCommunityPostTXTComment;
        editCommunityPost_BTN_submitComment = binding.editCommunityPostBTNSubmitComment;
        editCommunityPost_LBL_CommentsTitle = binding.editCommunityPostLBLCommentsTitle;
    }

    private void initViews() {
        editCommunityPost_progressBar.setVisibility(View.VISIBLE);
        editCommunityPost_IMG_back.setOnClickListener(this::changeFragment);

        enableControlsIfOwner();
        loadProductData();

        if (isAuthor && mode == PostMode.EDIT) {
            registerResult();

            editCommunityPost_TXT_post.addTextChangedListener(postWatcher);
            editCommunityPost_TXT_post.setOnFocusChangeListener(focusChangeListener);
            editCommunityPost_TXT_post.setMovementMethod(new ScrollingMovementMethod());

            editCommunityPost_TXT_addTags.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        //Clear focus here from searchbox
                        editCommunityPost_TXT_addTags.clearFocus();
                        Chip chip = new Chip(getContext());
                        String strTagValue = String.valueOf(editCommunityPost_TXT_addTags.getEditableText()).trim();
                        if(!strTagValue.isEmpty() && !arrTags.contains(strTagValue)) {
                            chip.setText(strTagValue);
                            setChipStyle(chip);
                            editCommunityPost_chipGroup_tags.addView(chip);
                            arrTags.add(strTagValue);
                        }
                        editCommunityPost_TXT_addTags.setText("");
                    }
                    return false;
                }
            });

            editCommunityPost_BTN_selectImage.setOnClickListener(view -> pickImage());
            editCommunityPost_BTN_removeImage.setOnClickListener(view -> clearImage());

            DocumentReference refUser = firebaseManager.getRefCurrentUser();
            refUser.get().addOnSuccessListener(documentSnapshot -> {
                connectedUser = documentSnapshot.toObject(User.class);
                if (connectedUser != null) {
                    editCommunityPost_BTN_post.setOnClickListener(view -> {
                        post
                                .setPost(String.valueOf(editCommunityPost_TXT_post.getEditableText()))
                                .setAuthorUID(connectedUser.getUserId())
                                .setUserName(connectedUser.getUsername())
                                .setCity(connectedUser.getCity())
                                .setCity(String.valueOf(editCommunityPost_TXT_addLocation.getEditableText()))
                                .setTags(arrTags);
                        if (imageUri != null) {
                            uploadToFirebase(imageUri, post);
                        } else {
                            firebaseManager.updateCommunityPostInDB(post);
                            changeFragment(view);
                        }

                    });
                }
            });

        } else {
            CommentFragment childFragment = new CommentFragment(post);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.editCommunityPost_FRAGMENT_comments, childFragment).commit();

            DocumentReference refUser = firebaseManager.getRefCurrentUser();
            refUser.get().addOnSuccessListener(documentSnapshot -> {
                connectedUser = documentSnapshot.toObject(User.class);
                if (connectedUser != null) {
                    editCommunityPost_BTN_submitComment.setOnClickListener(v -> {
                        // SAVE COMMENT TO DB
                        UUID cid = UUID.randomUUID();
                        Comment newComment = new Comment()
                                .setComment(String.valueOf(editCommunityPost_TXT_comment.getEditableText()))
                                .setCommentId(cid.toString())
                                .setAuthorUID(connectedUser.getUserId())
                                .setUserName(connectedUser.getUsername());

                        firebaseManager.addCommentOnPost(post, newComment);

                        editCommunityPost_BTN_addComment.setVisibility(View.VISIBLE);
                        show_hide_comments(false);
                    });
                }
            });

            editCommunityPost_BTN_addComment.setOnClickListener(v -> {
                editCommunityPost_BTN_addComment.setVisibility(View.GONE);
                show_hide_comments(true);
            });

        }

        editCommunityPost_progressBar.setVisibility(View.GONE);
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
        chip.setCloseIconVisible(isAuthor && mode == PostMode.EDIT);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrTags.remove(chip.getText().toString());
                editCommunityPost_chipGroup_tags.removeView(chip);
            }
        });
    }


    private void loadProductData() {
        editCommunityPost_TXT_post.setText(post.getPost());
        editCommunityPost_TXT_addLocation.setText(post.getCity());
        arrTags = post.getTags();
        if (arrTags != null) {
                for (String tag : arrTags) {
                    Chip chip = new Chip(getContext());
                    chip.setText(tag);
                    setChipStyle(chip);
                    editCommunityPost_chipGroup_tags.addView(chip);
                }
        } else {
            arrTags = new ArrayList<>();
            editCommunityPost_TXT_layout_addTags.setVisibility(isAuthor && mode == PostMode.EDIT ? View.VISIBLE : View.GONE);
        }
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
            clearImage();
        }
    }

    private void enableControlsIfOwner() {
        boolean isEditMode = isAuthor && mode == PostMode.EDIT;

        editCommunityPost_LBL_title.setText(isEditMode ? R.string.edit_post : R.string.post);
        editCommunityPost_TXT_post.setEnabled(isEditMode);
        editCommunityPost_TXT_addLocation.setEnabled(isEditMode);
        editCommunityPost_TXT_layout_addLocation.setHint(isEditMode ? R.string.change_location : R.string.location);
        editCommunityPost_TXT_layout_addLocation.setHelperTextEnabled(isEditMode);
        editCommunityPost_TXT_addTags.setEnabled(isEditMode);
        editCommunityPost_TXT_layout_addTags.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        editCommunityPost_CONTAINER_imageActions.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        editCommunityPost_BTN_selectImage.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        editCommunityPost_BTN_removeImage.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        editCommunityPost_BTN_post.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        editCommunityPost_BTN_post.setEnabled(isEditMode);

        //comments section
        editCommunityPost_LBL_CommentsTitle.setPaintFlags(editCommunityPost_LBL_CommentsTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        editCommunityPost_LBL_CommentsTitle.setVisibility(!isEditMode ? View.VISIBLE : View.GONE);
        editCommunityPost_BTN_addComment.setVisibility(!isEditMode ? View.VISIBLE : View.GONE);
        show_hide_comments(false);

    }

    private void show_hide_comments(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;

        divider.setVisibility(visibility);
        editCommunityPost_CONTAINER_commentActions.setVisibility(visibility);
        editCommunityPost_TXT_comment.setVisibility(visibility);
        editCommunityPost_BTN_submitComment.setVisibility(visibility);
        editCommunityPost_TXT_comment.setText("");
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
                            firebaseManager.updateCommunityPostInDB(post);
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
        post.setImageURL(null);
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
        editCommunityPost_TXT_comment.setText("");
        editCommunityPost_TXT_addLocation.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetInputControls();
        binding = null;
    }

}