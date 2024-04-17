package com.example.curlycurl.Models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {
    public enum ProductCondition {
        NEW,
        USED
    }

    public enum ProductType {
        SHAMPOO,
        CONDITIONER,
        CREME,
        GEL,
        GLAZE,
        MOUSSE,
        MASK,
        OTHER

    }

    private String productId;

    private String productName;
    private ProductType productType;
    private ProductCondition condition;
    private String description;
    private String city;
    private String imageURL;
    private String ownerUID;
    private String userName;
    private Timestamp created = new Timestamp(new Date());
    private Timestamp modified = new Timestamp(new Date());

    public Product() {
    }

    public String getProductId() {
        return productId;
    }

    public Product setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public Product setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public ProductType getProductType() {
        return productType;
    }

    public Product setProductType(ProductType productType) {
        this.productType = productType;
        return this;
    }

    public ProductCondition getCondition() {
        return condition;
    }

    public Product setCondition(ProductCondition condition) {
        this.condition = condition;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Product setCity(String city) {
        this.city = city;
        return this;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Product setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public Product setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
        return this;
    }

    public Timestamp getCreated() {
        return created;
    }

    public Product setCreated(Timestamp created) {
        this.created = created;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public Product setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Timestamp getModified() {
        return modified;
    }

    public Product setModified(Timestamp modified) {
        this.modified = modified;
        return this;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", OwnerUID='" + ownerUID + '\'' +
                ", userName='" + userName +
                '}';
    }
}
