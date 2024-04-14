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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;


public class ProfileFragment extends Fragment {

    public static final String KEY_UUID = "KEY_UUID";
    public static final String KEY_USERNAME = "KEY_USERNAME";
    private FragmentProfileBinding binding;
    private User connectedUser;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        //connectedUser = FirebaseManager.getInstance().getConnectedUserProfile();
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        int x = 1;
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        /*
        Bundle args = getArguments();
        if (args != null) {
            uuid = args.getString(KEY_UUID,"");
            username = args.getString(KEY_USERNAME,"A_S");
        }*/


        Fragment childFragment = new ProductFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_FRAGMENT_list, childFragment).commit();


        final TextView txt_username = binding.profileLBLUsername;
        final TextView txt_postsNum = binding.profileLBLPostsNum;



        /*
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        DocumentReference refUser = db.collection("users").document(mUser.getUid());
        */

        DocumentReference refUser = firebaseManager.getRefUser();
        refUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                connectedUser = documentSnapshot.toObject(User.class);
                if (connectedUser != null) {
                    txt_username.setText(connectedUser.getUsername());
                    txt_postsNum.setText(connectedUser.getCity());
                }
            }
        });



        final MaterialButton profile_BTN_signOut = binding.profileBTNSignOut;
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void changeActivity() {
        Intent openingScreenActivity = new Intent(requireActivity(), OpeningScreenActivity.class);
        startActivity(openingScreenActivity);
        requireActivity().finish();
    }
}