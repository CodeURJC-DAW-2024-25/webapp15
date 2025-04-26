// register.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { UserDTO } from '../dtos/user.dto';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {
  private readonly API_URL = '/api/v1';

  constructor(private http: HttpClient) {}

  createUser(userData: UserDTO): Observable<UserDTO> {
    // Add proper headers
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    // Log the data being sent for debugging
    console.log('Sending registration data:', userData);

    return this.http.post<UserDTO>(
      `${this.API_URL}/user`,
      userData,
      { headers} // Add withCredentials if your API uses cookies
    ).pipe(
      catchError(this.handleError)
    );
  }

  // Improved error handling
  private handleError(error: HttpErrorResponse) {
    console.error('Full error object:', error);
    
    let errorMessage = 'Registration failed. Please try again.';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
      console.error('Client-side error:', errorMessage);
    } else {
      // Server-side error
      console.error(`Backend returned code ${error.status}, body:`, error.error);
      
      if (error.status === 0) {
        errorMessage = 'Cannot connect to server. Please check your internet connection.';
      } else if (error.status === 400) {
        errorMessage = error.error?.message || 'Invalid registration data.';
      } else if (error.status === 409) {
        errorMessage = 'Username or email already exists.';
      } else if (error.error && typeof error.error === 'object') {
        // Try to extract message from different error formats
        if (error.error.message) {
          errorMessage = error.error.message;
        } else if (error.error.error) {
          errorMessage = error.error.error;
        }
      }
    }
    
    return throwError(() => new Error(errorMessage));
  }
}