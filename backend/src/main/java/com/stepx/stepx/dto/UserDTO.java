package com.stepx.stepx.dto;

import java.sql.Blob;
import java.util.List;


public class UserDTO {

    private Long id;
    private boolean imageBool;
    private String imageUser;
    private String firstname;
    private String lastName;
    private List<String> roles;
    private String username;
    private String email;
    private List<OrderShoesDTO> orders;

    // Constructors
    public UserDTO() {
    }

    public UserDTO(Long id, String imageUser, String firstname, String lastName, List<String> roles, String username, String email, List<OrderShoesDTO> orders) {
        this.id = id;
        this.imageUser = imageUser;
        this.firstname = firstname;
        this.lastName = lastName;
        this.roles = roles;
        this.username = username;
        this.email = email;
        this.orders = orders;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUser() {
        return imageUser;
    }

    public void setImageUser(String imageUser) {
        this.imageUser = imageUser;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<OrderShoesDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderShoesDTO> orders) {
        this.orders = orders;
    }

    // toString method
    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", imageUser=" + imageUser +
                ", firstname='" + firstname + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + roles +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", orders=" + orders +
                '}';
    }
}