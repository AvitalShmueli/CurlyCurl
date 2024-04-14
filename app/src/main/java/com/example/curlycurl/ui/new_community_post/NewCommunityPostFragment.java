package com.example.curlycurl.ui.new_community_post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentNewCommunityPostBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class NewCommunityPostFragment extends Fragment{

    private FragmentNewCommunityPostBinding binding;

    private MaterialTextView productPost_LBL_title;
    private MaterialButton communityPost_BTN_addTags;
    private MaterialButton communityPost_BTN_selectImage;
    private MaterialButton communityPost_BTN_addLocation;
    private MaterialButton communityPost_BTN_post;
    private ShapeableImageView communityPost_IMG_back;
    private BottomNavigationView navBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewCommunityPostViewModel newCommunityPostViewModel =
                new ViewModelProvider(this).get(NewCommunityPostViewModel.class);

        binding = FragmentNewCommunityPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);

        productPost_LBL_title = binding.communityPostLBLTitle;
        //newProductPostViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        communityPost_BTN_addTags = binding.communityPostBTNAddTags;
        communityPost_BTN_selectImage = binding.communityPostBTNSelectImage;
        communityPost_BTN_addLocation = binding.communityPostBTNAddLocation;
        communityPost_BTN_post = binding.communityPostBTNPost;
        communityPost_IMG_back = binding.communityPostIMGBack;

        communityPost_BTN_addTags.setOnClickListener(view -> toast("Tag"));
        communityPost_BTN_selectImage.setOnClickListener(view -> toast("Photo"));
        communityPost_BTN_addLocation.setOnClickListener(view -> toast("Location"));

        communityPost_BTN_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // post (save to DB)
                changeFragment(v);
            }
        });
        communityPost_IMG_back.setOnClickListener(this::changeFragment);

        return root;
    }

    private void changeFragment(View v) {
        Navigation.findNavController(v).navigate(R.id.action_navigation_return_to_community);
        navBar.setVisibility(View.VISIBLE);
    }

    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void resetInputControls() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}