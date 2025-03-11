package com.stepx.stepx.model;


import java.time.LocalDate;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;



import java.util.List;

import java.util.stream.IntStream;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;

    @Column(name = "date")
    private LocalDate date;

   

    
    //A shoe has numerous reviews, making 1:N
    @ManyToOne
    @JoinColumn(name = "shoe_id")
    private Shoe shoe;

    // Getters y setters

    public Review() {

    }

    public Review(int rating, String description, Shoe shoe, User user, LocalDate date) {
        this.rating = rating;
        this.description = description;
        this.shoe = shoe;
        this.user = user;
        this.date = LocalDate.now();
    }

    public List<Integer> getStars() {
        return IntStream.rangeClosed(1, rating).boxed().collect(Collectors.toList());
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDate getDate() {
        return date;
    }

}
