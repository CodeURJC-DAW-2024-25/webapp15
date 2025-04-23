// order-count-chart.component.ts
import { Component, HostListener, OnInit } from '@angular/core';
import { AdminDataService } from '../../services/admin.service';
import { NgxChartsModule } from '@swimlane/ngx-charts';


@Component({
  selector: 'app-order-count-chart',
  templateUrl: './order-count-chart.component.html',
  styleUrl: '../../../assets/css/admin.css', 
})
export class OrderCountChartComponent implements OnInit {
  // Chart data
  orderChartData: any[] = [];
  
  // Chart options
  autoScale = true;
  view: [number, number] = [1200, 320];
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = true;
  xAxisLabel = 'Month';
  showYAxisLabel = true;
  yAxisLabel = 'Orders';
  timeline = false;
  colorScheme: string = 'vivid'; // or other predefined scheme like 'natural', 'cool', etc.
  
  loading = true;
  error = false;

  constructor(private adminDataService: AdminDataService) { }

  ngOnInit(): void {
    this.loadChartData();
    this.updateChartSize();
  }
 



  loadChartData(): void {
    this.adminDataService.getOrderCountChartData().subscribe({
      next: (response) => {
        // Transform the data for ngx-charts format
        this.orderChartData = this.transformChartData(response);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading order count chart data', err);
        this.error = true;
        this.loading = false;
      }
    });
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

  // Transform data from API format to ngx-charts format
  private transformChartData(response: any): any[] {
    // Create a series array with a single series for the orders
    return [
      {
        name: 'Orders',
        series: response.labels.map((month: string, index: number) => {
          return {
            name: month,
            value: response.data[index]
          };
        })
      }
    ];
  }
  yAxisTickFormatting = (value: number) => {
    return value % 1 === 0 ? value.toString() : '';
  };

  onSelect(event: any): void {
    console.log('Item clicked', event);
  }
}