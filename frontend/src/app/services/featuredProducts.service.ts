import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FeaturedProductsService {
  private apiUrl = '/api/v1/index/bestSelling'; 
  private baseUrl = '/api/v1/Shoes';

  constructor(private http: HttpClient) { }

  getBestSellingProducts(limit: number = 10): Observable<any> {
    return this.http.get(`${this.apiUrl}?limit=${limit}`);
  }
  getImageUrl(shoeId:number, imageNumber:number):string{
    return `${this.baseUrl}/${shoeId}/image/${imageNumber}`; // Constructing the image URL for a specific shoe and image number
}
}