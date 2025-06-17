// src/app/services/reviews.service.ts
import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ReviewDTO } from '../dtos/review.dto';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private baseUrl = '/api/v1/reviews'; // ajusta si tu puerto o ruta base es distinta

  constructor(private http: HttpClient) { }

  getReviewsByShoeId(shoeId: number, page: number, size: number = 2): Observable<ReviewDTO[]> {
    console.log(`Getting reviews for shoe ID: ` + this.http.get<ReviewDTO[]>(`${this.baseUrl}/Shoe/${shoeId}`));
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ReviewDTO[]>(`${this.baseUrl}/paginated/${shoeId}`, { params });
  }
  submitReview(review: ReviewDTO) {
    return this.http.post<ReviewDTO>(`${this.baseUrl}`, review);


  }
  // Añade este método en ReviewService
  deleteReview(reviewId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${reviewId}`, { responseType: 'text' });
  }


  // Si en el futuro quieres añadir más funciones:
  // createReview(dto: ReviewDTO): Observable<ReviewDTO> { ... }
  // deleteReview(id: number): Observable<ReviewDTO> { ... }
  // updateReview(id: number, dto: ReviewDTO): Observable<ReviewDTO> { ... }
}
