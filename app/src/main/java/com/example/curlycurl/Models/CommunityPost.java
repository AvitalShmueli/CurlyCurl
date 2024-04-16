package com.example.curlycurl.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class CommunityPost {
    private String post;
    private String postId;
    private Timestamp created = new Timestamp(new Date());
    private String imageURL;
    private String authorUID;
    private String userName;
    private String city;
    private boolean isCollapsed = true;
    public final static int MAX_LINES_COLLAPSED = 6;

    public CommunityPost() {
    }

    public String getPost() {
        return post;
    }

    public CommunityPost setPost(String post) {
        this.post = post;
        return this;
    }

    public Timestamp getCreated() {
        return created;
    }

    public CommunityPost setCreated(Timestamp created) {
        this.created = created;
        return this;
    }

    public String getImageURL() {
        return imageURL;
    }

    public CommunityPost setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public String getPostId() {
        return postId;
    }

    public CommunityPost setPostId(String postId) {
        this.postId = postId;
        return this;
    }

    public String getAuthorUID() {
        return authorUID;
    }

    public CommunityPost setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
        return this;
    }

    public String getCity() {
        return city;
    }

    public CommunityPost setCity(String city) {
        this.city = city;
        return this;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public CommunityPost setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public CommunityPost setUserName(String userName) {
        this.userName = userName;
        return this;
    }
}
