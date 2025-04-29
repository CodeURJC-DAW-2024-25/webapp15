import { Component, HostListener, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { LoginService } from '../../services/login.service';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-user-spending-chart',
  templateUrl: './profile-chart.component.html',
})
export class UserSpendingChartComponent implements OnInit {
  spendingChartData: any[] = [];

  view: [number, number] = [1200, 300];
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = true;
  xAxisLabel = 'Month';
  showYAxisLabel = true;
  yAxisLabel = 'Spending ($)';
  colorScheme: string = 'cool';

  loading = true;
  error = false;

  constructor(
    private userService: UserService,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.loginService.checkSession().pipe(take(1)).subscribe(isLoggedIn => {
      if (isLoggedIn) {
        if (!this.loginService.user) {
          this.loginService.getCurrentUser().pipe(take(1)).subscribe(user => {
            this.loginService.user = user;
            this.loadChartData();
          });
        } else {
          this.loadChartData();
        }
      } else {
        console.error('User not logged in');
        this.error = true;
        this.loading = false;
      }
    });

    this.updateChartSize();
  }

  loadChartData(): void {
    if (!this.loginService.logged || !this.loginService.user) {
      console.error('User not logged in');
      this.error = true;
      this.loading = false;
      return;
    }

    const userId = this.loginService.user.id;
    if (userId == null) {
      console.error('No user ID available – cannot load chart');
      this.error = true;
      this.loading = false;
      return;
    }

    this.userService.getUserMonthlySpendingChart(userId)
      .subscribe({
        next: (response) => {
          console.log('Chart data response:', response);
          if (response?.labels?.length && response?.data?.length) {
            this.spendingChartData = this.transformChartData(response);
            this.loading = false;
          } else {
            console.warn('Received empty or invalid chart data format');
            this.error = true;
            this.loading = false;
          }
        },
        error: (err) => {
          console.error('Error loading user spending chart data', err);
          this.error = true;
          this.loading = false;
        }
      });
  }

  private transformChartData(response: any): any[] {
    const length = Math.min(response.labels.length, response.data.length);
    return Array.from({ length }, (_, i) => ({
      name: response.labels[i],
      value: response.data[i]
    }));
  }

  onSelect(event: any): void {
    console.log('Item clicked', event);
  }

  resizeChart(width: any): void {
    const chartWidth = Math.max(width, 300);
    this.view = [chartWidth, 320];
  }

  @HostListener('window:resize')
  onResize() {
    this.updateChartSize();
  }

  updateChartSize() {
    const width = Math.max(window.innerWidth * 0.9, 300);
    const height = Math.max(window.innerHeight * 0.4, 250);
    this.view = [width, height];
  }

  yAxisTickFormatting(val: number): string {
    return '$' + val.toLocaleString();
  }
}
