package com.example.curlycurl.Adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.curlycurl.Interfaces.Callback_ProductPostSelected;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.R;
import com.example.curlycurl.databinding.FragmentProductBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class MyProductRecyclerViewAdapter extends RecyclerView.Adapter<MyProductRecyclerViewAdapter.ViewHolder> {

    private final List<Product> productList;
    private Context context;
    private Callback_ProductPostSelected callbackProductPostSelected;



    public MyProductRecyclerViewAdapter(Context context, List<Product> items) {
        this.context = context;
        productList = items;

    }

    public void setCallbackProductPostSelected(Callback_ProductPostSelected callbackProductPostSelected) {
        this.callbackProductPostSelected = callbackProductPostSelected;
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
                .centerCrop()
                .placeholder(R.drawable.baseline_image_24)
                .into(holder.mImageView);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callbackProductPostSelected != null)
                    callbackProductPostSelected.onProductPostSelected(holder.mItem);
            }
        };
        holder.mProductNameView.setOnClickListener(listener);
        holder.mProductDescriptionView.setOnClickListener(listener);
        holder.mImageView.setOnClickListener(listener);
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

