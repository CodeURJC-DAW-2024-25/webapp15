// singleproduct.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ShoeDTO } from '../dtos/shoe.dto';

@Injectable({
  providedIn: 'root'
})
export class SingleProductService {
  private apiUrl = '/api/v1/Shoes';

  constructor(private http: HttpClient) { }

  getProductById(productId: number): Observable<ShoeDTO> {
    return this.http.get<ShoeDTO>(`${this.apiUrl}/${productId}`);
  }
}