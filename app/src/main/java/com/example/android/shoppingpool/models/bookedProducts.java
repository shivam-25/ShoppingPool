package com.example.android.shoppingpool.models;

public class bookedProducts {

    String productName, category, variant, amount, dateOfBooking, timeOfBooking, soldBy, qty, productId, promoted;

    public bookedProducts(String productName, String category, String variant, String amount, String dateOfBooking, String timeOfBooking, String soldBy, String qty, String productId, String promoted ){
        this.productName = productName;
        this.category = category;
        this.variant = variant;
        this.amount = amount;
        this.dateOfBooking = dateOfBooking;
        this.timeOfBooking = timeOfBooking;
        this.soldBy = soldBy;
        this.qty = qty;
        this.productId = productId;
        this.promoted = promoted;
    }

    public String getPromoted() {
        return promoted;
    }

    public void setPromoted(String promoted) {
        this.promoted = promoted;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDateOfBooking() {
        return dateOfBooking;
    }

    public void setDateOfBooking(String dateOfBooking) {
        this.dateOfBooking = dateOfBooking;
    }

    public String getTimeOfBooking() {
        return timeOfBooking;
    }

    public void setTimeOfBooking(String timeOfBooking) {
        this.timeOfBooking = timeOfBooking;
    }

    public String getSoldBy() {
        return soldBy;
    }

    public void setSoldBy(String soldBy) {
        this.soldBy = soldBy;
    }
}
