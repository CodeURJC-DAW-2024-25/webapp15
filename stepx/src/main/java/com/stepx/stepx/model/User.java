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


    @OneToMany(mappedBy = "user", cascade=CascadeType.ALL,orphanRemoval = false)
    private List<OrderShoes> orders =new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String username;


    public User() {
    }

    public User(String username, Blob imageUser) {
        this.username = username;
        this.imageUser = imageUser;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + "]";
    }

    public void addOrderShoe(OrderShoes orderShoes){
        this.orders.add(orderShoes);
    }
}
