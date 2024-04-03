package com.example.curlycurl.ui.profile;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.curlycurl.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.curlycurl.databinding.FragmentProductBinding;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public MyItemRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mProductNameView.setText(mValues.get(position).content);
        holder.mProductDescriptionView.setText(mValues.get(position).details);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mProductNameView;
        public final TextView mProductDescriptionView;
        public final ShapeableImageView mImageView;
        public PlaceholderItem mItem;

        public ViewHolder(FragmentProductBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mProductNameView = binding.productLBLName;
            mImageView = binding.productIMGPhoto;
            mProductDescriptionView = binding.productLBLDescription;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mProductNameView.getText() + "'";
        }
    }
}