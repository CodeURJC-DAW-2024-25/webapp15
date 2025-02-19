package com.stepx.stepx.model;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.time.*;

@Entity
public class Order {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;
	
	@Column(nullable = false)
	private LocalDate date;    

	@ManyToMany(mappedBy="shops")
 	private List<Product> products;


	public Order() {}

	public Order(LocalDate date, List<Product> products) {
		this.date = date;
		this.products = products;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<Product> getProducts() {
		return this.products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "Order [id=" + this.id + ", date=" + this.date + ", number of products=" + this.products.size() + "]";
	}

}
