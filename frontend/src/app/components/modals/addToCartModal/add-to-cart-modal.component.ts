import { Component, Input } from '@angular/core';
import { NgbActiveModal, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { ShoeDTO } from '../../../dtos/shoe.dto';
import { ShoeService } from '../../../services/shoe.service';
import { LoginService } from '../../../services/login.service';
import { OrderShoesService } from '../../../services/order-shoes.service';
import { OrderItemDTO } from '../../../dtos/orderitem.dto';
import { ShoeSizeStockService } from '../../../services/shoesizestock.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-add-to-cart-modal',
  standalone: true,
  templateUrl: './add-to-cart-modal.component.html',
  imports: [
    CommonModule,
    NgbModalModule,
  ]
})
export class AddToCartModalComponent {
  @Input() shoe!: ShoeDTO;
  stockAvailable: boolean | null = null;

  selectedSize = 'M';
  quantity = 1;
  constructor(
  public  activeModal       : NgbActiveModal,
    public  shoeService       : ShoeService,
    private loginService      : LoginService,
    private orderShoesService : OrderShoesService,
    private stockService      : ShoeSizeStockService
) {}

ngOnInit(): void {
  const shoeId = this.shoe.id;

  /* ①  get stock */
  this.stockService.checkStock([shoeId], [this.selectedSize]).subscribe({
    next: map => {
      const key = `${shoeId}_${this.selectedSize}`;
      const available = map[key] ?? 0;

    
      this.stockAvailable = available >= this.quantity;
      console.log('stockAvailable dentro del subscribe ➜', this.stockAvailable);
               
    },
    error: () => {
      this.stockAvailable = false;
      console.error('Error consultando stock');
    }
  });

  console.log('stockAvailable justo después de llamar ➜', this.stockAvailable);
}


  confirmAddToCart() {
  if (!this.stockAvailable) { return; }  
  const userId = this.loginService.user?.id;

  if (!userId) {
    alert("Debes iniciar sesión para añadir al carrito.");
    return;
  }

  // 1. get  cart 
  this.orderShoesService.getCartByUserId(userId).subscribe({
    next: (cart) => {
      const updatedCart = { ...cart };

      // 2. check if an order item already exist 
      const existingItem = updatedCart.orderItems?.find(item =>
        item.shoeId === this.shoe.id && item.size === this.selectedSize
      );

      if (existingItem) {
        // update quantity
        existingItem.quantity += this.quantity;
      } else {
        // vreate new order item
        const newItem: OrderItemDTO = {
          orderId: updatedCart.id,
          shoeId: this.shoe.id,
          shoeName: this.shoe.name,
          quantity: this.quantity,
          size: this.selectedSize,
          price: this.shoe.price
        };

        updatedCart.orderItems = [...(updatedCart.orderItems || []), newItem];
      }

      // make sure the state is appropriate for the cart (e.g., "notFinished")
      updatedCart.state = 'notFinished';

      // 3.send cart update
      this.orderShoesService.updateOrderShoe(updatedCart.id, updatedCart).subscribe({
        next: () => {
          console.log("Zapato añadido al carrito.");
          this.activeModal.close('added');
        },
        error: (err) => {
          console.error("Error actualizando carrito:", err);
          alert("No se pudo añadir al carrito.");
        }
      });
    },
    error: (err) => {
      console.error("Error obteniendo el carrito:", err);
      alert("Error obteniendo carrito del usuario.");
    }
  });
}


  cancel() {
    this.activeModal.dismiss('cancel');
  }
}
