// shoe-size-stock.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ShoeSizeStockService {
  private readonly API_URL = '/api/v1/ShoeSizeStocks';

  constructor(private http: HttpClient) {}

  checkStock(shoeIds: number[], sizes: string[]): Observable<{[key: string]: number}> {
  const body = { shoeIds, sizes };               // 👈 estructura exacta
  return this.http.post<{[key: string]: number}>(
    '/api/v1/ShoeSizeStocks/CheckStock',
    body
  );
}



}
