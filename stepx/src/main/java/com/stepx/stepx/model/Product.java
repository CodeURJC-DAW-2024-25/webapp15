//Product java

package com.stepx.stepx.model;

import java.util.ArrayList;

public class Product {
    private Long id;
    private String name;
    private String shortDescription;
    private String longDescription;
    private int price;
    private String brand;
    private ArrayList<String> images;
    private ArrayList<Integer>stock;
    private String category;
    private String size;


    public Product(){

    }

    public Product(String name, String shortDescription, String longDescription, int price, String brand, ArrayList<String> images, ArrayList<Integer> stock, String category, String size) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.price = price;
        this.brand = brand;
        this.images = images;
        this.stock = stock;
        this.category = category;
        this.size = size;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public int getPrice() {
        return price;
    }

    public String getBrand() {
        return brand;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public ArrayList<Integer> getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }
    public String getSize(){
        return size;
    }
}