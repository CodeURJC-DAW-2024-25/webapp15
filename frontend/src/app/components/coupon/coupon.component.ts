import { Component } from '@angular/core';
import { CouponService } from '../../services/coupon.service';

@Component({
  selector: 'app-coupon',
  templateUrl: './coupon.component.html',
  styleUrl: '../../../assets/css/style.css'
})
export class CouponComponent {
  loading = false;
  message = '';
  showMessage = false;
  messageType = '';

  constructor(private couponService: CouponService) {}

  /**
   * Sends a coupon to user with ID 1
   */
  sendCoupon(): void {
    this.loading = true;
    this.showMessage = false;
    
    // Sending coupon to user #1
    this.couponService.sendCoupon(1).subscribe({
      next: (response) => {
        this.loading = false;
        this.message = response.message || 'Coupon sent successfully!';
        this.messageType = 'success';
        this.showMessage = true;
      },
      error: (error) => {
        this.loading = false;
        this.message = error.error?.message || 'Failed to send coupon. Please try again.';
        this.messageType = 'danger';
        this.showMessage = true;
      }
    });
  }
}