import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, map, of, switchMap, tap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { UserDTO } from '../dtos/user.dto';

interface AuthResponse {
  status: 'SUCCESS' | 'FAILURE';
  message?: string;
  accessToken?: string;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
  private readonly API_URL: string;
  public logged: boolean = false;
  public user: UserDTO | null = null;

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    const isServer = typeof window === 'undefined';
    this.API_URL = isServer
      ? process.env['API_URL'] ?? 'http://localhost:4200/api/v1'
      : '/api/v1';
  }

  logIn(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.API_URL}/auth/login`,
      { username, password },
      { withCredentials: true }
    ).pipe(
      switchMap((loginResponse: AuthResponse) => {
        if (loginResponse.status === 'SUCCESS') {
          return this.getCurrentUser().pipe(
            map((user: UserDTO | null): AuthResponse => {
              if (user) {
                this.logged = true;
                this.user = user;
                if (isPlatformBrowser(this.platformId)) {
                  localStorage.setItem('accessToken', loginResponse.accessToken ?? '');
                }
                return {
                  status: 'SUCCESS',
                  message: 'Autenticación completada'
                };
              }
              return {
                status: 'FAILURE',
                message: 'Usuario no encontrado'
              };
            })
          );
        }
        return of(loginResponse);
      }),
      catchError((err): Observable<AuthResponse> => {
        return of({
          status: 'FAILURE',
          message: 'Error en la conexión con el servidor desde el servicio login'
        });
      })
    );
  }

  getCurrentUser(): Observable<UserDTO | null> {
    return this.http.get<UserDTO>(`${this.API_URL}/auth/me`, {
      withCredentials: true
    }).pipe(
      tap(user => {
        this.user = user;
        this.logged = true;
      }),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          return this.refreshToken().pipe(
            switchMap(() => this.getCurrentUser()),
            catchError((refreshError) => {
              console.error('Error refreshing token:', refreshError);
              this.handleAuthError();
              return of(null);
            })
          );
        }
        console.error('Error getting current user:', error);
        this.handleAuthError();
        return of(null);
      })
    );
  }

  private handleAuthError(): void {
    this.logged = false;
    this.user = null;
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('accessToken');
    }
    this.router.navigate(['/']);
  }

  refreshToken(): Observable<any> {
    return this.http.post(`${this.API_URL}/auth/refresh`, {}, {
      withCredentials: true
    }).pipe(
      catchError(error => {
        console.error('Refresh token failed:', error);
        this.handleAuthError();
        return throwError(() => error);
      })
    );
  }

  checkSession(): Observable<boolean> {
    return this.http.get<boolean>(
      `${this.API_URL}/auth/check`,
      { withCredentials: true }
    ).pipe(
      map(() => true),
      catchError(err => {
        if (err.status === 0) {
          console.error('Error de conexión - verifica el proxy y la conexión al backend');
        } else {
          console.error('Session check failed:', err);
        }
        return of(false);
      })
    );
  }

  reqIsLogged(): void {
    this.checkSession().subscribe((isLogged) => {
      this.logged = isLogged;
      if (isLogged) {
        this.getCurrentUser().subscribe((user) => {
          this.user = user;
        });
      } else {
        this.user = null;
      }
    });
  }

  logOut(): Observable<void> {
    return this.http.post<void>(
      `${this.API_URL}/auth/logout`, 
      {}, 
      { withCredentials: true }
    ).pipe(
      tap(() => {
        this.logged = false;
        this.user = null;
        
        if (isPlatformBrowser(this.platformId)) {
          localStorage.removeItem('accessToken');
        }
        
        this.router.navigate(['/']);
        if (isPlatformBrowser(this.platformId)) {
          window.location.reload();
        }
      }),
      catchError(error => {
        console.error('Error durante logout:', error);
        this.logged = false;
        this.user = null;
        this.router.navigate(['/']);
        return throwError(() => error);
      })
    );
  }
}
