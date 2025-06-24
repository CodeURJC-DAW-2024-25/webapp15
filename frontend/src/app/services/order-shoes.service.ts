import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderShoesDTO } from '../dtos/ordershoes.dto';

@Injectable({
  providedIn: 'root'
})
export class OrderShoesService {
  private readonly API_URL = '/api/v1/OrderShoes';

  constructor(private http: HttpClient) {}

  getCartByUserId(userId: number): Observable<OrderShoesDTO> {
    return this.http.get<OrderShoesDTO>(`${this.API_URL}/User/${userId}/Cart`, {
      withCredentials: true
    });
  }

  applyCoupon(userId: number, couponCode: string): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/User/${userId}/Coupon`, {
      params: { couponCode },
      withCredentials: true
    });
  }

  updateOrderShoe(orderShoeId: number, updatedOrder: OrderShoesDTO): Observable<OrderShoesDTO> {
  return this.http.put<OrderShoesDTO>(`/api/v1/OrderShoes/${orderShoeId}`, updatedOrder, {
    withCredentials: true
    });
  }
 
}
