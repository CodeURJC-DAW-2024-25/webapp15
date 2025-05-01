import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
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

  // Obtener datos del usuario
  getUser(userId: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.API_URL}/${userId}`, {
      withCredentials: true
    });
  }

  // Obtener imagen como Blob
  getUserImage(userId: number): Observable<Blob> {
    console.log('Fetching user image for ID:', userId);
    return this.http.get(`${this.API_URL}/${userId}/image`, {
      responseType: 'blob',
      withCredentials: true,
      observe: 'response' // Añade esto para obtener la respuesta completa
    }).pipe(
      tap(response => {
        console.log('Image response:', {
          status: response.status,
          headers: response.headers,
          bodySize: response.body?.size
        });
      }),
      map(response => response.body as Blob),
      catchError(error => {
        console.error('Error fetching user image:', error);
        return throwError(() => error);
      })
    );
  }

  // Actualizar datos del usuario
  updateUser(userId: number, userData: UserDTO): Observable<UserDTO> {
    return this.http.put<UserDTO>(`${this.API_URL}/${userId}`, userData, {
      withCredentials: true
    });
  }

  // Subir nueva imagen
  uploadUserImage(userId: number, imageFile: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', imageFile);

    return this.http.put(`${this.API_URL}/${userId}/image`, formData, {
      withCredentials: true,
      reportProgress: true,
      observe: 'events'
    });
  }

  // Convertir Blob a SafeUrl
  blobToSafeUrl(blob: Blob): SafeUrl {
    const url = URL.createObjectURL(blob);
    return this.sanitizer.bypassSecurityTrustUrl(url);
  }

  // Convertir Base64 a SafeUrl
  base64ToSafeUrl(base64: string, mimeType: string = 'image/jpeg'): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(
      `data:${mimeType};base64,${base64}`
    );
  }
}