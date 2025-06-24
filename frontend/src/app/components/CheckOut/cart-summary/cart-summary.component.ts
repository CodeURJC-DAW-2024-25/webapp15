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

  displayedSubtotal = 0;
  discountPercent = 0;
  orderShoe?: OrderShoesDTO;  
  cartItems: CartItemView[] = [];
  loading = true;

  constructor(
    private orderShoesService: OrderShoesService,
    public shoeService: ShoeService,
    public loginService: LoginService,
    private shoeSizeStockService: ShoeSizeStockService, 
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  setDiscount(percent: number) {
    this.discountPercent = percent;
  }

  private calcItemsSubtotal(): number {
  return (this.cartItems || []).reduce(
    (acc, c) => acc + c.item.price * c.item.quantity, 0);
  }

  get totalAfterDiscount(): number {
    return +(this.displayedSubtotal * (1 - this.discountPercent / 100)).toFixed(2);
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
          stockAvailable: true 
        }));

        this.displayedSubtotal = this.calcItemsSubtotal();
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
      }
    })

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
    this.recalculateTotal();
  }
  couponLocked = false;
  lockAfterCoupon(): void {
    this.couponLocked = true;
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
            cart.stockAvailable = false; // no stock
          } else if (availableStock < cart.item.quantity) {
            cart.item.quantity = availableStock;
            cart.stockAvailable = true;
            newSubtotal += availableStock * cart.item.price;
          } else {
            cart.stockAvailable = true;
            newSubtotal += cart.item.quantity * cart.item.price;
          }
        });

        this.displayedSubtotal = newSubtotal;
        this.orderShoe!.summary = newSubtotal;
        const dto = this.buildDtoForUpdate();

        this.orderShoesService
          .updateOrderShoe(this.orderShoe!.id, dto)
          .subscribe({
            next: updated => {
              this.orderShoe = updated; 
            },
            error: err => console.error('Error al actualizar carrito', err)
          });
      },
      error: (err) => {
        console.error('Error al verificar stock:', err);
      }
    });
  }

  private buildDtoForUpdate(): OrderShoesDTO {
  return {
  
    id:        this.orderShoe!.id,
    userId:    this.orderShoe!.userId, 
    date:      this.orderShoe!.date,          
    country:   this.orderShoe!.country,
    firstName: this.orderShoe!.firstName,
    secondName:this.orderShoe!.secondName,
    email:     this.orderShoe!.email,
    address:   this.orderShoe!.address,
    numerPhone:this.orderShoe!.numerPhone,
    summary:   this.displayedSubtotal,
    state:     'notFinished',                 
    cuponUsed: this.orderShoe!.cuponUsed,     
    coupon:    this.orderShoe!.coupon,
    orderItems: this.cartItems.map(c => ({
      id:         c.item.id,                  
      orderId:    this.orderShoe!.id,
      shoeId:     c.item.shoeId,
      shoeName:   c.item.shoeName,
      quantity:   c.item.quantity,
      size:       c.item.size,
      price:      c.item.price
    }))
  };
}



  
}
