package com.example.curlycurl.Adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.R;
import com.example.curlycurl.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.curlycurl.databinding.FragmentProductBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyProductRecyclerViewAdapter extends RecyclerView.Adapter<MyProductRecyclerViewAdapter.ViewHolder> {

    private final List<Product> productList;
    private Context context;

    public MyProductRecyclerViewAdapter(Context context, List<Product> items) {
        this.context = context;
        productList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = productList.get(position);
        holder.mProductNameView.setText(productList.get(position).getProductName());
        holder.mProductDescriptionView.setText(productList.get(position).getDescription());
        Glide
                .with(context)
                .load(productList.get(position).getImageURL())
                .fitCenter()
                .placeholder(R.drawable.baseline_image_24)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final MaterialTextView mProductNameView;
        public final MaterialTextView mProductDescriptionView;
        public final ShapeableImageView mImageView;
        public Product mItem;

        public ViewHolder(FragmentProductBinding binding) {
            super(binding.getRoot());
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