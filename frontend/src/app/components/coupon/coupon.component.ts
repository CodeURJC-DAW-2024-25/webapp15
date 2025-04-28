import { Component, OnInit } from '@angular/core';
import { CouponService } from '../../services/coupon.service';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-coupon',
  templateUrl: './coupon.component.html',
  styleUrl: '../../../assets/css/style.css'
})
export class CouponComponent implements OnInit {
  loading = false;
  message = '';
  showMessage = false;
  messageType = '';

  constructor(
    private couponService: CouponService,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    // Ensure user data is refreshed when component initializes
    this.loginService.reqIsLogged();
  }

  /**
   * Sends a coupon to the current logged-in user
   */
  sendCoupon(): void {
    this.loading = true;
    this.showMessage = false;
    
    // Check if user is logged in and has an ID
    if (!this.loginService.logged || !this.loginService.user || this.loginService.user.id === undefined) {
      this.loading = false;
      this.message = 'You need to be logged in to get a coupon';
      this.messageType = 'warning';
      this.showMessage = true;
      return;
    }
    
    // Now we're sure the user ID exists
    const userId = this.loginService.user.id;
    
    // Send coupon to the current user
    this.couponService.sendCoupon(userId).subscribe({
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
