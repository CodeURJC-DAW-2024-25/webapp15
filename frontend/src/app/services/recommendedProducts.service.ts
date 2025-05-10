import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RecommendedProductsService {
  private apiUrl = '/api/v1/index/recommended-products'; // Ajusta según tu endpoint

  constructor(private http: HttpClient) { }

  getRecommendedProducts(): Observable<any> {
    console.log('Calling getRecommendedProducts...');
    console.log('estos on los recomendados' + this.http.get(this.apiUrl));
    return this.http.get(this.apiUrl);
  }
  getImageUrl(shoeId:number, imageNumber:number):string{
    return `${this.apiUrl}/${shoeId}/image/${imageNumber}`; // Constructing the image URL for a specific shoe and image number
}
}