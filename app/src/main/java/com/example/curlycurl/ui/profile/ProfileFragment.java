package com.example.curlycurl.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.curlycurl.Interfaces.Callback_ProductPostSelected;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.OpeningScreenActivity;
import com.example.curlycurl.R;
import com.example.curlycurl.Utilities.FirebaseManager;
import com.example.curlycurl.databinding.FragmentProfileBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView txt_username, txt_details;
    private ShapeableImageView profile_IMG_user;
    private MaterialButton profile_BTN_signOut;
    private FirebaseManager firebaseManager;
    private User connectedUser;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseManager = FirebaseManager.getInstance();

        createBinding();
        initViews();

        ProductFragment childFragment = new ProductFragment(ProductFragment.ProductsFragmentMode.USER);
        childFragment.setCallbackProductPostSelected(new Callback_ProductPostSelected() {
            @Override
            public void onProductPostSelected(Product product) {
                navigateToEditProductPostFragment(product);
            }
        });
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_FRAGMENT_list, childFragment).commit();


        return root;
    }

    private void createBinding() {
        txt_username = binding.profileLBLUsername;
        txt_details = binding.profileLBLDetails;
        profile_IMG_user = binding.profileIMGUser;
        profile_BTN_signOut = binding.profileBTNSignOut;
    }

    private void initViews() {

        DocumentReference refUser = firebaseManager.getRefCurrentUser();
        refUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                connectedUser = documentSnapshot.toObject(User.class);
                if (connectedUser != null) {
                    txt_username.setText(connectedUser.getUsername());
                    StringBuilder strDetails = new StringBuilder();
                    if (connectedUser.getCurlType() != null)
                        strDetails.append(connectedUser.getCurlType().toString().substring(1));
                    if (connectedUser.getCity() != null) {
                        strDetails.append(" | ").append(connectedUser.getCity());
                    }
                    txt_details.setText(strDetails);
                    Glide
                            .with(requireContext())
                            .load(connectedUser.getImageURL())
                            .centerCrop()
                            .placeholder(R.drawable.user_photo)
                            .into(profile_IMG_user);

                }
            }
        });

        profile_BTN_signOut.setOnClickListener(v -> {
            AuthUI.getInstance()
                    .signOut(requireActivity())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseManager.signOut();
                            changeActivity();
                        }
                    });
        });
    }

    private void navigateToEditProductPostFragment(Product product) {
        Bundle args = new Bundle();
        args.putString("product_id", product.getProductId());
        args.putString("product_name", product.getProductName());
        args.putString("product_description", product.getDescription());
        args.putString("product_type", product.getProductType().toString());
        args.putString("product_condition", product.getCondition().toString());
        args.putString("product_city", product.getCity());
        args.putString("imageURL", product.getImageURL());
        args.putString("ownerUID", product.getOwnerUID());
        args.putString("userName", product.getUserName());
        args.putString("ownerEmail", product.getOwnerEmail());
        args.putStringArrayList("tags", product.getTags());
        args.putString("frag", "profile");
        Navigation.findNavController(requireView()).navigate(R.id.navigateToEditProductPostFragment_profile, args);

    }

    private void changeActivity() {
        Intent openingScreenActivity = new Intent(requireActivity(), OpeningScreenActivity.class);
        startActivity(openingScreenActivity);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
