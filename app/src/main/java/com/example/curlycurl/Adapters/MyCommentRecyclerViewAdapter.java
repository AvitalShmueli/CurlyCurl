package com.example.curlycurl.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.curlycurl.Models.Comment;
import com.example.curlycurl.Utilities.FirebaseManager;
import com.example.curlycurl.databinding.FragmentCommentBinding;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class MyCommentRecyclerViewAdapter extends RecyclerView.Adapter<MyCommentRecyclerViewAdapter.ViewHolder> {

    private final List<Comment> commentsList;

    public MyCommentRecyclerViewAdapter(List<Comment> items) {
        commentsList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.currentUser = FirebaseManager.getInstance().getmAuth().getCurrentUser();
        holder.mItem = commentsList.get(position);
        holder.comment_LBL_comment.setText(commentsList.get(position).getComment());
        holder.comment_LBL_author.setText(holder.mItem.getUserName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        holder.comment_LBL_created.setText(dateFormat.format(holder.mItem.getCreated().toDate()));

        View.OnClickListener expand_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mItem.isCollapsed())
                    holder.comment_LBL_comment.setMaxLines(Integer.MAX_VALUE);
                else
                    holder.comment_LBL_comment.setMaxLines(Comment.MAX_LINES_COLLAPSED);
                holder.mItem.setCollapsed(!holder.mItem.isCollapsed());
            }
        };
        holder.comment_CARD_data.setOnClickListener(expand_listener);

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final MaterialTextView comment_LBL_comment, comment_LBL_author, comment_LBL_created;
        public final CardView comment_CARD_data;
        public FirebaseUser currentUser;
        public Comment mItem;

        public ViewHolder(FragmentCommentBinding binding) {
            super(binding.getRoot());
            comment_LBL_author = binding.commentLBLAuthor;
            comment_LBL_created = binding.commentLBLCreated;
            comment_LBL_comment = binding.commentLBLComment;
            comment_CARD_data = binding.commentCARDData;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + comment_LBL_comment.getText() + "'";
        }
    }
}