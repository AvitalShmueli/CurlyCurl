package com.example.curlycurl;

import com.example.curlycurl.Models.CommunityPost;
import com.example.curlycurl.Models.Product;
import com.example.curlycurl.Models.User;
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
    private DocumentReference refProduct;
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

    public FirebaseFirestore getDBRef() {
        return db;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseUser getmUser() {
        return mUser;
    }

    public CollectionReference getRefUsersCollection() {
        return refUsersCollection;
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
        //DocumentReference ref = refUsersCollection.document(user.getUserId());
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

    public boolean isUserConnected() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        return mUser != null;
    }

    public CollectionReference getRefProductsCollection() {
        return refProductsCollection;
    }

    public void createNewProductInDB(Product product) {
        //Log.d(TAG, "owner uuid: " + mUser.getUid() + " | " + product.getOwnerUID());
        //DocumentReference ref = db.collection("users").document(mUser.getUid());
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
        refCommunityPostsCollection.document(post.getPostId()).set(docCommunityPost);
        //refCommunityPostsCollection.document(post.getPostId()).set(post);
    }


    public CollectionReference getRefCommunityPostsCollection() {
        return refCommunityPostsCollection;
    }

    public Query usersProduct() {
        return refProductsCollection.whereEqualTo("ownerUID", mUser.getUid()).orderBy("created", Query.Direction.DESCENDING);
    }


    public void signOut() {
        instance = null;
    }


}
