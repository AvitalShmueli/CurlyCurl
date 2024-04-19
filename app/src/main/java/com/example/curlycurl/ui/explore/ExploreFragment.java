package com.example.curlycurl.ui.explore;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.curlycurl.Interfaces.Callback_CommunityPostSelected;
import com.example.curlycurl.Interfaces.Callback_ProductPostSelected;
import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentExploreBinding;
import com.example.curlycurl.ui.community.CommunityPostsFragment;
import com.example.curlycurl.ui.community_post.EditCommunityPostFragment;
import com.example.curlycurl.ui.profile.ProductFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;


public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private MaterialButton explore_BTN_marketplace, explore_BTN_community;
    private ShapeableImageView explore_IMG_map, explore_IMG_search;
    private TextInputEditText explore_TXT_search;
    private Fragment childFragment;
    private boolean showAsMap;
    private int displayedFragment = 0;
    private String searchTerm;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        showAsMap = false;

        createBinding();
        initViews();
        Bundle args = getArguments();
        if (args != null)
            displayedFragment = args.getInt("displayedFragment");

        showChildFragment(displayedFragment);

        return root;
    }

    private void createBinding() {
        explore_BTN_marketplace = binding.exploreBTNMarketplace;
        explore_BTN_community = binding.exploreBTNCommunity;
        explore_IMG_map = binding.exploreIMGMap;
        explore_TXT_search = binding.exploreTXTSearch;
        explore_IMG_search = binding.exploreIMGSearch;
    }

    private void initViews() {

        explore_TXT_search.setOnFocusChangeListener(focusChangeListener);
        explore_TXT_search.addTextChangedListener(searchWatcher);
        explore_TXT_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //Clear focus here from searchbox
                    explore_TXT_search.clearFocus();
                }
                return false;
            }
        });

        explore_BTN_marketplace.setOnClickListener(v -> {
            displayedFragment = 0;
            showChildFragment(displayedFragment);
        });
        explore_BTN_community.setOnClickListener(v -> {
            displayedFragment = 1;
            showChildFragment(displayedFragment);
        });
        explore_IMG_map.setOnClickListener(v -> showAsMap());
    }

    private void showAsMap() {
        showAsMap = !showAsMap;
        showChildFragment(displayedFragment);
    }

    private void showChildFragment(int fragment) {
        if (showAsMap) {
            childFragment = new MapsFragment();
            ((MapsFragment) childFragment).setSearchTerm(searchTerm);
            if (fragment == 0) { // products
                explore_BTN_marketplace.setPaintFlags(explore_BTN_marketplace.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                explore_BTN_community.setPaintFlags(0);
                ((MapsFragment) childFragment).setMode(MapsFragment.MapFragmentMode.PRODUCTS);
                ((MapsFragment) childFragment).setCallbackProductPostSelected(new Callback_ProductPostSelected() {
                    @Override
                    public void onProductPostSelected(Product product) {
                        navigateToEditProductPostFragment(product);
                    }
                });
                ((MapsFragment) childFragment).setSearchTerm(searchTerm);
            } else { //community
                explore_BTN_marketplace.setPaintFlags(0);
                explore_BTN_community.setPaintFlags(explore_BTN_community.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                ((MapsFragment) childFragment).setMode(MapsFragment.MapFragmentMode.COMMUNITY);
                ((MapsFragment) childFragment).setCallbackCommunityPostSelected(new Callback_CommunityPostSelected() {
                    @Override
                    public void onCommunityPostSelected_comment(CommunityPost post) {
                        navigateToEditCommunityPostFragment(post, EditCommunityPostFragment.PostMode.COMMENT);
                    }

                    @Override
                    public void onCommunityPostSelected_edit(CommunityPost post) {
                        navigateToEditCommunityPostFragment(post, EditCommunityPostFragment.PostMode.EDIT);
                    }
                });
                ((MapsFragment) childFragment).setSearchTerm(searchTerm);
            }
            explore_IMG_map.setImageResource(R.drawable.baseline_grid_view_24);
        } else {
            if (fragment == 0) { // products
                explore_BTN_marketplace.setPaintFlags(explore_BTN_marketplace.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                explore_BTN_community.setPaintFlags(0);
                childFragment = new ProductFragment(ProductFragment.ProductsFragmentMode.EXPLORE);
                ((ProductFragment) childFragment).setCallbackProductPostSelected(new Callback_ProductPostSelected() {
                    @Override
                    public void onProductPostSelected(Product product) {
                        navigateToEditProductPostFragment(product);
                    }
                });
            } else { //community
                explore_BTN_marketplace.setPaintFlags(0);
                explore_BTN_community.setPaintFlags(explore_BTN_community.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                childFragment = new CommunityPostsFragment(CommunityPostsFragment.CommunityPostsFragmentMode.EXPLORE);
                ((CommunityPostsFragment) childFragment).setCallbackCommunityPostSelected(new Callback_CommunityPostSelected() {
                    @Override
                    public void onCommunityPostSelected_comment(CommunityPost post) {
                        navigateToEditCommunityPostFragment(post, EditCommunityPostFragment.PostMode.COMMENT);
                    }

                    @Override
                    public void onCommunityPostSelected_edit(CommunityPost post) {
                        //no edit from map view
                        navigateToEditCommunityPostFragment(post, EditCommunityPostFragment.PostMode.COMMENT);
                    }
                });
            }
            explore_IMG_map.setImageResource(R.drawable.map);
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.explore_FRAGMENT_list, childFragment).commit();
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
        args.putStringArrayList("tags",product.getTags());
        args.putString("frag", "explore");
        Navigation.findNavController(requireView()).navigate(R.id.navigateToEditProductPostFragment_explore, args);

    }

    private void navigateToEditCommunityPostFragment(CommunityPost post, EditCommunityPostFragment.PostMode mode) {
        Bundle args = new Bundle();
        args.putString("post_id", post.getPostId());
        args.putString("post_post", post.getPost());
        args.putString("post_authorUID", post.getAuthorUID());
        args.putString("post_city", post.getCity());
        args.putString("imageURL", post.getImageURL());
        args.putString("userName", post.getUserName());
        args.putStringArrayList("tags",post.getTags());
        args.putString("frag", "explore");
        args.putString("mode", String.valueOf(mode));
        Navigation.findNavController(requireView()).navigate(R.id.navigateToEditCommunityPostFragment_explore, args);

    }

    TextWatcher searchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            explore_IMG_search.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            explore_IMG_search.setVisibility(View.INVISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (explore_TXT_search.getEditableText().length() == 0) {
                searchTerm = "";
                if (showAsMap)
                    ((MapsFragment) childFragment).setSearchTerm(searchTerm);
                else {
                    if (displayedFragment == 0)
                        ((ProductFragment) childFragment).setSearchTerm(searchTerm);
                    else
                        ((CommunityPostsFragment) childFragment).setSearchTerm(searchTerm);
                }
                explore_IMG_search.setVisibility(View.VISIBLE);
            }
        }
    };
    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            searchTerm = explore_TXT_search.getEditableText().toString().trim();
            if (showAsMap)
                ((MapsFragment) childFragment).setSearchTerm(searchTerm);
            else {
                if (displayedFragment == 0)
                    ((ProductFragment) childFragment).setSearchTerm(searchTerm);
                else
                    ((CommunityPostsFragment) childFragment).setSearchTerm(searchTerm);
            }
            explore_IMG_search.setVisibility(View.INVISIBLE);
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}