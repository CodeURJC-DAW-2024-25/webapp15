package com.stepx.stepx.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Shoe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String shortdescription;
    
    @Lob
    @Column(columnDefinition = "TEXT") 
    private String longDescription;
    
    private BigDecimal price;

    //Saving images as Blobs 
    @Lob
    @Column(name = "image1")
    private Blob image1;

    @Lob
    @Column(name = "image2")
    private Blob image2;

    @Lob
    @Column(name = "image3")
    private Blob image3;

    @Enumerated(EnumType.STRING)
    private Brand brand;

    @Enumerated(EnumType.STRING)
    private Category category;

    // A shoe has stock to be related to many sizes    
    @OneToMany(mappedBy = "shoe", cascade = CascadeType.ALL, orphanRemoval = true)//relation with shoessizestock
    private List<ShoeSizeStock> sizeStocks = new ArrayList<>();

    @OneToMany(mappedBy = "shoe", cascade = CascadeType.ALL, orphanRemoval = true)//relationWith reviews
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "shoe", cascade = CascadeType.ALL, orphanRemoval = true)//relation with orderItem
    private List<OrderItem> orderItems = new ArrayList<>();

    // Getters y setters
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

    public String getDescription() {
        return shortdescription;
    }

    public void setDescription(String description) {
        this.shortdescription = description;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Blob getImage1() {
        return image1;
    }

    public void setImage1(Blob image1) {
        this.image1 = image1;
    }

    public Blob getImage2() {
        return image2;
    }

    public void setImage2(Blob image2) {
        this.image2 = image2;
    }

    public Blob getImage3() {
        return image3;
    }

    public void setImage3(Blob image3) {
        this.image3 = image3;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ShoeSizeStock> getSizeStocks() {
        return sizeStocks;
    }

    // Encapsulated method to add a ShoeSizeStock
    public void addSizeStock(ShoeSizeStock sizeStock) {
        sizeStocks.add(sizeStock);
        sizeStock.setShoe(this); // Ensure bidirectional relationship is maintained
    }

    // Encapsulated method to remove a ShoeSizeStock
    public void removeSizeStock(ShoeSizeStock sizeStock) {
        sizeStocks.remove(sizeStock);
        sizeStock.setShoe(null); // Break the bidirectional relationship
    }

    // Define enums inside Shoe
    public enum Category {
        SPORT,
        CASUAL,
        URBAN
    }

    public enum Brand {
        NIKE,
        ADIDAS,
        PUMA,
        NEW_BALANCE,
    }
}