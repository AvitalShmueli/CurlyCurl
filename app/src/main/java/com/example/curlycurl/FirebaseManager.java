package com.example.curlycurl;

import com.example.curlycurl.Models.Product;
import com.example.curlycurl.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private User connectedUser;
    private CollectionReference refUsersCollection;
    private DocumentReference refUser;
    private CollectionReference refProductsCollection;
    private DocumentReference refProduct;

    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            refUsersCollection =  db.collection("users");
            refUser = db.collection("users").document(mUser.getUid());
            refProductsCollection = db.collection("products");
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

    public DocumentReference getRefUser() {
        return refUser;
    }

    public void createUsersProfileInDB(FirebaseUser user) {

        connectedUser = new User().
                setUsername(user.getDisplayName())
                .setEmail(user.getEmail())
                .setUserId(user.getUid());

        db.collection("users").document(user.getUid()).set(connectedUser);
    }

    public void updateUserProfile(User user) {
        DocumentReference ref = db.collection("users").document(user.getUserId());
        ref.update(
                "username",
                user.getUsername(),
                "email",
                user.getEmail(),
                "curlType",
                user.getCurlType(),
                "city",
                user.getCity());
    }

    public User getConnectedUser() {
        return connectedUser;
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
        refUser.update("all_products", FieldValue.arrayUnion(product));
        refProductsCollection.document(product.getProductId()).set(product);
    }

    public void signOut(){
        instance = null;
    }

}
