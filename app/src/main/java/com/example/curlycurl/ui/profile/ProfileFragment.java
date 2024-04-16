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

import com.bumptech.glide.Glide;
import com.example.curlycurl.FirebaseManager;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.OpeningScreenActivity;
import com.example.curlycurl.R;
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

    public static final String KEY_UUID = "KEY_UUID";
    public static final String KEY_USERNAME = "KEY_USERNAME";
    private FragmentProfileBinding binding;

    private TextView txt_username, txt_postsNum;
    private ShapeableImageView profile_IMG_user;
    private MaterialButton profile_BTN_signOut;
    private FirebaseManager firebaseManager;
    private User connectedUser;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseManager = FirebaseManager.getInstance();

        createBinding();
        initViews();

        Fragment childFragment = new ProductFragment(ProductFragment.ProductsFragmentMode.USER);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_FRAGMENT_list, childFragment).commit();

        return root;
    }

    private void createBinding() {
        txt_username = binding.profileLBLUsername;
        txt_postsNum = binding.profileLBLPostsNum;
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
                    txt_postsNum.setText(connectedUser.getCity());
                    Glide
                            .with(getContext())
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
