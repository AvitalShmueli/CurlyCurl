package com.example.curlycurl.ui.profile;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.curlycurl.Adapters.MyProductRecyclerViewAdapter;
import com.example.curlycurl.Utilities.FirebaseManager;
import com.example.curlycurl.Interfaces.Callback_ProductPostSelected;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.R;
import com.example.curlycurl.Utilities.SignalManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ProductFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";

    public enum ProductsFragmentMode {
        USER,
        EXPLORE
    }

    private int mColumnCount = 2;

    private FirebaseManager firebaseManager;
    private List<Product> productList;
    private MyProductRecyclerViewAdapter myAdapter;
    private ProductsFragmentMode mode;
    private Callback_ProductPostSelected callbackProductPostSelected;
    private String searchTerm = "";


    public ProductFragment() {
        this.mode = ProductsFragmentMode.USER;
    }

    public ProductFragment(ProductsFragmentMode mode) {
        this.mode = mode;
    }


    public static ProductFragment newInstance(int columnCount) {
        ProductFragment fragment = new ProductFragment(ProductsFragmentMode.USER);
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

        productList = new ArrayList<>();
        myAdapter = new MyProductRecyclerViewAdapter(getContext(), productList);
        myAdapter.setCallbackProductPostSelected(callbackProductPostSelected);
        EventChangeListener();

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
            recyclerView.setAdapter(myAdapter);
        }


        return view;
    }

    public void setCallbackProductPostSelected(Callback_ProductPostSelected callbackProductPostSelected) {
        this.callbackProductPostSelected = callbackProductPostSelected;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        Log.d(TAG, "Search term has changed: " + searchTerm);
        EventChangeListener();
    }

    private void EventChangeListener() {
        productList.clear();
        if (mode == ProductsFragmentMode.USER) {
            firebaseManager.usersProduct().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("Firestore error", error.getMessage());
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            productList.add(dc.getDocument().toObject(Product.class));
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });
        } else {
            if (!searchTerm.isEmpty()) {
                Query query = firebaseManager.getRefProductsCollection().where(
                        Filter.or(
                                Filter.arrayContains("tags", searchTerm),
                                Filter.and(
                                        Filter.greaterThanOrEqualTo("productName", searchTerm),
                                        Filter.lessThan("productName", searchTerm + 'z')
                                ),
                                Filter.equalTo("productType", searchTerm.toUpperCase())
                        )
                ).orderBy("created", Query.Direction.DESCENDING);
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore error", error.getMessage());
                            SignalManager.getInstance().toast("No results");
                            productList.clear();
                            myAdapter.notifyDataSetChanged();
                            return;
                        }
                        if (value.getDocumentChanges().size() == 0) {
                            SignalManager.getInstance().toast("No results");
                            productList.clear();
                            myAdapter.notifyDataSetChanged();
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                productList.add((dc.getDocument().toObject(Product.class)));
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
            } else {
                firebaseManager.getRefProductsCollection().orderBy("created", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.e("Firestore error", error.getMessage());
                                    return;
                                }

                                for (DocumentChange dc : value.getDocumentChanges()) {
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        productList.add((dc.getDocument().toObject(Product.class)));
                                    }
                                    myAdapter.notifyDataSetChanged();
                                }
                            }
                        });

            }
        }
    }

}