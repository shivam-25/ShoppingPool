package com.example.android.shoppingpool.models;

public class BestBuyProducts {

    String category,proId, promoted;

    public BestBuyProducts()
    {
        this.category = null;
        this.proId = null;
        this.promoted = null;
    }

    public BestBuyProducts(String category, String proId, String promoted) {
        this.category = category;
        this.proId = proId;
        this.promoted = promoted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getPromoted() {
        return promoted;
    }

    public void setPromoted(String promoted) {
        this.promoted = promoted;
    }
}
