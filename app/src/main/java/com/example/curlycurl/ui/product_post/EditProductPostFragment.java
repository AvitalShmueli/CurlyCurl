package com.example.curlycurl.ui.product_post;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.curlycurl.Utilities.FirebaseManager;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.R;
import com.example.curlycurl.Utilities.SignalManager;
import com.example.curlycurl.databinding.FragmentEditProductPostBinding;
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

public class EditProductPostFragment extends Fragment {
    private FragmentEditProductPostBinding binding;
    private FirebaseManager firebaseManager;
    private FirebaseStorage storage;
    private User connectedUser;
    private LinearLayout editProductPost_CONTAINER_imageActions;
    private TextInputEditText editProductPost_TXT_productName, editProductPost_TXT_productDescription, editProductPost_TXT_addLocation, editProductPost_TXT_addTags;
    private ShapeableImageView editProductPost_IMG_ImageView, editProductPost_IMG_back;
    private MaterialButton editProductPost_BTN_selectImage, editProductPost_BTN_removeImage, editProductPost_BTN_post, editProductPost_BTN_delete;
    private MaterialTextView editProductPost_LBL_title, editProductPost_LBL_ownerEmail;
    private ActivityResultLauncher<Intent> resultLauncher;
    private ArrayList<String> itemsProductType;
    private ArrayList<String> itemsProductCondition;
    private TextInputLayout editProductPost_DD_layout_productType, editProductPost_DD_layout_productCondition, editProductPost_TXT_layout_addLocation, editProductPost_TXT_layout_addTags;
    private AutoCompleteTextView editProductPost_DD_productType, editProductPost_DD_productCondition;
    private Product.ProductCondition selectedCondition = null;
    private Product.ProductType selectedType = null;
    private Uri imageUri;
    private ProgressBar editProductPost_progressBar;
    private Product product = null;
    private boolean isOwner;
    private String frag;
    private BottomNavigationView navBar;
    private ChipGroup editProductPost_chipGroup_tags;
    private ArrayList<String> arrTags;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEditProductPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);

        firebaseManager = FirebaseManager.getInstance();
        storage = FirebaseStorage.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            product = new Product()
                    .setProductId(args.getString("product_id"))
                    .setProductName(args.getString("product_name"))
                    .setDescription(args.getString("product_description"))
                    .setProductType(Product.ProductType.valueOf(args.getString("product_type")))
                    .setCondition(Product.ProductCondition.valueOf(args.getString("product_condition")))
                    .setCity(args.getString("product_city"))
                    .setImageURL(args.getString("imageURL"))
                    .setOwnerUID(args.getString("ownerUID"))
                    .setUserName(args.getString("userName"))
                    .setOwnerEmail(args.getString("ownerEmail"))
                    .setTags(args.getStringArrayList("tags"));
            frag = args.getString("frag");
            assert product != null;
            Log.d(TAG, "product.getOwnerUID() " + product.getOwnerUID());
            isOwner = product.getOwnerUID().equals(firebaseManager.getmUser().getUid());
        }

        createBinding();
        initDropDownValues();
        initViews();


        return root;
    }

    private void initDropDownValues() {
        itemsProductType = new ArrayList<>();
        for (Product.ProductType p : Product.ProductType.values()) {
            itemsProductType.add(p.name());
        }
        Log.d(TAG, "dropdown items (product type) = " + itemsProductType.toString());

        itemsProductCondition = new ArrayList<>();
        for (Product.ProductCondition c : Product.ProductCondition.values()) {
            itemsProductCondition.add(c.name());
        }
        Log.d(TAG, "dropdown items (product condition) = " + itemsProductCondition.toString());

    }

    private void createBinding() {
        editProductPost_LBL_title = binding.editProductPostLBLTitle;
        editProductPost_IMG_back = binding.editProductPostIMGBack;

        editProductPost_CONTAINER_imageActions = binding.editProductPostCONTAINERImageActions;

        editProductPost_TXT_productName = binding.editProductPostTXTProductName;
        editProductPost_TXT_productDescription = binding.editProductPostTXTProductDescription;
        editProductPost_DD_layout_productType = binding.editProductPostDDLayoutProductType;
        editProductPost_DD_productType = binding.editProductPostDDProductType;
        editProductPost_DD_layout_productCondition = binding.editProductPostDDLayoutProductCondition;
        editProductPost_DD_productCondition = binding.editProductPostDDProductCondition;
        editProductPost_LBL_ownerEmail = binding.editProductPostLBLOwnerEmail;

        editProductPost_BTN_selectImage = binding.editProductPostBTNSelectImage;
        editProductPost_IMG_ImageView = binding.editProductPostIMGImageView;
        editProductPost_BTN_removeImage = binding.editProductPostBTNRemoveImage;

        editProductPost_TXT_addLocation = binding.editProductPostTXTAddLocation;
        editProductPost_TXT_layout_addLocation = binding.editProductPostTXTLayoutAddLocation;
        editProductPost_TXT_addTags = binding.editProductPostTXTAddTags;
        editProductPost_chipGroup_tags = binding.editProductPostChipGroupTags;
        editProductPost_TXT_layout_addTags = binding.editProductPostTXTLayoutAddTags;

        editProductPost_BTN_post = binding.editProductPostBTNPost;
        editProductPost_BTN_delete = binding.editProductPostBTNDelete;
        editProductPost_progressBar = binding.editProductPostProgressBar;
    }

    private void initViews() {
        editProductPost_progressBar.setVisibility(View.VISIBLE);

        editProductPost_IMG_back.setOnClickListener(this::changeFragment);
        enableControlsIfOwner();
        loadProductData();

        if (isOwner) {
            registerResult();

            ArrayAdapter<String> adapterItems_productType = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsProductType);
            editProductPost_DD_productType.setAdapter(adapterItems_productType);
            editProductPost_DD_productType.setOnItemClickListener((adapterView, view, position, id) -> {
                String item = adapterView.getItemAtPosition(position).toString();
                selectedType = Product.ProductType.valueOf(item);
            });

            ArrayAdapter<String> adapterItems_productCondition = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsProductCondition);
            editProductPost_DD_productCondition.setAdapter(adapterItems_productCondition);
            editProductPost_DD_productCondition.setOnItemClickListener((adapterView, view, position, id) -> {
                String item = adapterView.getItemAtPosition(position).toString();
                selectedCondition = Product.ProductCondition.valueOf(item);
            });


            editProductPost_TXT_productName.addTextChangedListener(postWatcher);
            editProductPost_TXT_productDescription.addTextChangedListener(postWatcher);

            editProductPost_TXT_productName.setOnFocusChangeListener(focusChangeListener);
            editProductPost_TXT_productDescription.setOnFocusChangeListener(focusChangeListener);

            editProductPost_TXT_addTags.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        //Clear focus here from searchbox
                        editProductPost_TXT_addTags.clearFocus();
                        Chip chip = new Chip(getContext());
                        String strTagValue = String.valueOf(editProductPost_TXT_addTags.getEditableText()).trim();
                        if(!strTagValue.isEmpty() && !arrTags.contains(strTagValue)) {
                            chip.setText(strTagValue);
                            setChipStyle(chip);
                            editProductPost_chipGroup_tags.addView(chip);
                            arrTags.add(strTagValue);
                        }
                        editProductPost_TXT_addTags.setText("");
                    }
                    return false;
                }
            });
            editProductPost_BTN_selectImage.setOnClickListener(view -> pickImage());
            editProductPost_BTN_removeImage.setOnClickListener(view -> clearImage());

            DocumentReference refUser = firebaseManager.getRefCurrentUser();
            refUser.get().addOnSuccessListener(documentSnapshot -> {
                connectedUser = documentSnapshot.toObject(User.class);
                if (connectedUser != null) {
                    editProductPost_BTN_post.setOnClickListener(view -> {
                        product
                                .setProductName(String.valueOf(editProductPost_TXT_productName.getEditableText()))
                                .setDescription(String.valueOf(editProductPost_TXT_productDescription.getEditableText()))
                                .setProductType(selectedType)
                                .setCondition(selectedCondition)
                                .setOwnerUID(connectedUser.getUserId())
                                .setUserName(connectedUser.getUsername())
                                .setCity(String.valueOf(editProductPost_TXT_addLocation.getEditableText()))
                                .setTags(arrTags);
                        if (imageUri != null) {
                            uploadToFirebase(imageUri, product);
                        } else {
                            firebaseManager.updateProductInDB(product);
                            changeFragment(view);
                        }
                    });
                }
            });
            editProductPost_BTN_delete.setOnClickListener(view -> {
                firebaseManager.deleteProductFromDB(product);
                changeFragment(view);
            });
        }
        editProductPost_progressBar.setVisibility(View.GONE);
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
        chip.setCloseIconVisible(isOwner);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrTags.remove(chip.getText().toString());
                editProductPost_chipGroup_tags.removeView(chip);
            }
        });
    }

    private void loadProductData() {
        editProductPost_TXT_productName.setText(product.getProductName());
        if (product.getProductType() != null) {
            editProductPost_DD_productType.setText(product.getProductType().name());
            selectedType = product.getProductType();
        }
        if (product.getCondition() != null) {
            editProductPost_DD_productCondition.setText(product.getCondition().name());
            selectedCondition = product.getCondition();
        }
        editProductPost_TXT_productDescription.setText(product.getDescription());
        editProductPost_TXT_addLocation.setText(product.getCity());
        arrTags = product.getTags();
        if (arrTags != null) {
            if (arrTags.size() == 0)
                editProductPost_TXT_layout_addTags.setVisibility(isOwner ? View.VISIBLE : View.GONE);
            else {
                for (String tag : arrTags) {
                    Chip chip = new Chip(getContext());
                    chip.setText(tag);
                    setChipStyle(chip);
                    editProductPost_chipGroup_tags.addView(chip);
                }
            }
        } else {
            arrTags = new ArrayList<>();
            editProductPost_TXT_layout_addTags.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        }

        if (product.getImageURL() != null) {
            Glide
                    .with(requireContext())
                    .load(product.getImageURL())
                    .fitCenter()
                    .placeholder(R.drawable.baseline_image_24)
                    .into(editProductPost_IMG_ImageView);
            editProductPost_BTN_selectImage.setText(R.string.change_picture);
            editProductPost_BTN_removeImage.setVisibility(View.VISIBLE);
            editProductPost_IMG_ImageView.setVisibility(View.VISIBLE);

        } else {
            clearImage();
        }
        if (!isOwner) {
            String strOwnerEmail = "✉   " + product.getOwnerEmail();
            editProductPost_LBL_ownerEmail.setText(strOwnerEmail);
        }
    }

    private void enableControlsIfOwner() {
        editProductPost_LBL_title.setText(isOwner ? R.string.edit_product : R.string.product_details);
        editProductPost_TXT_productName.setEnabled(isOwner);
        editProductPost_TXT_productDescription.setEnabled(isOwner);
        editProductPost_DD_productType.setEnabled(isOwner);
        editProductPost_DD_layout_productType.setEndIconVisible(isOwner);
        editProductPost_DD_productCondition.setEnabled(isOwner);
        editProductPost_DD_layout_productCondition.setEndIconVisible(isOwner);
        editProductPost_TXT_addTags.setEnabled(isOwner);
        editProductPost_TXT_layout_addTags.setVisibility(isOwner ? View.VISIBLE : View.GONE);

        editProductPost_TXT_addLocation.setEnabled(isOwner);
        editProductPost_TXT_layout_addLocation.setHelperTextEnabled(isOwner);
        editProductPost_TXT_layout_addLocation.setHint(isOwner ? R.string.change_location : R.string.location);

        editProductPost_CONTAINER_imageActions.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        editProductPost_BTN_selectImage.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        editProductPost_BTN_post.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        editProductPost_BTN_post.setEnabled(isOwner);
        editProductPost_BTN_delete.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        editProductPost_BTN_removeImage.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        editProductPost_LBL_ownerEmail.setVisibility(isOwner ? View.GONE : View.VISIBLE);
    }

    private void uploadToFirebase(Uri uri, Product product) {
        StorageReference fileRef = storage.getReference().child("product_images")
                .child(product.getProductId() + "_" + System.currentTimeMillis() + "." + getFileExtension(uri));
        editProductPost_IMG_ImageView.setDrawingCacheEnabled(true);
        editProductPost_IMG_ImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) editProductPost_IMG_ImageView.getDrawable()).getBitmap();
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
                            product.setImageURL(downloadUri.toString());
                            firebaseManager.createNewProductInDB(product);
                            SignalManager.getInstance().toast("Uploaded successfully");
                            editProductPost_progressBar.setVisibility(View.GONE);
                            clearImage();
                            changeFragment(getView());
                        } else {
                            // Handle failures
                            SignalManager.getInstance().toast("Something went wrong");
                            editProductPost_progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "Something went wrong | " + uri);
                        }
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                editProductPost_progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SignalManager.getInstance().toast("Uploading failed");
                editProductPost_progressBar.setVisibility(View.GONE);
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
        editProductPost_IMG_ImageView.setImageURI(null);
        editProductPost_BTN_selectImage.setText(R.string.add_a_picture);
        editProductPost_IMG_ImageView.setVisibility(View.GONE);
        editProductPost_BTN_removeImage.setVisibility(View.GONE);
        product.setImageURL(null);
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
                            editProductPost_IMG_ImageView.setImageURI(imageUri);
                            editProductPost_IMG_ImageView.setVisibility(View.VISIBLE);
                            editProductPost_BTN_selectImage.setText(R.string.change_picture);
                            editProductPost_BTN_removeImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            SignalManager.getInstance().toast("No Image Selected");
                        }
                    }
                }
        );
    }

    private void resetInputControls() {
        editProductPost_TXT_productName.setText("");
        editProductPost_TXT_productDescription.setText("");
    }

    TextWatcher postWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String strProductName = editProductPost_TXT_productName.getEditableText().toString();
            String strProductDescription = editProductPost_TXT_productDescription.getEditableText().toString();
            editProductPost_BTN_post.setEnabled(!strProductName.isEmpty() && !strProductDescription.isEmpty());
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
        if (isOwner && frag.equals("profile"))
            Navigation.findNavController(v).navigate(R.id.action_navigation_return_to_profile_edit);
        else
            Navigation.findNavController(v).navigate(R.id.action_editProductPostFragment_to_navigation_explore);
        navBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetInputControls();
        binding = null;
    }


}