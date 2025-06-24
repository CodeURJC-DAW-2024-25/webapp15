import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RecommendedProductsService {
  private apiUrl = '/api/v1/index/recommended-products'; 
  private baseUrl = '/api/v1/Shoes';

  constructor(private http: HttpClient) { }

  getRecommendedProducts(limit: number = 10): Observable<any> {
    console.log('Calling getRecommendedProducts...');
    console.log('estos on los recomendados' + this.http.get(this.apiUrl));
    return this.http.get(`${this.apiUrl}?limit=${limit}`);
  }
  getImageUrl(shoeId:number, imageNumber:number):string{
    return `${this.baseUrl}/${shoeId}/image/${imageNumber}`; // Constructing the image URL for a specific shoe and image number
}
}