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

import com.example.curlycurl.Utilities.FirebaseManager;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.R;
import com.example.curlycurl.Utilities.SignalManager;
import com.example.curlycurl.databinding.FragmentNewProductPostBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class NewProductPostFragment extends Fragment {
    private FragmentNewProductPostBinding binding;
    private FirebaseManager firebaseManager;
    private FirebaseStorage storage;
    private User connectedUser;
    private TextInputEditText productPost_TXT_productName, productPost_TXT_productDescription, productPost_TXT_addLocation, productPost_TXT_addTags;
    private ShapeableImageView productPost_IMG_ImageView;
    private MaterialButton productPost_BTN_selectImage, productPost_BTN_removeImage, productPost_BTN_writePost;
    private ActivityResultLauncher<Intent> resultLauncher;
    private ArrayList<String> itemsProductType;
    private ArrayList<String> itemsProductCondition;
    private AutoCompleteTextView productPost_DD_productType;
    private AutoCompleteTextView productPost_DD_productCondition;
    private Product.ProductCondition selectedCondition = null;
    private Product.ProductType selectedType = null;
    private Uri imageUri;
    private ProgressBar productPost_progressBar;
    private ChipGroup productPost_chipGroup_tags;

    private ArrayList<String> arrTags;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNewProductPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseManager = FirebaseManager.getInstance();
        storage = FirebaseStorage.getInstance();

        arrTags = new ArrayList<>();

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
        productPost_TXT_productName = binding.productPostTXTProductName;
        productPost_TXT_productDescription = binding.productPostTXTProductDescription;
        productPost_DD_productType = binding.productPostDDProductType;
        productPost_DD_productCondition = binding.productPostDDProductCondition;

        productPost_BTN_selectImage = binding.productPostBTNSelectImage;
        productPost_IMG_ImageView = binding.productPostIMGImageView;
        productPost_BTN_removeImage = binding.productPostBTNRemoveImage;

        productPost_TXT_addLocation = binding.productPostTXTAddLocation;
        productPost_TXT_addTags = binding.productPostTXTAddTags;
        productPost_chipGroup_tags = binding.productPostChipGroupTags;

        productPost_BTN_writePost = binding.productPostBTNWritePost;
        productPost_progressBar = binding.productPostProgressBar;
    }

    private void initViews() {
        productPost_progressBar.setVisibility(View.GONE);
        productPost_IMG_ImageView.setImageURI(null);
        productPost_IMG_ImageView.setVisibility(View.GONE);
        registerResult();

        ArrayAdapter<String> adapterItems_productType = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsProductType);
        productPost_DD_productType.setAdapter(adapterItems_productType);
        productPost_DD_productType.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            selectedType = Product.ProductType.valueOf(item);
        });

        ArrayAdapter<String> adapterItems_productCondition = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsProductCondition);
        productPost_DD_productCondition.setAdapter(adapterItems_productCondition);
        productPost_DD_productCondition.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            selectedCondition = Product.ProductCondition.valueOf(item);
        });


        productPost_TXT_productName.addTextChangedListener(postWatcher);
        productPost_TXT_productDescription.addTextChangedListener(postWatcher);

        productPost_TXT_productName.setOnFocusChangeListener(focusChangeListener);
        productPost_TXT_productDescription.setOnFocusChangeListener(focusChangeListener);

        productPost_TXT_addTags.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Clear focus here from searchbox
                    productPost_TXT_addTags.clearFocus();
                    Chip chip = new Chip(getContext());
                    String strTagValue = String.valueOf(productPost_TXT_addTags.getEditableText());
                    chip.setText(strTagValue);
                    setChipStyle(chip);
                    productPost_chipGroup_tags.addView(chip);
                    arrTags.add(strTagValue);
                    productPost_TXT_addTags.setText("");
                }
                return false;
            }
        });
        productPost_BTN_selectImage.setOnClickListener(view -> pickImage());
        productPost_BTN_removeImage.setOnClickListener(view -> clearImage());

        DocumentReference refUser = firebaseManager.getRefCurrentUser();
        refUser.get().addOnSuccessListener(documentSnapshot -> {
            connectedUser = documentSnapshot.toObject(User.class);
            if (connectedUser != null) {
                productPost_BTN_writePost.setOnClickListener(view -> {
                    UUID pid = UUID.randomUUID();
                    Product product = new Product()
                            .setProductId(pid.toString())
                            .setProductName(String.valueOf(productPost_TXT_productName.getEditableText()))
                            .setDescription(String.valueOf(productPost_TXT_productDescription.getEditableText()))
                            .setProductType(selectedType)
                            .setCondition(selectedCondition)
                            .setOwnerUID(connectedUser.getUserId())
                            .setOwnerEmail(connectedUser.getEmail())
                            .setUserName(connectedUser.getUsername())
                            .setCity(String.valueOf(productPost_TXT_addLocation.getEditableText()))
                            .setTags(arrTags);
                    if (imageUri != null) {
                        uploadToFirebase(imageUri, product);
                    } else {
                        firebaseManager.createNewProductInDB(product);
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
                productPost_chipGroup_tags.removeView(chip);
            }
        });
    }

    private void uploadToFirebase(Uri uri, Product product) {
        StorageReference fileRef = storage.getReference().child("product_images")
                .child(product.getProductId() + "_" + System.currentTimeMillis() + "." + getFileExtension(uri));
        productPost_IMG_ImageView.setDrawingCacheEnabled(true);
        productPost_IMG_ImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) productPost_IMG_ImageView.getDrawable()).getBitmap();
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
                            productPost_progressBar.setVisibility(View.GONE);
                            clearImage();
                            changeFragment(getView());
                        } else {
                            // Handle failures
                            SignalManager.getInstance().toast("Something went wrong");
                            productPost_progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "Something went wrong | " + uri);
                        }
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                productPost_progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SignalManager.getInstance().toast("Uploading failed");
                productPost_progressBar.setVisibility(View.GONE);
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
        productPost_IMG_ImageView.setImageURI(null);
        productPost_BTN_selectImage.setText(R.string.add_a_picture);
        productPost_IMG_ImageView.setVisibility(View.GONE);
        productPost_BTN_removeImage.setVisibility(View.GONE);
    }

    private void pickImage() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && android.os.ext.SdkExtensions.getExtensionVersion(android.os.Build.VERSION_CODES.R) >= 2) {
            //intent = new Intent((MediaStore.ACTION_PICK_IMAGES));
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
                            productPost_IMG_ImageView.setImageURI(imageUri);
                            Log.d(TAG, "test_imageURI " + imageUri);
                            productPost_IMG_ImageView.setVisibility(View.VISIBLE);
                            productPost_BTN_selectImage.setText(R.string.change_picture);
                            productPost_BTN_removeImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            SignalManager.getInstance().toast("No Image Selected");
                        }
                    }
                }
        );
    }

    private void resetInputControls() {
        productPost_TXT_productName.setText("");
        productPost_TXT_productDescription.setText("");

    }

    TextWatcher postWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String strProductName = productPost_TXT_productName.getEditableText().toString();
            String strProductDescription = productPost_TXT_productDescription.getEditableText().toString();
            productPost_BTN_writePost.setEnabled(!strProductName.isEmpty() && !strProductDescription.isEmpty());
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
        Navigation.findNavController(v).navigate(R.id.action_navigation_return_to_profile_new);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}