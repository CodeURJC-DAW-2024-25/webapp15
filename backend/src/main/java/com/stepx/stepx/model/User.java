package com.stepx.stepx.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "ImageProfile")
    private Blob imageUser;

    @Column(name = "Name")
    private String firstname;

    @Column(name = "LastName")
    private String lastName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coupon> coupons = new ArrayList<>();

    private String imageString;

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public String getFirstName() {
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

    public Blob getImageUser() {
        return imageUser;
    }

    public void setImageUser(Blob imageUser) {
        this.imageUser = imageUser;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> review;

    @Column(nullable = false)
    private String encodedPassword;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    // Relation between orders shopping
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.EAGER)
    private List<OrderShoes> orders = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    public User() {
    }

    public User(String username, String email, String encodedPassword, Blob imageUser, String... roles) {
        this.username = username;
        this.imageUser = imageUser;
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.roles = Arrays.asList(roles);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            // Evita duplicar el prefijo "ROLE_"
            String cleanRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            authorities.add(new SimpleGrantedAuthority(cleanRole));
        }
        return authorities;
    }
    

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isAdmin() {
        return roles.contains("ADMIN") || roles.contains("ROLES_ADMIN");
    }

    public boolean isRegularUser() {
        return roles.contains("USER") || roles.contains("ROLES_USER");
    }

    public static Object builder() {
        throw new UnsupportedOperationException("Unimplemented method 'builder'");
    }

    @Override
    public String getPassword() {
        return encodedPassword;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + "]";
    }

    public void addOrderShoe(OrderShoes orderShoes) {
        this.orders.add(orderShoes);
        orderShoes.setUser(this);
    }

    
}
