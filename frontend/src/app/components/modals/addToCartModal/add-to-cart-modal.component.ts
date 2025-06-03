import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ShoeDTO } from '../../../dtos/shoe.dto';
import { ShoeService } from '../../../services/shoe.service';
import { LoginService } from '../../../services/login.service';
import { OrderShoesService } from '../../../services/order-shoes.service';
import { OrderItemDTO } from '../../../dtos/orderitem.dto';

@Component({
  selector: 'app-add-to-cart-modal',
  templateUrl: './add-to-cart-modal.component.html',
})
export class AddToCartModalComponent {
  @Input() shoe!: ShoeDTO;

  constructor(
  public activeModal: NgbActiveModal,
  public shoeService: ShoeService,
  private loginService: LoginService,
  private orderShoesService: OrderShoesService
) {}


  confirmAddToCart() {
  const userId = this.loginService.user?.id;

  if (!userId) {
    alert("Debes iniciar sesión para añadir al carrito.");
    return;
  }

  const selectedSize = 'M'; // lógica rápida
  const quantity = 1;

  // 1. Obtener el carrito actual
  this.orderShoesService.getCartByUserId(userId).subscribe({
    next: (cart) => {
      const updatedCart = { ...cart };

      // 2. Ver si ya existe un item con ese zapato y talla
      const existingItem = updatedCart.orderItems?.find(item =>
        item.shoeId === this.shoe.id && item.size === selectedSize
      );

      if (existingItem) {
        // Solo actualiza cantidad
        existingItem.quantity += quantity;
      } else {
        // Crea nuevo item
        const newItem: OrderItemDTO = {
          orderId: updatedCart.id,
          shoeId: this.shoe.id,
          shoeName: this.shoe.name,
          quantity: quantity,
          size: selectedSize,
          price: this.shoe.price
        };

        updatedCart.orderItems = [...(updatedCart.orderItems || []), newItem];
      }

      // ⚠️ Asegurar que el estado sea el adecuado para el carrito (por ejemplo "notFinished")
      updatedCart.state = 'notFinished';

      // 3. Enviar actualización del carrito
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
