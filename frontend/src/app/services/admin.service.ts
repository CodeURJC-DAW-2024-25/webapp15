import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminDataService {
  private apiUrl = '/api/v1/admin';

  constructor(private http: HttpClient) { }

  // Fetch dashboard statistics
  getDashboardStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/dashboard-stats`);
  }

  // Fetch order count chart data
  getOrderCountChartData(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/order-count-chart`);
  }

  // Fetch money gained chart data
  getMoneyGainedChartData(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/money-gained-chart`);
  }
}