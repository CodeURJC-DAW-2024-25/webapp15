import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of } from 'rxjs';
import {UserDTO} from "../dtos/user.dto";


interface AuthResponse {
  status: 'SUCCESS' | 'FAILURE';
  message?: string;
  token?: string;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
  private readonly API_URL = "https://localhost:8443/api/v1";
  public user: UserDTO | null = null;
  public logged: boolean = false;
  public isAdmin: boolean = false;

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

  public reqIsLogged() {
    this.http.get<UserDTO>(`${this.API_URL}/user/me`, { withCredentials: true })
      .subscribe({
        next: (response) => {
          this.user = response;
          this.logged = true;
          this.isAdmin = response.roles.includes("ADMIN");
        },
        error: (error) => {
          this.user = null;
          this.logged = false;
          this.isAdmin = false;

          if (error.status !== 404) {
            console.error("Error when asking if logged: " + JSON.stringify(error));
          }
        }
      });
  }
}