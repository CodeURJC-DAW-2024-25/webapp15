package com.stepx.stepx.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Shoe{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String shortDescription;
    private String longDescription;
    private int price;
    private String brand;

    @ElementCollection
    private List<String> images = new ArrayList<>();

    @ElementCollection
    private List<Integer> stock = new ArrayList<>();

    private String category;
    private String defaultSize;

    public Shoe() {
    }

    public Shoe(String name, String shortDescription, String longDescription, int price, String brand, List<String> images, List<Integer> stock, String category, String defaultSize) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.price = price;
        this.brand = brand;
        this.images = images;
        this.stock = stock;
        this.category = category;
        this.defaultSize = defaultSize;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Integer> getStock() {
        return stock;
    }

    public void setStock(List<Integer> stock) {
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(String defaultSize) {
        this.defaultSize = defaultSize;
    }
}