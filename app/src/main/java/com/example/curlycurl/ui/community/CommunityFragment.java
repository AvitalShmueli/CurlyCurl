package com.example.curlycurl.ui.community;

import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentCommunityBinding;
import com.example.curlycurl.ui.new_community_post.NewCommunityPostFragment;
import com.google.android.material.button.MaterialButton;

public class CommunityFragment extends Fragment {
    private FragmentCommunityBinding binding;
    private CommunityViewModel mViewModel;
    private MaterialButton productPost_BTN_post;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Fragment childFragment = new CommunityPostsFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.community_FRAGMENT_list, childFragment).commit();

        productPost_BTN_post = binding.productPostBTNPost;
        productPost_BTN_post.setOnClickListener(v -> replaceFragment());

        return root;
    }

    private void replaceFragment() {
        //Navigation.findNavController(view).navigate(R.id.action_navigation_community_to_new_community_post);
        /*NewCommunityPostFragment newCommunityPostFragment = new NewCommunityPostFragment();
        getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main,newCommunityPostFragment).commit();*/
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}