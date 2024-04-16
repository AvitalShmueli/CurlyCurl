package com.example.curlycurl.Adapters;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.curlycurl.FirebaseManager;
import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentCommunityPostBinding;
import com.example.curlycurl.placeholder.PlaceholderContent.PlaceholderItem;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<CommunityPost> communityPostList;
    private Context context;
    private FirebaseManager firebaseManager;
    private User connectedUser;


    public MyItemRecyclerViewAdapter(Context context, List<CommunityPost> items) {
        this.context = context;
        communityPostList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentCommunityPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = communityPostList.get(position);
        //holder.mIdView.setText(mValues.get(position).id);

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

        holder.community_CARD_data.setOnClickListener(v -> {
            if (holder.mItem.isCollapsed())
                holder.community_LBL_post.setMaxLines(Integer.MAX_VALUE);
            else
                holder.community_LBL_post.setMaxLines(CommunityPost.MAX_LINES_COLLAPSED);
            holder.mItem.setCollapsed(!holder.mItem.isCollapsed());
        });
    }

    @Override
    public int getItemCount() {
        return communityPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //public final TextView mIdView;
        public final MaterialTextView community_LBL_post, community_LBL_author, community_LBL_created;
        public final ShapeableImageView community_IMG_photo;
        public final CardView community_CARD_data;
        public CommunityPost mItem;

        public ViewHolder(FragmentCommunityPostBinding binding) {
            super(binding.getRoot());
            //mIdView = binding.itemNumber;
            community_LBL_author = binding.communityLBLAuthor;
            community_LBL_created = binding.communityLBLCreated;
            community_LBL_post = binding.communityLBLPost;
            community_IMG_photo = binding.communityIMGPhoto;
            community_CARD_data = binding.communityCARDData;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + community_LBL_post.getText() + "'";
        }
    }
}