package com.example.curlycurl.ui.new_product_post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentNewProductPostBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class NewProductPostFragment extends Fragment {

    private FragmentNewProductPostBinding binding;

    private MaterialTextView productPost_LBL_title;
    private MaterialTextView productPost_LBL_addTags;
    private MaterialTextView productPost_LBL_addPhoto;
    private MaterialTextView productPost_LBL_addLocation;
    private BottomNavigationView navBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewProductPostViewModel newProductPostViewModel =
                new ViewModelProvider(this).get(NewProductPostViewModel.class);

        binding = FragmentNewProductPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /*navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);*/

        productPost_LBL_title = binding.productPostLBLTitle;
        //newProductPostViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        productPost_LBL_addTags = binding.productPostLBLAddTags;
        productPost_LBL_addPhoto = binding.productPostLBLAddPhoto;
        productPost_LBL_addLocation = binding.productPostLBLAddLocation;


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