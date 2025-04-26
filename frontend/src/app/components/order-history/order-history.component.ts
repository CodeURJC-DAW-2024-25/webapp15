import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { OrderShoesDTO } from '../../dtos/ordershoes.dto';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-order-history',
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css']
})
export class OrderHistoryComponent implements OnInit {
  orders: (OrderShoesDTO & { showDetails: boolean })[] = [];
  isLoading = true;
  error: string | null = null;
  userId: number | null = null;
  hasMoreOrders = false;
  currentPage = 0;
  pageSize = 10;

  constructor(
    private orderService: OrderService,
    private authService: LoginService
  ) {}

  

  ngOnInit(): void {
  
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        if (user) {
  
          if (user.id !== undefined && user.id !== null) {
            this.userId = user.id;
            this.loadOrders(user.id);
          } else {
            this.error = 'Usuario sin ID válido';
            this.isLoading = false;
          }
  
        } else {
          this.error = 'Usuario no autenticado';
          this.isLoading = false;
        }
      },
      error: (err) => {
        console.error('Error al obtener el usuario:', err);
        this.error = 'Error al obtener los datos del usuario.';
        this.isLoading = false;
      }
    });
  }
  

  loadOrders(userId: number, page: number = 0, size: number = 10): void {
    if (!userId) return;

    this.isLoading = true;
    this.error = null;

    this.orderService.getOrdersByUserId(userId, page, size).subscribe({
      next: (orders) => {
        this.orders = orders.map(order => ({
          ...order,
          showDetails: false
        }));
        this.isLoading = false;
        this.hasMoreOrders = orders.length === size;
      },
      error: (err) => {
        this.error = 'Failed to load orders. Please try again later.';
        this.isLoading = false;
        console.error('Error loading orders:', err);
      }
    });
  }

  loadMoreOrders(): void {
    if (!this.userId || !this.hasMoreOrders) return;

    this.currentPage++;
    this.isLoading = true;

    this.orderService.getOrdersByUserId(this.userId, this.currentPage, this.pageSize)
      .subscribe({
        next: (newOrders) => {
          this.orders = [
            ...this.orders,
            ...newOrders.map(order => ({
              ...order,
              showDetails: false
            }))
          ];
          this.hasMoreOrders = newOrders.length === this.pageSize;
          this.isLoading = false;
        },
        error: (err) => {
          this.error = 'Failed to load more orders.';
          this.isLoading = false;
          console.error('Error loading more orders:', err);
        }
      });
  }

  toggleOrderDetails(orderId: number): void {
    const order = this.orders.find(o => o.id === orderId);
    if (order) {
      order.showDetails = !order.showDetails;
    }
  }

  downloadTicket(orderId: number): void {
    console.log('Downloading ticket for order:', orderId);
  }

  formatDate(dateString: string): string {
    const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  }

  calculateTotalItems(order: OrderShoesDTO): number {
    return order.orderItems.reduce((total, item) => total + item.quantity, 0);
  }

  hasCoupon(order: OrderShoesDTO): boolean {
    return order.cuponUsed !== null && order.cuponUsed !== undefined;
  }
}
