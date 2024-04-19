package com.example.curlycurl.Models;

import java.util.List;

public class User {

    public enum CurlType {
        _2A, _2B, _2C, _3A, _3B, _3C, _4A, _4B, _4C
    }

    private String username;
    private String email;
    private String userId;
    private String city;
    private CurlType curlType;
    private String imageURL;
    private List<String> all_products = null; //product id only


    public User() {
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public User setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getCity() {
        return city;
    }

    public User setCity(String city) {
        this.city = city;
        return this;
    }

    public CurlType getCurlType() {
        return curlType;
    }

    public User setCurlType(CurlType curlType) {
        this.curlType = curlType;
        return this;
    }

    public String getImageURL() {
        return imageURL;
    }

    public User setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public List<String> getAll_products() {
        return all_products;
    }

    public User setAll_products(List<String> all_products) {
        this.all_products = all_products;
        return this;
    }


}
