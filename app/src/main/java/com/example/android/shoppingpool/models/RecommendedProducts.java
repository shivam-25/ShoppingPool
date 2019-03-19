package com.example.android.shoppingpool.models;

public class RecommendedProducts {

    String category, shopName, productName, productVariant, productImage, stockValue, discountPercent, price, offer, rating;

    public RecommendedProducts(){
        category = null;
        shopName = null;
        productName = null;
        productVariant = null;
        productImage = null;
        stockValue = null;
        discountPercent = null;
        price = null;
        offer = null;
        rating = null;
    }

    public RecommendedProducts(String category, String shopName, String productName, String productVariant, String productImage, String stockValue, String discountPercent, String price, String offer, String rating) {
        this.category = category;
        this.shopName = shopName;
        this.productName = productName;
        this.productVariant = productVariant;
        this.productImage = productImage;
        this.stockValue = stockValue;
        this.discountPercent = discountPercent;
        this.price = price;
        this.offer = offer;
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(String productVariant) {
        this.productVariant = productVariant;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getStockValue() {
        return stockValue;
    }

    public void setStockValue(String stockValue) {
        this.stockValue = stockValue;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
