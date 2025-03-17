package com.stepx.stepx.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderShoesDTO {
    private Long id;
    private LocalDate date;
    private String cuponUsed;
    private String country;
    private String firstName;
    private String secondName;
    private String email;
    private String address;
    private String numerPhone;
    private BigDecimal summary;
    private String state;
    private UserDTO user;
    private CouponDTO coupon;
    private List<OrderItemDTO> orderItems;

    // Getters y Setters
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CouponDTO getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponDTO coupon) {
        this.coupon = coupon;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
