import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { LoginService } from '../../services/login.service';
import { OrderShoesDTO } from '../../dtos/ordershoes.dto';
import { OrderShoesService } from '../../services/order-shoes.service';
import { ShoeService } from '../../services/shoe.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cart-modal',
  templateUrl: './cartModal.component.html',
  styleUrls: ['../../../assets/css/style.css']
})

export class CartModalComponent implements OnInit {
  cartItems: any[] = [];
  subtotal: number = 0;
  @Output() checkout = new EventEmitter<void>();

  constructor(
    public activeModal: NgbActiveModal,
    public loginService:LoginService,
    private orderShoesService: OrderShoesService,
    public shoeService: ShoeService,
    private router: Router,
    )
  {}

  ngOnInit():void {

    this.loginService.reqIsLogged();
    
    const userId=this.loginService.user?.id;
    
    if(!userId){
      console.error("No user ID found. User might not be logged in.");
      return;
    }

    this.orderShoesService.getCartByUserId(userId)
    .subscribe({
      next : (cart: OrderShoesDTO)=>{
        this.cartItems = cart.orderItems||[];
        this.subtotal = cart.summary;
        console.log('sumatory: ', this.subtotal);
      },
      error : (err)=>{
        console.log("Error fetching cart items: ", err);
      }
    })
  }
  
  // Método para cerrar el modal
  closeModal() {
    this.activeModal.dismiss();

    const cartButton = document.getElementById('cart-button');
    if (cartButton) {
      cartButton.focus();
    }
  }

  // Método para proceder al checkout
  proceedToCheckout() {
    this.activeModal.close('checkout');
    this.router.navigate(['/checkout']);
  }
}