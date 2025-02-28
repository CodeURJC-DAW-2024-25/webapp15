package com.stepx.stepx.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.TemporalType;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import jakarta.persistence.Transient;

@Entity
public class OrderItem {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderShoes orderShoes;

    @ManyToOne
    @JoinColumn(name = "shoe_id")
    private Shoe shoe;

    @Column(nullable = false)
    private int quantity;  // Cantidad del mismo zapato

    @Column(nullable = false)
    private String size;   // Talla del zapato

    
    public OrderItem() {}

    public OrderItem(OrderShoes orderShoes, Shoe shoe, int quantity, String size) {
        this.orderShoes = orderShoes;
        this.shoe = shoe;
        this.quantity = quantity;
        this.size = size;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderShoes getOrderShoes() {
        return orderShoes;
    }

    public void setOrderShoes(OrderShoes orderShoes) {
        this.orderShoes = orderShoes;
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    
}
