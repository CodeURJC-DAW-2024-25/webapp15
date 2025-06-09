import { Component,OnInit, ViewChild } from '@angular/core';
import { ShippingFormData } from './shipping-form/shipping-form.component'; // Definiremos esta interfaz
import {LoginService} from '../../services/login.service';
import { CartSummaryComponent } from './cart-summary/cart-summary.component';
import { OrderShoesDTO } from '../../dtos/ordershoes.dto';
import { OrderShoesService } from '../../services/order-shoes.service';
import { CouponDTO } from '../../dtos/coupon.dto';
import { OrderService } from '../../services/order.service';
import saveAs from 'file-saver';
@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrl: '../../../assets/css/style.css'
})
export class CheckoutComponent implements OnInit {

  constructor(private loginService:LoginService,
    private orderShoesService: OrderShoesService,
    private orderService: OrderService
  ) {}

  @ViewChild('cartSummary') cartSummary!: CartSummaryComponent; 
  private appliedCoupon: {dto: CouponDTO, percent: number} | null = null;

  ngOnInit(): void {
    this.loginService.reqIsLogged();
    setTimeout(() => {
      if (!this.loginService.user) {
        alert('Error: no se pudo obtener el usuario. Por favor, vuelve a iniciar sesión.');
        console.error('Usuario nulo después de intentar login.');
      }
    }, 500);
  }
  


private getSubtotalWithCoupon(): number {
  if (!this.cartSummary) { return 0; }

  const base = this.cartSummary.displayedSubtotal;   // 120

  if (!this.appliedCoupon) { return base; }

  return +(base * (1 - this.appliedCoupon.percent / 100)).toFixed(2);                              // 102.00
}

private downloadTicket(orderId: number): void {
  this.orderService.downloadTicket(orderId).subscribe({
    next: data => {
      const blob = new Blob([data], { type: 'application/pdf' });
      saveAs(blob, `ticket_${orderId}.pdf`);
    },
    error: err => console.error('Error downloading ticket', err)
  });
}


/* CheckoutComponent.ts */
 onCouponApplied(ev: { couponDto: CouponDTO; discountPercent: number } | null): void {
    if (!ev) {                 // cupón NO válido
      this.appliedCoupon = null;
      this.cartSummary.setDiscount(0); // Reiniciar descuento
      return;
    }

    /* guardamos dto + porcentaje */
    this.appliedCoupon = {
      dto    : ev.couponDto,
      percent: ev.discountPercent
    };

     // Solo guardamos la info del cupón (no aplicamos descuento directo aquí)
    if (this.cartSummary?.orderShoe) {
      this.cartSummary.orderShoe.cuponUsed = ev.couponDto.code;
      this.cartSummary.orderShoe.coupon = ev.couponDto;
    }

    this.cartSummary.setDiscount(ev.discountPercent); // Actualizar descuento en resumen
    // Lock para evitar que se cambie después
    this.cartSummary.lockAfterCoupon();
  }


/* ===============  CheckoutComponent.ts  =============== */
onSubmit(form: ShippingFormData): void {
  const cart = this.cartSummary?.orderShoe;
  if (!cart) { return; }

  const today = new Date().toISOString().slice(0, 10);
  const subtotal = this.cartSummary.displayedSubtotal;
  const discount = this.appliedCoupon?.percent ?? 0;
  const discountedTotal = +(subtotal * (1 - discount / 100)).toFixed(2);

  const dto: OrderShoesDTO = {
    ...cart,  // usa datos actuales de cart: items, userId...

    date    : today,
    state   : 'Processed',
    summary : this.getSubtotalWithCoupon(), // subtotal con cupón aplicado

    cuponUsed: this.appliedCoupon?.dto.code ?? '',
    coupon   : this.appliedCoupon?.dto ?? null,

    // Formulario de usuario
    country   : form.country,
    firstName : form.firstName,
    secondName: form.lastName,
    email     : form.email,
    address   : form.address,
    numerPhone: form.phone
  };

  this.orderShoesService.updateOrderShoe(dto.id, dto).subscribe({
    next : ()  => this.downloadTicket(dto.id),
    error: err => console.error('Error updating order', err)
  });

}




}
