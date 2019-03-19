package com.example.android.shoppingpool.models;

public class Jobs {

    private String shopName,id, date, experience, post, postings, salary;

    public Jobs(){

    }

    public Jobs(String shopName, String id, String date, String experience, String post, String postings, String salary) {
        this.shopName = shopName;
        this.id=id;
        this.date = date;
        this.experience = experience;
        this.post = post;
        this.postings = postings;
        this.salary = salary;
    }

    public String getShopName() {
        return shopName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPostings() {
        return postings;
    }

    public void setPostings(String postings) {
        this.postings = postings;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
