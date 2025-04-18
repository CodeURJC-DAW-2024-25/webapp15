import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { AdminDataService } from '../../services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['../../../assets/css/admin.css', "../../../../node_modules/@fortawesome/fontawesome-free/css/all.min.css"
],
  encapsulation: ViewEncapsulation.ShadowDom
})
export class AdminDashboardComponent implements OnInit {
  dashboardStats: any = {
    userCount: 0,
    shoeCount: 0,
    processedOrderCount: 0,
    totalMoneyGained: 0
  };
  loading = true;
  error = false;

  constructor(private adminDataService: AdminDataService) {}

  ngOnInit(): void {
    this.loadDashboardStats();
  }

  loadDashboardStats(): void {
    this.adminDataService.getDashboardStats().subscribe({
      next: (data) => {
        this.dashboardStats = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading dashboard stats', err);
        this.error = true;
        this.loading = false;
      }
    });
  }
}
