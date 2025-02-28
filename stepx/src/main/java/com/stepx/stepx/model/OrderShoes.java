package com.stepx.stepx.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
public class OrderShoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    // Relación con OrderItem (Corrigiendo el nombre de la lista)
    @OneToMany(mappedBy = "orderShoes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>(); // Antes estaba como orderShoes, lo corregí a orderItems


    private String cuponUsed;
    //del forumulario de checkout
    private String country;
    private String firstName;
    private String secondName;
    private String email;
    private String address;
    private String numerPhone;
    private BigDecimal summary;
    private String state;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public OrderShoes(){}

    public OrderShoes(User user){
        this.user=user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCuponUsed() {
        return cuponUsed;
    }

    public void setCuponUsed(String cuponUsed) {
        this.cuponUsed = cuponUsed;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumerPhone() {
        return numerPhone;
    }

    public void setNumerPhone(String numerPhone) {
        this.numerPhone = numerPhone;
    }

    public BigDecimal getSummary() {
        return summary;
    }

    public void setSummary(BigDecimal summary) {
        this.summary = summary;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void addItem(Shoe shoe, int quantity, String size) {
        // Revisamos si ya existe el zapato con la misma talla en la orden
        for (OrderItem item : orderItems ) {
            if (item.getShoe().equals(shoe) && item.getSize().equals(size)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        OrderItem newItem = new OrderItem(this, shoe, quantity, size);
        orderItems .add(newItem);
    }

    


    
}
