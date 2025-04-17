import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of } from 'rxjs';


interface AuthResponse {
  status: 'SUCCESS' | 'FAILURE';
  message?: string;
  token?: string;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
  private readonly API_URL = "/api/v1";

  constructor(private http: HttpClient) {}

  logIn(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.API_URL}/auth/login`,
      { username, password },
      { 
        withCredentials: true,
        headers: { 'Content-Type': 'application/json' }
      }
    );
  }
}