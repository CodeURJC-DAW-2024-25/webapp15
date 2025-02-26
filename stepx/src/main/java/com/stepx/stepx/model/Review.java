package com.stepx.stepx.model;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.TemporalType;

import java.time.LocalDate;
import jakarta.persistence.Transient;

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

    @Transient
    private String formattedDate;

    @Column(name = "date")
    private LocalDate date;

   

    // relacion con el zapato, un zapato tiene varias reviews
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

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public LocalDate getDate() {
        return date;
    }

}
