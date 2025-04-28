import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { OrderShoesDTO } from '../../dtos/ordershoes.dto';
import { saveAs } from 'file-saver';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';

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

  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      const routeUserId = params.get('userId');
      
      if (routeUserId) {
        this.id = +routeUserId;
        this.loadOrders(this.id);
      } else {
        // Temporalmente cargamos órdenes de un usuario fijo (ejemplo: ID 2)
        // Esto es solo para pruebas, quitar en producción
        this.id = 2;
        this.loadOrders(this.id);
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

  private loadOrders(id: number, page: number = 0, size: number = this.pageSize): void {
    this.isLoading = true;
    this.error = null;
  
    this.orderService.getOrdersByUserId(id, page, size).subscribe({
      next: (orders) => {
        this.orders = this.enhanceOrders(orders);
        this.isLoading = false;
        this.hasMoreOrders = orders.length === size;
      },
      error: (err) => {
        this.handleError('Error al cargar pedidos: ' + err.message);
      }
    });
  }

  private enhanceOrders(orders: OrderShoesDTO[]): EnhancedOrder[] {
    return orders.map(order => ({
      ...order,
      showDetails: false,
      totalItems: this.calculateTotalItems(order),
      formattedDate: this.formatDate(order.date)
    }));
  }

  // Resto de los métodos permanecen igual...
  loadMoreOrders(): void {
    if (!this.id || !this.hasMoreOrders) return;

    this.currentPage++;
    this.isLoading = true;

    this.orderService.getOrdersByUserId(this.id, this.currentPage, this.pageSize)
      .subscribe({
        next: (newOrders) => {
          this.orders = [...this.orders, ...this.enhanceOrders(newOrders)];
          this.hasMoreOrders = newOrders.length === this.pageSize;
          this.isLoading = false;
        },
        error: (err) => {
          this.handleError('Failed to load more orders.');
          console.error('Error loading more orders:', err);
        }
      });
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