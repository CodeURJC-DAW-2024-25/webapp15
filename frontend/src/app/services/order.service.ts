import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { OrderShoesDTO } from '../dtos/ordershoes.dto';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly API_URL = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getOrdersByUserId(userId: number, page: number = 0, size: number = 5): Observable<OrderShoesDTO[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      
    return this.http.get<OrderShoesDTO[]>(
      `${this.API_URL}/OrderShoes/User/${userId}`,
      { 
        params: params,
        withCredentials: true
      }
    ).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Error fetching orders:', error);
        return throwError(() => this.createErrorMessage(error));
      })
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
    return this.http.get<OrderShoesDTO>(`${this.API_URL}/OrderShoes/${orderId}`);
  }

  downloadTicket(orderId: number): Observable<Blob> {
    return this.http.get(`${this.API_URL}/OrderShoes/dld/${orderId}`, {
      responseType: 'blob'
    });
  }
}