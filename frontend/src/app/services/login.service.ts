import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, map, of, switchMap, tap, throwError } from 'rxjs';
import { Router } from '@angular/router'; // Importar Router
import { UserDTO } from '../dtos/user.dto';

interface AuthResponse {
  status: 'SUCCESS' | 'FAILURE';
  message?: string;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
  private readonly API_URL = '/api/v1';
  
  constructor(
    private http: HttpClient,
    private router: Router // Inyectar Router
  ) {}

  
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
                alert(' Login exitoso en loginservice con el ID del usuario: ' + user.id);
                return { 
                  status: 'SUCCESS' as const,
                  message: 'Autenticación completada'
                };
              } else {
                alert(' Usuario no encontrado en service login');
                return { 
                  status: 'FAILURE' as const, 
                  message: 'Usuario no encontrado' 
                };
              }
            })
          );
        } else {
          alert(' Error al iniciar sesión desde login service');
          return of({ 
            status: 'FAILURE' as const, 
            message: loginResponse.message || 'Error al iniciar sesión desde el service login' 
          });
        }
      }),
      catchError((err): Observable<AuthResponse> => {
        alert('[TRACE] Error al iniciar sesión desde el service login: ' + err.message);
        return of({ 
          status: 'FAILURE' as const, 
          message: 'Error en la conexión con el servidor desde el servicio login' 
        });
      })
    );
  }
  
  
  

  
  getCurrentUser(): Observable<UserDTO | null> {
    let userData: UserDTO | null = null;
  
    return this.http.get<UserDTO>(`${this.API_URL}/auth/me`, {
      withCredentials: true
    }).pipe(
      tap((user) => {
        userData = user;
      }),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          return this.refreshToken().pipe(
            switchMap(() => this.getCurrentUser()),
            catchError(() => {
              this.router.navigate(['/login']);
              alert('Error al autenticar el login desde el login service (getcurrentUser)');
              return of(null);
            })
          );
        }
        return of(null);
      })
    );
  }

  // Método para refrescar el token
  refreshToken(): Observable<any> {
    return this.http.post(`${this.API_URL}/auth/refresh`, {}, {
      withCredentials: true
    });
  }

  // Verificar si la sesión es válida
  checkSession(): Observable<boolean> {
    return this.http.get<boolean>(
      `${this.API_URL}/auth/check`, 
      { withCredentials: true }
    ).pipe(
      catchError(err => {
        console.error('Error comprobando sesión:', err);
        return of(false);
      })
    );
  }
}