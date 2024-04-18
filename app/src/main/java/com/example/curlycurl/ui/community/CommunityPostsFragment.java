package com.example.curlycurl.ui.community;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.curlycurl.Adapters.MyItemRecyclerViewAdapter;
import com.example.curlycurl.FirebaseManager;
import com.example.curlycurl.Interfaces.Callback_CommunityPostSelected;
import com.example.curlycurl.Models.CommunityPost;
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

/**
 * A fragment representing a list of Items.
 */
public class CommunityPostsFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";

    public enum CommunityPostsFragmentMode {
        COMMUNITY,
        EXPLORE
    }

    private int mColumnCount = 1;
    private FirebaseManager firebaseManager;
    private List<CommunityPost> communityPostList;
    private MyItemRecyclerViewAdapter myAdapter;
    private CommunityPostsFragmentMode mode;
    private Callback_CommunityPostSelected callbackCommunityPostSelected;
    private String searchTerm = "";


    public CommunityPostsFragment() {
        this.mode = CommunityPostsFragmentMode.COMMUNITY;
    }

    public CommunityPostsFragment(CommunityPostsFragmentMode mode) {
        this.mode = mode;
    }

    @SuppressWarnings("unused")
    public static CommunityPostsFragment newInstance(int columnCount) {
        CommunityPostsFragment fragment = new CommunityPostsFragment();
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
        View view = inflater.inflate(R.layout.fragment_community_posts_list, container, false);

        firebaseManager = FirebaseManager.getInstance();

        communityPostList = new ArrayList<>();
        myAdapter = new MyItemRecyclerViewAdapter(getContext(), communityPostList);
        myAdapter.setCallbackCommunityPostSelected(callbackCommunityPostSelected);

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
            //recyclerView.setAdapter(new MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS));
            recyclerView.setAdapter(myAdapter);
        }
        return view;
    }

    public void setCallbackCommunityPostSelected(Callback_CommunityPostSelected callbackCommunityPostSelected) {
        this.callbackCommunityPostSelected = callbackCommunityPostSelected;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        Log.d(TAG, "Search term has changed: " + searchTerm);
        EventChangeListener();
    }

    private void EventChangeListener() {
        communityPostList.clear();
        if (mode == CommunityPostsFragmentMode.COMMUNITY || searchTerm.isEmpty()) {
            showAllPosts();
        } else {
            Query query = firebaseManager.getRefCommunityPostsCollection().where(
                    Filter.or(
                            Filter.and(
                                    Filter.greaterThanOrEqualTo("userName", searchTerm),
                                    Filter.lessThan("userName", searchTerm + 'z')
                            )
                    )
            ).orderBy("created", Query.Direction.DESCENDING);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("Firestore error", error.getMessage());
                        return;
                    }
                    if(value.getDocumentChanges().size()==0){
                        SignalManager.getInstance().toast("No results");
                        communityPostList.clear();
                        myAdapter.notifyDataSetChanged();
                        return;
                    }
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            communityPostList.add(dc.getDocument().toObject(CommunityPost.class));
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void showAllPosts() {
        firebaseManager.getRefCommunityPostsCollection()
                .orderBy("created", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                communityPostList.add(dc.getDocument().toObject(CommunityPost.class));
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}