// order.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderShoesDTO } from '../dtos/ordershoes.dto';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'https://localhost:8443/api/v1/OrderShoes';

  constructor(private http: HttpClient) { }

  getOrdersByUserId(userId: number, page?: number, size?: number): Observable<OrderShoesDTO[]> {
    let params = new HttpParams();
    
    if (page !== undefined && size !== undefined) {
      params = params.set('page', page.toString())
                    .set('size', size.toString());
    }
    
    return this.http.get<OrderShoesDTO[]>(`${this.apiUrl}/User/${userId}`, { params });
  }
}