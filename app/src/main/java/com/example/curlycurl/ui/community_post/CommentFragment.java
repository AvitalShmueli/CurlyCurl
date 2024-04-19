package com.example.curlycurl.ui.community_post;

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

import com.example.curlycurl.Adapters.MyCommentRecyclerViewAdapter;
import com.example.curlycurl.Models.Comment;
import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.R;
import com.example.curlycurl.Utilities.FirebaseManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private FirebaseManager firebaseManager;
    private List<Comment> commentsList;
    private MyCommentRecyclerViewAdapter myAdapter;
    private CommunityPost post = null;


    public CommentFragment() {
    }

    public CommentFragment(CommunityPost post) {
        this.post = post;
    }

    @SuppressWarnings("unused")
    public static CommentFragment newInstance(int columnCount) {
        CommentFragment fragment = new CommentFragment();
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
        View view = inflater.inflate(R.layout.fragment_comments_list, container, false);

        firebaseManager = FirebaseManager.getInstance();

        commentsList = new ArrayList<>();
        myAdapter = new MyCommentRecyclerViewAdapter(commentsList);

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
            recyclerView.setAdapter(myAdapter);
        }
        return view;
    }

    private void EventChangeListener() {
        commentsList.clear();
        firebaseManager.getPostComments(post).orderBy("created", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        commentsList.add(dc.getDocument().toObject(Comment.class));
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}