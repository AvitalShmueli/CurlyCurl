package com.example.curlycurl.ui.explore;

import static android.content.ContentValues.TAG;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.curlycurl.Interfaces.Callback_ProductPostSelected;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentExploreBinding;
import com.example.curlycurl.ui.community.CommunityPostsFragment;
import com.example.curlycurl.ui.profile.ProductFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
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
        showAsMap = false;

        createBinding();
        initViews();

        showChildFragment(0);

        return root;
    }

    private void createBinding() {
        explore_BTN_marketplace = binding.exploreBTNMarketplace;
        explore_BTN_community = binding.exploreBTNCommunity;
        explore_IMG_map = binding.exploreIMGMap;
    }

    private void initViews(){
        explore_BTN_marketplace.setOnClickListener(v -> showChildFragment(0));
        explore_BTN_community.setOnClickListener(v -> showChildFragment(1));
        explore_IMG_map.setOnClickListener(v -> showAsMap(!showAsMap));
    }

    private void showAsMap(boolean show) {
        showAsMap = !showAsMap;
    }

    private void showChildFragment(int fragment) {
        Fragment childFragment;
        if (fragment == 0) {
            explore_BTN_marketplace.setPaintFlags(explore_BTN_marketplace.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            explore_BTN_community.setPaintFlags(0);
            childFragment = new ProductFragment(ProductFragment.ProductsFragmentMode.EXPLORE);
            ((ProductFragment)childFragment).setCallbackProductPostSelected(new Callback_ProductPostSelected() {
                @Override
                public void onProductPostSelected(Product product) {
                    Log.d(TAG,"onclick | " +product.getProductId() + " | "+product.getOwnerUID());
                    navigateToEditProductPostFragment(product);
                }
            });
        } else {
            explore_BTN_marketplace.setPaintFlags(0);
            explore_BTN_community.setPaintFlags(explore_BTN_community.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            childFragment = new CommunityPostsFragment();
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.explore_FRAGMENT_list, childFragment).commit();

    }

    private void navigateToEditProductPostFragment(Product product){
        Log.d(TAG,"putSerializable | "+product.getProductName()+" "+product.getOwnerUID());
        Bundle args = new Bundle();
        args.putSerializable("key",product);
        Navigation.findNavController(getView()).navigate(R.id.navigateToEditProductPostFragment_explore,args);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}