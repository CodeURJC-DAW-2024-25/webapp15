/* ------------  IMPORTS  ------------ */
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService }      from '../../../services/login.service';
import { CouponService }     from '../../../services/coupon.service';
import { OrderShoesDTO }     from '../../../dtos/ordershoes.dto';
import { CouponDTO }         from '../../../dtos/coupon.dto';

/* ------------  DATOS DEL FORM  ------------ */
export interface ShippingFormData {
  country   : string;
  coupon    : string;
  firstName : string;
  lastName  : string;
  email     : string;
  address   : string;
  phone     : string;
}

@Component({
  selector   : 'app-shipping-form',
  templateUrl: './shipping-form.component.html',
  styleUrl   : '../../../../assets/css/style.css'
})
export class ShippingFormComponent {

  /* ──────────────────── inputs/outputs ─────────────────── */
  @Input()  orderShoe?: OrderShoesDTO;
  @Output() couponApplied = new EventEmitter<{ couponDto: CouponDTO; discountPercent: number } | null>();
  @Output() formSubmit    = new EventEmitter<ShippingFormData & { discountPercent: number }>();

  /* ───────────────────── form & helpers ────────────────── */
  shippingForm : FormGroup;
  couponValid  : boolean | null = null;
  discountPercent = 0;                 // % real (0-100)

  constructor(
    private fb           : FormBuilder,
    private loginService : LoginService,
    private couponService: CouponService
  ) {
    this.shippingForm = this.fb.group({
      country  : ['', Validators.required],
      coupon   : [''],
      firstName: ['', Validators.required],
      lastName : ['', Validators.required],
      email    : ['', [Validators.required, Validators.email]],
      address  : ['', Validators.required],
      phone    : ['', Validators.required]
    });
  }

  /* ---------- click “Apply coupon” ---------- */
  applyCoupon(): void {
    const code = this.shippingForm.get('coupon')?.value?.trim();
    if (!code) { this.couponValid = null; return; }

    const userId = this.loginService.user?.id;
    if (!userId) { alert('Not logged in'); return; }

    this.couponService.validateCoupon(userId, code).subscribe({
      next: (dto) => {
        /* dto.discount = 0.85  ➟  15 % */
        const percent = dto.discount <= 1
          ? (1 - dto.discount) * 100
          : dto.discount;

        this.couponValid     = true;
        this.discountPercent = percent;

        this.couponApplied.emit({
          couponDto      : dto,
          discountPercent: percent
        });
      },
      error: () => {
        this.couponValid     = false;
        this.discountPercent = 0;
        this.couponApplied.emit(null);
      }
    });
  }

  /* ---------- click “Continue with Order” ---------- */
  submitForm(): void {
    if (this.shippingForm.invalid) { return; }

    this.formSubmit.emit({
      ...this.shippingForm.value,
      discountPercent: this.discountPercent
    });
  }
}
