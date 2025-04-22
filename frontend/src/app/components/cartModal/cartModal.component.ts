import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-cart-modal',
  templateUrl: './cartModal.component.html',
  styleUrls: ['../../../assets/css/style.css']
})
export class CartModalComponent {
  @Input() cartItems: any[] = [];
  @Input() subtotal: number = 0;
  @Output() checkout = new EventEmitter<void>();

  constructor(public activeModal: NgbActiveModal, private http: HttpClient) {}


  // Método para cerrar el modal
  closeModal() {
    this.activeModal.dismiss();
  }

  // Método para proceder al checkout
  proceedToCheckout() {
    this.checkout.emit();
    this.closeModal();
  }
}