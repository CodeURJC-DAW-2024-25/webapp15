import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CartModalComponent } from '../cartModal/cartModal.component';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['../../../assets/css/style.css']
})
export class HeaderComponent { 

    admin = false;
    isAuthenticated = true;
    cartItems: any[] = []; // Para almacenar los items del carrito
    subtotal: number = 0; // Para el total del carrito
  
    constructor(private http: HttpClient,private modalService: NgbModal) {}

    openCartModal() {
        const modalRef = this.modalService.open(CartModalComponent, {
          centered: true
        });
      }
}
