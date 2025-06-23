import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { OrderShoesDTO } from '../../dtos/ordershoes.dto';
import { saveAs } from 'file-saver';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { LoginService } from '../../services/login.service';

interface EnhancedOrder extends OrderShoesDTO {
  showDetails: boolean;
  totalItems?: number;
  formattedDate?: string;
}

@Component({
  selector: 'app-order-history',
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css']
})
export class OrderHistoryComponent implements OnInit {
  orders: EnhancedOrder[] = [];
  isLoading = true;
  error: string | null = null;
  id: number | null = null;
  hasMoreOrders = false;
  currentPage = 0;
  pageSize = 5;
  allOrders: EnhancedOrder[] = [];
  isAdmin: boolean = false;
  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute,
    private router: Router,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.loginService.getCurrentUser().subscribe((user) => {
      if (user && user.id) {
        this.id = user.id;
        this.isAdmin = user.id === 1;
        if (!this.isAdmin) {
        this.loadOrders(this.id);
      } else {
        this.isLoading = false;
      }
      } else {
        this.handleError('No se pudo obtener el usuario.', true);
      }
    });
  }

  private handleError(message: string, redirect: boolean = false): void {
    this.error = message;
    this.isLoading = false;
    
    if (redirect) {
      this.router.navigate(['/']);
    }
  }

  private loadOrders(id: number): void {
    this.isLoading = true;
    this.error = null;
  
    this.orderService.getOrdersByUserId(id).subscribe({
      next: (allOrders) => {
        const sorted = this.enhanceOrders(allOrders);
        this.allOrders = sorted;
        this.orders = sorted.slice(0, this.pageSize);
        this.hasMoreOrders = sorted.length > this.pageSize;
        this.isLoading = false;
      },
      error: (err) => {
        this.handleError('Error al cargar pedidos: ' + err.message);
      }
    });
  }

  private enhanceOrders(orders: OrderShoesDTO[]): EnhancedOrder[] {
    return orders
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
      .map(order => ({
        ...order,
        showDetails: false,
        totalItems: this.calculateTotalItems(order),
        formattedDate: this.formatDate(order.date)
      }));
  }

  loadMoreOrders(): void {
    const nextIndex = (this.currentPage + 1) * this.pageSize;
    const nextChunk = this.allOrders.slice(nextIndex, nextIndex + this.pageSize);
  
    this.orders = [...this.orders, ...nextChunk];
    this.currentPage++;
    this.hasMoreOrders = this.allOrders.length > this.orders.length;
  }
  
  toggleOrderDetails(orderId: number): void {
    const order = this.orders.find(o => o.id === orderId);
    if (!order) return;

    if (!order.showDetails && (!order.orderItems || order.orderItems.length === 0)) {
      this.loadOrderDetails(orderId);
    } else {
      order.showDetails = !order.showDetails;
    }
  }

  loadOrderDetails(orderId: number): void {
    const order = this.orders.find(o => o.id === orderId);
    if (!order) return;

    this.orderService.getOrderDetails(orderId).subscribe({
      next: (details) => {
        order.orderItems = details.orderItems;
        order.showDetails = true;
      },
      error: (err) => {
        console.error('Error loading order details:', err);
        this.error = 'Failed to load order details';
      }
    });
  }

  downloadTicket(orderId: number): void {
    this.orderService.downloadTicket(orderId).subscribe({
      next: (data) => {
        const blob = new Blob([data], { type: 'application/pdf' });
        saveAs(blob, `ticket_${orderId}.pdf`);
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error downloading ticket:', err);
        this.error = 'Failed to download ticket. Please try again.';
      }
    });
  }

  formatDate(dateString: string): string {
    const options: Intl.DateTimeFormatOptions = { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    };
    return new Date(dateString).toLocaleDateString(undefined, options);
  }

  calculateTotalItems(order: OrderShoesDTO): number {
    return order.orderItems?.reduce((total, item) => total + item.quantity, 0) || 0;
  }

  hasCoupon(order: OrderShoesDTO): boolean {
    return !!order.cuponUsed;
  }
}