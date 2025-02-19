package com.stepx.stepx.model;

import java.sql.Blob;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long productCode = null; //Codigo de producto
	
    @Column(nullable = false)
	private String category;
    
    @Column(nullable = false)
    private String shortDescription;

    @Column(nullable = false)
    private String extendedDescription;
	
	@Column(nullable = false)
	private Double price;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private Blob[] images;

    @Column(nullable = false)
    private Review[] reviews;

    public Product(){}
        public Product(String category, Double price, String brand, int stock, Blob[] images, String shortDescription, String extendedDescription){
            this.category = category;
            this.price = price;
            this.brand = brand;
            this.stock = stock;
            this.images = images;
            this.shortDescription = shortDescription;
            this.extendedDescription = extendedDescription;
        }

	public Long getProductCode() {
		return this.productCode;
	}

    public void setProductCode(Long productCode){
        this.productCode = productCode;
    }

	public String getCategory() {
		return this.category;
	}

    public void setCategory(String category){
        this.category = category;
    }

    public Double getPrice(){
        return this.price;
    }

    public void setPrice(Double price){
        this.price = price;
    }

    public String getBrand(){
        return this.brand;
    }

    
    public void setBrand(String brand){
        this.brand = brand;
    }

    public int getStock(){
        return this.stock;
    }

    
    public void setStock(int stock){
        this.stock = stock;
    }

    public Blob[] getImages(){
        return this.images;
    }

    public void setImages(Blob[] images){
        this.images = images;
    }

    public void setImage(int ref, Blob image){ //Para cambiar imagen específica
        this.images[ref] = image;
    }
    
    public Blob getImage(int ref){
        return this.images[ref];
    }

	public String getShortDescription() {
		return this.shortDescription;
	}

    public void setShortDescription(String shortDescription){
        this.shortDescription = shortDescription;
    }

    public String getExtendedDescription() {
		return this.extendedDescription;
	}

    public void setExtendedDescription(String extendedDescription){
        this.extendedDescription = extendedDescription;
    }

	@Override
	public String toString() {
		return "Shoe [id=" + this.productCode + ", category=" + this.category + ", brand=" + this.brand +", short description=" + this.shortDescription + "]";
	}

    
}

