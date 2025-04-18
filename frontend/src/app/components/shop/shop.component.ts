import { Component,OnInit } from "@angular/core";
//import { CommonModule } from '@angular/common';
import { ShoeService } from '../../services/shoe.service';
import { ShoeDTO } from '../../dtos/shoe.dto';
//import { ShoeCardComponent } from '../shop/shoe-card.component';
//import { response } from "express";

@Component({
    selector: 'app-shop',
    templateUrl: './shop.component.html',
    styleUrls: ['../../../assets/css/style.css']
})

//cerebro de la página shop
export class ShopComponent implements OnInit {

    shoes : ShoeDTO[] = []; // Array to hold the list of shoes

    currentPage: number = 0; // Current page number for pagination

    hasMoreShoes: boolean = true; // Flag to check if there are more shoes to load
    
    selectedBrand: string |null = null; // Variable to hold the selected brand for filtering
    
    selectedCategory : string |null = null; // Variable to hold the selected category for filtering

    constructor(private shoeService: ShoeService) { } // Injecting the ShoeService
    
    ngOnInit(): void {
        this.loadShoes(); // Load shoes when the component initializes
    }

    loadShoes(): void {

        if(!this.selectedBrand && !this.selectedCategory){
            this.shoeService.getShoes(this.currentPage).subscribe( response =>{
                this.shoes.push(...response.content); // Append the new shoes to the existing array
                this.hasMoreShoes = !response.last; // Check if it is the last page of results
                this.currentPage++; // Increment the current page number for the next load})
            });
            return;
        }

        if(this.selectedBrand){
            this.shoeService.getShoesByBrand(this.currentPage,this.selectedBrand).subscribe(response =>{
                this.shoes.push(...response.content);
                this.hasMoreShoes = !response.last; // Check if there are more shoes to load
                this.currentPage++; // Increment the current page number for the next load
            });
            return;
        }

        if(this.selectedCategory){
            this.shoeService.getShoesByCategory(this.currentPage,this.selectedCategory).subscribe(response =>{
                this.shoes.push(...response.content);
                this.hasMoreShoes = !response.last; // Check if there are more shoes to load
                this.currentPage++; // Increment the current page number for the next load
            });
            return;
        }
        
    }

    filterByBrand(brand: string): void {
        this.resetState();
        this.selectedBrand = brand.toUpperCase() === 'ALL' ? null : brand;
        this.loadShoes();
    }
      
    filterByCategory(category: string): void {
        this.resetState();
        this.selectedCategory = category.toUpperCase() === 'ALL' ? null : category;
        this.loadShoes();
    }
      
      /* factoriza el reseteo para no repetir código */
    private resetState(): void {
        this.shoes = [];
        this.currentPage = 0;
        this.hasMoreShoes = true;
    }
}