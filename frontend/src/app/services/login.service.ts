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
  private readonly API_URL = "/api/v1";
  public user: UserDTO | null = null;
  public logged: boolean = false;

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
      .subscribe(
        (response) => {
          console.log("User logged: " + JSON.stringify(response));
          this.user = response;
          this.logged = true;
        },
        (error) => {
          this.user = null;
          this.logged = false;
          if (error.status !== 404) {
            console.error("Error when asking if logged: " + JSON.stringify(error));
          }
        }
      );
  }
}