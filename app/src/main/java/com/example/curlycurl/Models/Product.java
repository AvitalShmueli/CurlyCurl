package com.example.curlycurl.Models;

public class Product {
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
        MASK

    }

    private String productId;

    private String productName;
    private ProductType productType;
    private ProductCondition condition;
    private String description;
    private String city;
    private String imageURL;
    private String OwnerUID;

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
        return OwnerUID;
    }

    public Product setOwnerUID(String ownerUID) {
        OwnerUID = ownerUID;
        return this;
    }
}
