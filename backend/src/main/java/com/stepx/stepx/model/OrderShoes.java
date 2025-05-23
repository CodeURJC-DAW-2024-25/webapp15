package com.stepx.stepx.model;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class OrderShoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    // Relating with OrderItem (Correcting name from list)
    @OneToMany(mappedBy = "orderShoes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>(); // Before it was like orderShoes, fixed to orderItems

    private String cuponUsed;
    // From checkout form
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

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    public OrderShoes() {
    }

    public OrderShoes(User user) {
        this.user = user;
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

    public void setActualDate() {
        this.date = LocalDate.now();
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

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
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

    public void setSummary(BigDecimal summary2) {
        this.summary = summary2;
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
        // Check if the she already exists with the same size at the order
        for (OrderItem item : orderItems) {
            if (item.getShoe().equals(shoe) && item.getSize().equals(size)) {
                item.setQuantity(item.getQuantity() + quantity);
                item.setPrice(shoe.getPrice());
                return;
            }
        }

        OrderItem newItem = new OrderItem(this, shoe, quantity, size);
        newItem.setPrice(shoe.getPrice());
        newItem.setShoeName(shoe.getName());
        orderItems.add(newItem);
    }

    public int getLenghtOrderShoes() {
        return this.orderItems.size();
    }

    public List<OrderItem> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItemsList){
        this.orderItems=orderItemsList;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : this.orderItems) {
            BigDecimal itemTotal = item.getShoe().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        return total;
    }

}
