package com.example.curlycurl.ui.explore;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentExploreBinding;
import com.example.curlycurl.ui.community.CommunityPostsFragment;
import com.example.curlycurl.ui.profile.ProductFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private Fragment childFragment;
    private MaterialButton explore_BTN_marketplace;
    private MaterialButton explore_BTN_community;
    private ShapeableImageView explore_IMG_map;
    private boolean showAsMap;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExploreViewModel exploreViewModel =
                new ViewModelProvider(this).get(ExploreViewModel.class);

        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        explore_BTN_marketplace = binding.exploreBTNMarketplace;
        explore_BTN_community = binding.exploreBTNCommunity;
        explore_IMG_map = binding.exploreIMGMap;

        showChildFragment(0);
        showAsMap = false;

        explore_BTN_marketplace.setOnClickListener(v -> showChildFragment(0));
        explore_BTN_community.setOnClickListener(v -> showChildFragment(1));
        explore_IMG_map.setOnClickListener(v -> showAsMap(!showAsMap));

        return root;
    }

    private void showAsMap(boolean show) {
        showAsMap = !showAsMap;
    }

    private void showChildFragment(int fragment) {
        if (fragment == 0) {
            explore_BTN_marketplace.setPaintFlags(explore_BTN_marketplace.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            explore_BTN_community.setPaintFlags(0);
            childFragment = new ProductFragment();
        } else {
            explore_BTN_marketplace.setPaintFlags(0);
            explore_BTN_community.setPaintFlags(explore_BTN_community.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            childFragment = new CommunityPostsFragment();
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.explore_FRAGMENT_list, childFragment).commit();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}