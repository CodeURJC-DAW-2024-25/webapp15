import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { OrderShoesDTO } from '../dtos/ordershoes.dto';
import { environment } from '../../environments/environment';
import { LoginService } from '../services/login.service';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = `${environment.apiUrl}/OrderShoes`;
  private readonly API_URL = "/api/v1";
 
  constructor(private http: HttpClient, private loginService: LoginService) {
  }

  getOrdersByUserId(userId: number, page: number, size: number): Observable<OrderShoesDTO[]> {
    return this.http.get<OrderShoesDTO[]>(
      `${this.API_URL}/OrderShoes/User/${userId}?page=${page}&size=${size}`,
      { withCredentials: true } // Esto envía las cookies de sesión
    );
  }

  private createErrorMessage(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return `Error de conexión: 
        - Verifica que el backend esté corriendo en https://localhost:8443
        - Revisa la configuración del proxy
        - Prueba acceder manualmente a ${error.url}`;
    }
    return error.error?.message || error.message;
  }



  getOrderDetails(orderId: number): Observable<OrderShoesDTO> {
    return this.http.get<OrderShoesDTO>(`${this.apiUrl}/${orderId}`);
  }

  downloadTicket(orderId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download/${orderId}`, {
      responseType: 'blob'
    });
  }
}