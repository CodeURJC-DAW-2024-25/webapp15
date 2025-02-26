package com.stepx.stepx.model;

import jakarta.persistence.*;

@Entity
public class ShoeSizeStock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación con el zapato
    @ManyToOne
    @JoinColumn(name = "shoe_id")
    private Shoe shoe;
    
    // Se puede usar String para manejar tallas como "42", "M", etc.
    private String size;
    
    private Integer stock;
    
    // Getters y setters

    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public Shoe getShoe() {
        return shoe;
    }
 
    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }
 
    public String getSize() {
        return size;
    }
 
    public void setSize(String size) {
        this.size = size;
    }
 
    public Integer getStock() {
        return stock;
    }
 
    public void setStock(Integer stock) {
        this.stock = stock;
    }

   
}
