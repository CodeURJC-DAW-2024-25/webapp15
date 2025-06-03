import { Component,OnInit } from "@angular/core";
//import { CommonModule } from '@angular/common';
import { ShoeService } from '../../services/shoe.service';
import { ShoeDTO } from '../../dtos/shoe.dto';
import { LoginService } from "../../services/login.service";
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

    selectedShoe?:ShoeDTO;

    isAuthenticated: boolean = false; // Flag to check if the user is authenticated

    isAdmin: boolean = false; // Flag to check if the user is an admin
    isUser: boolean = false; // Flag to check if the user is a regular user

    constructor(private shoeService: ShoeService,public loginService:LoginService) { } // Injecting the ShoeService
    
    ngOnInit(): void {
        this.loginService.reqIsLogged(); // Inicia la verificación asíncrona

        setTimeout(() => {
            // Ahora los valores deberían estar actualizados
            this.isAuthenticated = this.loginService.logged;
            this.isAdmin = this.loginService.user?.roles.includes('ROLE_ADMIN') ?? false;
            this.isUser = this.loginService.user?.roles.includes('ROLE_USER') ?? false;

            console.log("User is admin:", this.isAdmin);
            console.log("User is regular user:", this.isUser);

            // Ya puedes cargar los zapatos
            this.loadShoes();
        }, 300); // Espera corta para que reqIsLogged() tenga tiempo de completar
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

    onShoeDeleted(shoeId: number): void {
        console.log('Shoe deleted:', shoeId); // Log the deleted shoe ID
        this.shoes = this.shoes.filter(shoe => shoe.id !== shoeId); // Remove the deleted shoe from the list

    }
}