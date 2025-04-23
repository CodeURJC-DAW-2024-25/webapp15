// money-gained-chart.component.ts
import { Component, HostListener, OnInit } from '@angular/core';
import { AdminDataService } from '../../services/admin.service';

@Component({
  selector: 'app-money-gained-chart',
  templateUrl: './money-gained-chart.component.html',
  styleUrl: '../../../assets/css/admin.css', 

})
export class MoneyGainedChartComponent implements OnInit {
  // Chart data
  moneyChartData: any[] = [];
  
  // Chart options
  view: [number, number] = [1200, 300];
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = true;
  xAxisLabel = 'Month';
  showYAxisLabel = true;
  yAxisLabel = 'Revenue ($)';
  timeline = false;
  colorScheme: string = 'cool'; // or other predefined scheme like 'natural', 'cool', etc.


  
  loading = true;
  error = false;

  constructor(private adminDataService: AdminDataService) { }

  ngOnInit(): void {
    this.loadChartData();
    this.updateChartSize();
  }

  loadChartData(): void {
    this.adminDataService.getMoneyGainedChartData().subscribe({
      next: (response) => {
        // Transform the data for ngx-charts format
        this.moneyChartData = this.transformChartData(response);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading money gained chart data', err);
        this.error = true;
        this.loading = false;
      }
    });
  }

  // Transform data from API format to ngx-charts format
  private transformChartData(response: any): any[] {
    return response.labels.map((month: string, index: number) => {
      return {
        name: month,
        value: response.data[index]
      };
    });
  }

  onSelect(event: any): void {
    console.log('Item clicked', event);
  }
  
   resizeChart(width: any): void {
    this.view = [width, 320]
  }

  @HostListener('window:resize')
  onResize() {
    this.updateChartSize();
  }

  updateChartSize() {
    const width = window.innerWidth * 0.9;
    const height = window.innerHeight * 0.6;
    this.view = [width, height];
  }

  // Format the y-axis tick values as dollars
  yAxisTickFormatting(val: number): string {
    return '$' + val.toLocaleString();
  }
}