import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, of, tap, throwError } from 'rxjs';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { UserDTO } from '../dtos/user.dto';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = "/api/v1/user";

  constructor(
    private http: HttpClient,
    private sanitizer: DomSanitizer
  ) { }

  
  getUser(userId: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.API_URL}/${userId}`, {
      withCredentials: true
    });
  }

 
  getUserImage(userId: number): Observable<Blob> {
    return this.http.get(`${this.API_URL}/${userId}/image`, {
      responseType: 'blob',
      withCredentials: true,
      observe: 'response' // Añade esto para obtener la respuesta completa
    }).pipe(
      tap(response => {
        console.log();
      }),
      map(response => response.body as Blob),
      catchError(error => {
        console.error('Error fetching user image:', error);
        return throwError(() => error);
      })
    );
  }

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
      `${this.API_URL}/chartuser/${userId}`,
      { headers, withCredentials: true }
    ).pipe(
      tap(response => console.log('Raw API response:', response)),
      catchError(error => {
        console.error('Error fetching user spending chart data:', error);
        return of({ labels: [], data: [] });
      })
    );
  }

  
  updateUser(userId: number, userData: UserDTO): Observable<UserDTO> {
    return this.http.put<UserDTO>(`${this.API_URL}/${userId}`, userData, {
      withCredentials: true
    });
  }


  uploadUserImage(userId: number, imageFile: File): Observable<any> {
    const formData = new FormData();
    formData.append('imageFile', imageFile);

    return this.http.put(`${this.API_URL}/${userId}/image`, formData, {
      withCredentials: true,
      reportProgress: true,
      observe: 'events'
    });
  }


  blobToSafeUrl(blob: Blob): SafeUrl {
    const url = URL.createObjectURL(blob);
    return this.sanitizer.bypassSecurityTrustUrl(url);
  }

  
  base64ToSafeUrl(base64: string, mimeType: string = 'image/jpeg'): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(
      `data:${mimeType};base64,${base64}`
    );
  }

  getImageUrl(userId: number):string{
    return `${this.API_URL}/${userId}/image`;
  }
  
}