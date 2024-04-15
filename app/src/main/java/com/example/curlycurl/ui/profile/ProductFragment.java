package com.example.curlycurl.ui.profile;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.curlycurl.Adapters.MyProductRecyclerViewAdapter;
import com.example.curlycurl.FirebaseManager;
import com.example.curlycurl.Models.User;
import com.example.curlycurl.R;
import com.google.firebase.firestore.DocumentReference;


/**
 * A fragment representing a list of Items.
 */
public class ProductFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;

    private FirebaseManager firebaseManager;
    private User connectedUser;



    public static ProductFragment newInstance(int columnCount) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        firebaseManager = FirebaseManager.getInstance();
        DocumentReference refUser = firebaseManager.getRefUser();

        refUser.get().addOnSuccessListener(documentSnapshot -> {
            connectedUser = documentSnapshot.toObject(User.class);
            if (connectedUser != null) {
                // Set the adapter
                if (view instanceof RecyclerView) {
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    if (mColumnCount <= 1) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    } else {
                        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                    }
                    //recyclerView.setAdapter(new MyProductRecyclerViewAdapter(PlaceholderContent.ITEMS));
                    if(connectedUser.getAll_products()!=null)
                        recyclerView.setAdapter(new MyProductRecyclerViewAdapter(connectedUser.getAll_products()));
                }
            }
        });

        return view;
    }
}