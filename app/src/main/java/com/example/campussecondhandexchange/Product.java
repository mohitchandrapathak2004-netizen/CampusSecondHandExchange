package com.example.campussecondhandexchange;

import java.io.Serializable;

public class Product implements Serializable {
    private String productId;
    private String itemName;
    private String category;
    private String price;
    private String condition;
    private String description;
    private String imageUrl;
    private String sellerId;
    private String sellerName;
    private String sellerPhone;

    // Empty constructor required for Firebase Firestore
    public Product() {}

    public Product(String productId, String itemName, String category, String price, String condition,
                   String description, String imageUrl, String sellerId, String sellerName, String sellerPhone) {
        this.productId = productId;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.condition = condition;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
    }

    // Getters
    public String getProductId() { return productId; }
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public String getPrice() { return price; }
    public String getCondition() { return condition; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public String getSellerPhone() { return sellerPhone; }
}