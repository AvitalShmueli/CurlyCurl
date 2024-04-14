package com.example.curlycurl.ui.new_product_post;

import static android.content.ContentValues.TAG;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentNewProductPostBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.UUID;

public class NewProductPostFragment extends Fragment {

    private FragmentNewProductPostBinding binding;
    private FirebaseManager firebaseManager;
    private User connectedUser;


    private TextInputEditText productPost_TXT_productName;
    private TextInputEditText productPost_TXT_productDescription;
    private ShapeableImageView productPost_IMG_ImageView;
    private MaterialButton productPost_BTN_addTags;
    private MaterialButton productPost_BTN_selectImage;
    private MaterialButton productPost_BTN_removeImage;
    private MaterialButton productPost_BTN_addLocation;
    private MaterialButton productPost_BTN_post;

    private ActivityResultLauncher<Intent> resultLauncher;
    private ArrayList<String> itemsProductType;
    private ArrayList<String> itemsProductCondition;
    private AutoCompleteTextView productPost_DD_productType;
    private AutoCompleteTextView productPost_DD_productCondition;


    private Product.ProductCondition selectedCondition = null;
    private Product.ProductType selectedType = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewProductPostViewModel newProductPostViewModel =
                new ViewModelProvider(this).get(NewProductPostViewModel.class);

        binding = FragmentNewProductPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseManager = FirebaseManager.getInstance();


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

        productPost_BTN_addLocation = binding.productPostBTNAddLocation;
        productPost_BTN_addTags = binding.productPostBTNAddTags;

        productPost_BTN_post = binding.productPostBTNPost;
    }

    private void initViews() {
        productPost_IMG_ImageView.setImageURI(null);
        productPost_IMG_ImageView.setVisibility(View.GONE);
        registerResult();

        ArrayAdapter<String> adapterItems_productType = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsProductType);
        productPost_DD_productType.setAdapter(adapterItems_productType);
        productPost_DD_productType.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            selectedType = Product.ProductType.valueOf(item);
            toast("Selected type: " + item);
        });

        ArrayAdapter<String> adapterItems_productCondition = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsProductCondition);
        productPost_DD_productCondition.setAdapter(adapterItems_productCondition);
        productPost_DD_productCondition.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            selectedCondition = Product.ProductCondition.valueOf(item);
            toast("Selected condition: " + item);
        });



        productPost_TXT_productName.addTextChangedListener(postWatcher);
        productPost_TXT_productDescription.addTextChangedListener(postWatcher);

        productPost_BTN_addTags.setOnClickListener(view -> toast("Tag"));
        productPost_BTN_selectImage.setOnClickListener(view -> pickImage());
        productPost_BTN_removeImage.setOnClickListener(view -> clearImage());
        productPost_BTN_addLocation.setOnClickListener(view -> toast("Location"));


        DocumentReference refUser = firebaseManager.getRefUser();
        refUser.get().addOnSuccessListener(documentSnapshot -> {
            connectedUser = documentSnapshot.toObject(User.class);
            if (connectedUser != null) {
                productPost_BTN_post.setOnClickListener(view -> {
                    UUID pid = UUID.randomUUID();
                    String strProductName = String.valueOf(productPost_TXT_productName.getEditableText());
                    Log.d(TAG, "strProductName | " + strProductName);
                    Product product = new Product()
                            .setProductId(pid.toString())
                            .setProductName(String.valueOf(productPost_TXT_productName.getEditableText()))
                            .setDescription(String.valueOf(productPost_TXT_productDescription.getEditableText()))
                            .setProductType(selectedType)
                            .setCondition(selectedCondition)
                            .setOwnerUID(connectedUser.getUserId())
                            .setCity(connectedUser.getCity());
                    FirebaseManager.getInstance().createNewProductInDB(product);
                    changeFragment(view);
                });
            }
        });
    }

    TextWatcher postWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String strProductName = productPost_TXT_productName.getEditableText().toString();
            String strProductDescription = productPost_TXT_productDescription.getEditableText().toString();
            productPost_BTN_post.setEnabled(!strProductName.isEmpty() && !strProductDescription.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void clearImage() {
        productPost_IMG_ImageView.setImageURI(null);
        productPost_IMG_ImageView.setVisibility(View.GONE);
        productPost_BTN_removeImage.setVisibility(View.GONE);
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
                            Uri imageUri = result.getData().getData();
                            productPost_IMG_ImageView.setImageURI(imageUri);
                            Log.d(TAG, "test_imageURI " + imageUri);
                            productPost_IMG_ImageView.setVisibility(View.VISIBLE);
                            productPost_BTN_selectImage.setText(R.string.change_picture);
                            productPost_BTN_removeImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            toast("No Image Selected");
                        }
                    }
                }
        );
    }

    private void resetInputControls() {
        productPost_TXT_productName.setText("");
        productPost_TXT_productDescription.setText("");
    }

    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void changeFragment(View v) {
        resetInputControls();
        Navigation.findNavController(v).navigate(R.id.action_navigation_return_to_profile);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        //resetInputControls();
    }
}