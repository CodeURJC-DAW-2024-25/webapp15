import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
    return this.http.get<any>(`${this.API_URL}/coupon/send`, {
      params: { userId: userId.toString() }
    });
  }
}