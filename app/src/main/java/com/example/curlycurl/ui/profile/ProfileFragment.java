package com.example.curlycurl.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Fragment childFragment = new ProductFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.profile_FRAGMENT_list, childFragment).commit();

        final TextView txt_title = binding.profileLBLTitle;
        //profileViewModel.getText().observe(getViewLifecycleOwner(), txt_title::setText);
        txt_title.setText("My hair products");

        final TextView txt_username = binding.profileLBLUsername;
        txt_username.setText("Avital");

        final TextView txt_postsNum = binding.profileLBLPostsNum;
        txt_postsNum.setText("0 posts");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}