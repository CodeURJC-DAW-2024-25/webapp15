import { Component, OnInit } from '@angular/core';
import { OrderShoesService } from '../../../services/order-shoes.service';
import { ShoeService } from '../../../services/shoe.service';
import { HttpClient } from '@angular/common/http';
import { OrderShoesDTO } from '../../../dtos/ordershoes.dto';
import { OrderItemDTO } from '../../../dtos/orderitem.dto';


@Component({
  selector: 'app-cart-summary',
  templateUrl: './cart-summary.component.html',
  styleUrl: '../../../../assets/css/style.css',
})
export class CartSummaryComponent implements OnInit {

  orderShoe?: OrderShoesDTO;        // El carrito completo
  cartItems: OrderItemDTO[] = [];    // Solo los productos
  loading = true;

  constructor(
    private http: HttpClient,
    private orderShoesService: OrderShoesService,
    public shoeService: ShoeService
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart() {
    // Primero pedimos el usuario actual
    this.http.get<{ id: number }>('/api/v1/user/me', { withCredentials: true }).subscribe({
      next: (user) => {
        const userId = user.id;
        this.orderShoesService.getCartByUserId(userId).subscribe({
          next: (order) => {
            this.orderShoe = order;
            this.cartItems = order.orderItems || [];
            this.loading = false;
          },
          error: (err) => {
            console.error('Error al obtener el carrito:', err);
            this.loading = false;
          }
        });
      },
      error: (err) => {
        console.error('Error al obtener el usuario:', err);
        this.loading = false;
      }
    });
  }

  get subtotal(): number {
    return this.cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
  }

  increaseQuantity(item: OrderItemDTO) {
    item.quantity++;
  }

  decreaseQuantity(item: OrderItemDTO) {
    if (item.quantity > 1) {
      item.quantity--;
    }
  }

  removeItem(item: OrderItemDTO) {
    this.cartItems = this.cartItems.filter(ci => ci.id !== item.id);
  }

  recalculateTotal() {
    console.log('Recalculating total...');
    // Aquí se podría recalcular si en el futuro hay cupones o envíos pagos
  }
}
