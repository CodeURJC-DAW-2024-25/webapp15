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
 	private List<Shoe> shoes;


	public Order() {}

	public Order(LocalDate date, List<Shoe> shoes) {
		this.date = date;
		this.shoes = shoes;
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

	public List<Shoe> getShoes() {
		return this.shoes;
	}

	public void setShoes(List<Shoe> shoes) {
		this.shoes = shoes;
	}

	@Override
	public String toString() {
		return "Order [id=" + this.id + ", date=" + this.date + ", number of shoes=" + this.shoes.size() + "]";
	}

}