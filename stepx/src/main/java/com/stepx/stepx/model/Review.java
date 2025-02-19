
package com.stepx.stepx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.*;

@Entity

public class Review {
    @Column(nullable = false)
    private int rating;

   // @Column(nullable = false)
   // private User user;

   @Column(nullable = false)
   private String description;

   @Column(nullable = false)
    private LocalDate date;

    public Review(){}

    public Review(int rating, String description, LocalDate date){ //Falta agregar User
        this.rating = rating;
        this.description = description;
        this.date = date;
    }

    public int getRating(){
        return this.rating;
    }

    public void setRating(int rating){
        this.rating = rating;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescriprion(String description){
        this.description = description;
    }

    public LocalDate getDate(){
        return this.date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }

    @Override
	public String toString() {
		return "Review [rating=" + this.rating + ", date=" + this.date + ", description=" + this.description + "]";
	}
}
