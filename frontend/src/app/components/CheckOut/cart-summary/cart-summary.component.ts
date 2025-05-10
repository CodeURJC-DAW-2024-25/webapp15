import { Component, OnInit } from '@angular/core';
import { OrderShoesService } from '../../../services/order-shoes.service';
import { ShoeService } from '../../../services/shoe.service';
import { LoginService } from '../../../services/login.service';
import { OrderShoesDTO } from '../../../dtos/ordershoes.dto';
import { OrderItemDTO } from '../../../dtos/orderitem.dto';
import { ShoeSizeStockService } from '../../../services/shoesizestock.service';

interface CartItemView {
  item: OrderItemDTO;
  stockAvailable: boolean;  
}


@Component({
  selector: 'app-cart-summary',
  templateUrl: './cart-summary.component.html',
  styleUrl: '../../../../assets/css/style.css',
})
export class CartSummaryComponent implements OnInit {

  

  orderShoe?: OrderShoesDTO;        // El carrito completo
  cartItems: CartItemView[] = [];   // Solo los productos
  loading = true;

  constructor(
    private orderShoesService: OrderShoesService,
    public shoeService: ShoeService,
    public loginService: LoginService,
    private shoeSizeStockService: ShoeSizeStockService, // Inyectar el servicio de stock
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart() {
    
    const userId=this.loginService.user?.id;

    if(!userId){
      console.error("No user ID found. User might not be logged in.");
      this.loading = false;
      return;
    }

    this.orderShoesService.getCartByUserId(userId).subscribe({
      next: (order)=>{
        this.orderShoe = order;
        this.cartItems = (order.orderItems || []).map(item => ({
          item,
          stockAvailable: true // ✅ por defecto, se asume con stock hasta verificar
        }));
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al obtener el carrito:', err);
        this.loading = false;
      }
    })

  }

  get subtotal(): number {
    return this.cartItems.reduce((total, cart) => {
      return cart.stockAvailable ? total + (cart.item.price * cart.item.quantity) : total;
    }, 0);
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
    this.cartItems = this.cartItems.filter(ci => ci.item.id !== item.id);
  }

  recalculateTotal() {
    const shoeIds = this.cartItems.map(cart => cart.item.shoeId);
    const sizes = this.cartItems.map(cart => cart.item.size);

    this.shoeSizeStockService.checkStock(shoeIds, sizes).subscribe({
      next: (stockMap) => {
        let newSubtotal = 0;

        this.cartItems.forEach(cart => {
          const key = `${cart.item.shoeId}_${cart.item.size}`;
          const availableStock = stockMap[key] ?? 0;

          if (availableStock === 0) {
            cart.stockAvailable = false; // ❌ no hay stock
          } else if (availableStock < cart.item.quantity) {
            //alert(`Stock insuficiente para ${cart.item.shoeName} (talla ${cart.item.size}). Disponibles: ${availableStock}`);
            cart.item.quantity = availableStock;
            cart.stockAvailable = true;
            newSubtotal += availableStock * cart.item.price;
          } else {
            cart.stockAvailable = true;
            newSubtotal += cart.item.quantity * cart.item.price;
          }
        });

        this.orderShoe!.summary = newSubtotal;
      },
      error: (err) => {
        console.error('Error al verificar stock:', err);
      }
    });
  }
  
}
