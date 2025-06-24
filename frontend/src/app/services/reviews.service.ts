// src/app/services/reviews.service.ts
import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ReviewDTO } from '../dtos/review.dto';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private baseUrl = '/api/v1/reviews';

  constructor(private http: HttpClient) { }

  getReviewsByShoeId(shoeId: number, page: number, size: number = 2): Observable<ReviewDTO[]> {
    console.log(`Getting reviews for shoe ID: ` + this.http.get<ReviewDTO[]>(`${this.baseUrl}/Shoe/${shoeId}`));
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ReviewDTO[]>(`${this.baseUrl}/pages/${shoeId}`, { params });
  }
  submitReview(review: ReviewDTO) {
    return this.http.post<ReviewDTO>(`${this.baseUrl}`, review);


  }

  deleteReview(reviewId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${reviewId}`, { responseType: 'text' });
  }


}
