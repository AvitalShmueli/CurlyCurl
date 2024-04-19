package com.example.curlycurl.Models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Comment {
    private String comment;
    private String commentId;
    private String postId;
    private String authorUID;
    private String userName;
    private Timestamp created = new Timestamp(new Date());
    private boolean isCollapsed = true;
    public final static int MAX_LINES_COLLAPSED = 3;

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public Comment setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getCommentId() {
        return commentId;
    }

    public Comment setCommentId(String commentId) {
        this.commentId = commentId;
        return this;
    }

    public String getPostId() {
        return postId;
    }

    public Comment setPostId(String postId) {
        this.postId = postId;
        return this;
    }

    public String getAuthorUID() {
        return authorUID;
    }

    public Comment setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public Comment setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Timestamp getCreated() {
        return created;
    }

    public Comment setCreated(Timestamp created) {
        this.created = created;
        return this;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public Comment setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
        return this;
    }
}
