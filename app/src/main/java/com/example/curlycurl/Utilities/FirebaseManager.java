package com.example.curlycurl.Utilities;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.curlycurl.Models.Comment;
import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private CollectionReference refUsersCollection;
    private DocumentReference refUser;
    private CollectionReference refProductsCollection;
    private CollectionReference refCommunityPostsCollection;


    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            refUsersCollection = db.collection("Users");
            refUser = db.collection("Users").document(mUser.getUid());
            refProductsCollection = db.collection("Products");
            refCommunityPostsCollection = db.collection("CommunityPosts");
        }
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null)
            instance = new FirebaseManager();
        return instance;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseUser getmUser() {
        return mUser;
    }

    public DocumentReference getRefCurrentUser() {
        return refUser;
    }

    public DocumentReference getRefUser(String userId) {
        return refUsersCollection.document(userId);
    }

    public void createUsersProfileInDB(FirebaseUser user) {

        User connectedUser = new User().
                setUsername(user.getDisplayName())
                .setEmail(user.getEmail())
                .setUserId(user.getUid());

        refUsersCollection.document(user.getUid()).set(connectedUser);
    }

    public void updateUserProfile(User user) {
        DocumentReference ref = refUsersCollection.document(user.getUserId());
        ref.update(
                "username",
                user.getUsername(),
                "email",
                user.getEmail(),
                "curlType",
                user.getCurlType(),
                "city",
                user.getCity(),
                "imageURL",
                user.getImageURL()
        );
    }

    public CollectionReference getRefProductsCollection() {
        return refProductsCollection;
    }

    public void createNewProductInDB(Product product) {
        refProductsCollection.document(product.getProductId()).set(product);
        refUser.update("all_products", FieldValue.arrayUnion(product.getProductId()));
    }

    public void updateProductInDB(Product product) {
        DocumentReference ref = refProductsCollection.document(product.getProductId());
        ref.update(
                "productName",
                product.getProductName(),
                "productType",
                product.getProductType(),
                "condition",
                product.getCondition(),
                "description",
                product.getDescription(),
                "imageURL",
                product.getImageURL(),
                "city",
                product.getCity(),
                "tags",
                product.getTags(),
                "modified",
                new Timestamp(new Date())
        );
    }

    public void createNewCommunityPostInDB(CommunityPost post) {
        Map<String, Object> docCommunityPost = new HashMap<>();
        docCommunityPost.put("authorUID", post.getAuthorUID());
        docCommunityPost.put("userName", post.getUserName());
        docCommunityPost.put("created", post.getCreated());
        docCommunityPost.put("city", post.getCity());
        docCommunityPost.put("imageURL", post.getImageURL());
        docCommunityPost.put("post", post.getPost());
        docCommunityPost.put("postId", post.getPostId());
        docCommunityPost.put("tags", post.getTags());
        refCommunityPostsCollection.document(post.getPostId()).set(docCommunityPost);
    }

    public void updateCommunityPostInDB(CommunityPost post) {
        DocumentReference ref = refCommunityPostsCollection.document(post.getPostId());
        ref.update(
                "post",
                post.getPost(),
                "imageURL",
                post.getImageURL(),
                "city",
                post.getCity(),
                "tags",
                post.getTags(),
                "modified",
                new Timestamp(new Date())
        );
    }

    public void addCommentOnPost(CommunityPost post, Comment comment) {
        DocumentReference postRef = refCommunityPostsCollection.document(post.getPostId());
        Map<String, Object> docComment = new HashMap<>();
        docComment.put("comment", comment.getComment());
        docComment.put("commentId", comment.getCommentId());
        docComment.put("postId", post.getPostId());
        docComment.put("authorUID", comment.getAuthorUID());
        docComment.put("userName", comment.getUserName());
        docComment.put("created", comment.getCreated());
        postRef.collection("comments").document(comment.getCommentId()).set(docComment);
    }

    public CollectionReference getRefCommunityPostsCollection() {
        return refCommunityPostsCollection;
    }

    public CollectionReference getPostComments(CommunityPost post) {
        Log.d(TAG, "post to comments" + post.getPostId());
        DocumentReference postRef = refCommunityPostsCollection.document(post.getPostId());
        return postRef.collection("comments");
    }

    public Query usersProduct() {
        return refProductsCollection.whereEqualTo("ownerUID", mUser.getUid()).orderBy("created", Query.Direction.DESCENDING);
    }


    public void deleteProductFromDB(Product product) {
        refUser.update("all_products", FieldValue.arrayRemove(product.getProductId()));
        refProductsCollection.document(product.getProductId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Product successfully deleted!");
                SignalManager.getInstance().toast("Product successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting product", e);
            }
        });
    }


    public void signOut() {
        instance = null;
    }


}
