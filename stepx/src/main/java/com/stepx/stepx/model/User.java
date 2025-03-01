package com.stepx.stepx.model;

import jakarta.persistence.*;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "ImageProfile")
    private Blob imageUser;

    public Blob getImageUser() {
        return imageUser;
    }

    public void setImageUser(Blob imageUser) {
        this.imageUser = imageUser;
    }

    @OneToMany(mappedBy = "user")
    private List<Review> review;


    // Relación con órdenes de compra
    @OneToMany(mappedBy = "user", cascade=CascadeType.ALL,orphanRemoval = false,fetch = FetchType.EAGER)
    private List<OrderShoes> orders =new ArrayList<>();


    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    public User() {
    }

    public User(String username,String email, Blob imageUser) {
        this.username = username;
        this.imageUser = imageUser;
        this.email=email;
    }

    public Long getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
        
    }public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + "]";
    }

    public void addOrderShoe(OrderShoes orderShoes){
        this.orders.add(orderShoes);
    }
}
