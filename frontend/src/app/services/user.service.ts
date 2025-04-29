import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, of, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = '/api/v1';

  constructor(private http: HttpClient) { }

  /**
   * Get monthly spending chart data for a specific user
   * @param userId The ID of the user to get spending data for
   * @returns Observable with labels and data for the chart
   */
  getUserMonthlySpendingChart(userId: number): Observable<any> {
    // Add auth token if available
    const token = localStorage.getItem('accessToken');
    const headers = token ? 
      new HttpHeaders().set('Authorization', `Bearer ${token}`) : 
      undefined;
    
    return this.http.get<any>(
      `${this.API_URL}/user/chartuser/${userId}`,
      { headers, withCredentials: true }
    ).pipe(
      tap(response => console.log('Raw API response:', response)),
      catchError(error => {
        console.error('Error fetching user spending chart data:', error);
        return of({ labels: [], data: [] });
      })
    );
  }

  
}