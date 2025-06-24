import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CouponDTO } from '../dtos/coupon.dto';

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  private readonly API_URL = "/api/v1";

  constructor(private http: HttpClient) {}

  /**
   * Sends a coupon to a specific user by ID
   * @param userId The ID of the user to send the coupon to
   * @returns Observable with the API response
   */
  sendCoupon(userId: number): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/coupon/email`, {
      params: { userId: userId.toString() }
    });
  }

  validateCoupon(userId: number, code: string) {
    return this.http.get<CouponDTO>(
      `/api/v1/coupon/validation`,
      { params: { userId, code } }
    );
  }


}