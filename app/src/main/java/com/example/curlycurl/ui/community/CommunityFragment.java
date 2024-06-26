package com.example.curlycurl.ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.curlycurl.Interfaces.Callback_CommunityPostSelected;
import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentCommunityBinding;
import com.example.curlycurl.ui.community_post.EditCommunityPostFragment;
import com.google.android.material.button.MaterialButton;

public class CommunityFragment extends Fragment {
    private FragmentCommunityBinding binding;
    private MaterialButton productPost_BTN_writePost;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CommunityPostsFragment childFragment = new CommunityPostsFragment();
        childFragment.setCallbackCommunityPostSelected(new Callback_CommunityPostSelected() {
            @Override
            public void onCommunityPostSelected_comment(CommunityPost post) {
                navigateToEditCommunityPostFragment(post, EditCommunityPostFragment.PostMode.COMMENT);
            }

            @Override
            public void onCommunityPostSelected_edit(CommunityPost post) {
                navigateToEditCommunityPostFragment(post, EditCommunityPostFragment.PostMode.EDIT);
            }
        });
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.community_FRAGMENT_list, childFragment).commit();

        productPost_BTN_writePost = binding.productPostBTNWritePost;
        productPost_BTN_writePost.setOnClickListener(this::replaceFragment);

        return root;
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
        args.putString("frag","community");
        args.putString("mode", String.valueOf(mode));
        Navigation.findNavController(requireView()).navigate(R.id.navigateToEditCommunityPostFragment_community,args);
    }

    private void replaceFragment(View v) {
        Navigation.findNavController(v).navigate(R.id.action_navigation_create_new_community_post);
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}