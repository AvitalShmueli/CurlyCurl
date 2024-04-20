package com.example.curlycurl.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.curlycurl.Utilities.FirebaseManager;
import com.example.curlycurl.Interfaces.Callback_CommunityPostSelected;
import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentCommunityPostBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<CommunityPost> communityPostList;
    private Context context;
    private Callback_CommunityPostSelected callbackCommunityPostSelected;


    public MyItemRecyclerViewAdapter(Context context, List<CommunityPost> items) {
        this.context = context;
        communityPostList = items;
    }

    public void setCallbackCommunityPostSelected(Callback_CommunityPostSelected callbackCommunityPostSelected) {
        this.callbackCommunityPostSelected = callbackCommunityPostSelected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentCommunityPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.currentUser = FirebaseManager.getInstance().getmAuth().getCurrentUser();
        holder.mItem = communityPostList.get(position);
        holder.community_LBL_author.setText(holder.mItem.getUserName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        holder.community_LBL_created.setText(dateFormat.format(holder.mItem.getCreated().toDate()));
        holder.community_LBL_post.setText(holder.mItem.getPost());

        if (holder.mItem.getImageURL() == null)
            holder.community_IMG_photo.setVisibility(View.GONE);
        else {
            holder.community_IMG_photo.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .load(holder.mItem.getImageURL())
                    .fitCenter()
                    .placeholder(R.drawable.baseline_image_24)
                    .into(holder.community_IMG_photo);
        }

        StringBuilder strTags = new StringBuilder();
        ArrayList<String> arrTags = holder.mItem.getTags();
        if(arrTags != null && arrTags.size() > 0){
            for(String s : holder.mItem.getTags())
                strTags.append("# ").append(s).append(" ");
            holder.community_LBL_tags.setText(strTags);
            holder.community_LBL_tags.setVisibility(View.VISIBLE);
        }else {
            holder.community_LBL_tags.setText("");
            holder.community_LBL_tags.setVisibility(View.GONE);
        }

        View.OnClickListener expand_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mItem.isCollapsed())
                    holder.community_LBL_post.setMaxLines(Integer.MAX_VALUE);
                else
                    holder.community_LBL_post.setMaxLines(CommunityPost.MAX_LINES_COLLAPSED);
                holder.mItem.setCollapsed(!holder.mItem.isCollapsed());
            }
        };
        holder.community_IMG_photo.setOnClickListener(expand_listener);
        holder.community_LBL_post.setOnClickListener(expand_listener);


        if (holder.mItem.getAuthorUID().equals(holder.currentUser.getUid())) {
            holder.community_BTN_edit.setOnClickListener(v -> {
                if (callbackCommunityPostSelected != null)
                    callbackCommunityPostSelected.onCommunityPostSelected_edit(holder.mItem);
            });
            holder.community_BTN_edit.setVisibility(View.VISIBLE);
            holder.community_SPACE_actions.setVisibility(View.VISIBLE);
        } else {
            holder.community_BTN_edit.setOnClickListener(null);
            holder.community_BTN_edit.setVisibility(View.GONE);
            holder.community_SPACE_actions.setVisibility(View.GONE);
        }
        holder.community_BTN_comment.setOnClickListener(v -> {
            if (callbackCommunityPostSelected != null)
                callbackCommunityPostSelected.onCommunityPostSelected_comment(holder.mItem);
        });
    }

    @Override
    public int getItemCount() {
        return communityPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //public final TextView mIdView;
        public final MaterialTextView community_LBL_post, community_LBL_author, community_LBL_created, community_LBL_tags;
        public final ShapeableImageView community_IMG_photo;
        public final MaterialButton community_BTN_comment, community_BTN_edit;
        public final Space community_SPACE_actions;
        public final CardView community_CARD_data;
        public CommunityPost mItem;
        public FirebaseUser currentUser;

        public ViewHolder(FragmentCommunityPostBinding binding) {
            super(binding.getRoot());
            community_LBL_author = binding.communityLBLAuthor;
            community_LBL_created = binding.communityLBLCreated;
            community_LBL_post = binding.communityLBLPost;
            community_IMG_photo = binding.communityIMGPhoto;
            community_CARD_data = binding.communityCARDData;
            community_BTN_comment = binding.communityBTNComment;
            community_BTN_edit = binding.communityBTNEdit;
            community_SPACE_actions = binding.communitySPACEActions;
            community_LBL_tags = binding.communityLBLTags;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + community_LBL_post.getText() + "'";
        }
    }
}