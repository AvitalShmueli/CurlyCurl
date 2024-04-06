package com.example.curlycurl.ui.new_community_post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.curlycurl.databinding.FragmentNewCommunityPostBinding;
import com.example.curlycurl.databinding.FragmentNewProductPostBinding;
import com.google.android.material.textview.MaterialTextView;

public class NewCommunityPostFragment extends Fragment {

    private FragmentNewCommunityPostBinding binding;

    private MaterialTextView productPost_LBL_title;
    private MaterialTextView productPost_LBL_addTags;
    private MaterialTextView productPost_LBL_addPhoto;
    private MaterialTextView productPost_LBL_addLocation;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewCommunityPostViewModel newCommunityPostViewModel =
                new ViewModelProvider(this).get(NewCommunityPostViewModel.class);

        binding = FragmentNewCommunityPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        productPost_LBL_title = binding.communityPostLBLTitle;
        //newProductPostViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        productPost_LBL_addTags = binding.communityPostLBLAddTags;
        productPost_LBL_addPhoto = binding.productPostLBLAddPhoto;
        productPost_LBL_addLocation = binding.communityPostLBLAddLocation;


        productPost_LBL_addTags.setOnClickListener(view->toast("Tag"));
        productPost_LBL_addPhoto.setOnClickListener(view->toast("Photo"));
        productPost_LBL_addLocation.setOnClickListener(view->toast("Location"));

        return root;
    }

    private void toast(String msg) {
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}