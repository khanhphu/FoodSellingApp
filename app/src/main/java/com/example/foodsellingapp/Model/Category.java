package com.example.foodsellingapp.Model;

public class Category {
    private String name;
    private String cateId;
    private int url;
    public Category(){

    }

    public Category(String name, String cateId) {

        this.name = name;
        this.cateId = cateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }
}
