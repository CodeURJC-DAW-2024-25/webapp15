import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ShoeDTO } from '../../../dtos/shoe.dto';
import { ShoeService } from '../../../services/shoe.service';

@Component({
  selector: 'app-add-to-cart-modal',
  templateUrl: './add-to-cart-modal.component.html',
})
export class AddToCartModalComponent {
  @Input() shoe!: ShoeDTO;

  constructor(
    public activeModal: NgbActiveModal,
    public shoeService: ShoeService
  ) {}

  confirmAddToCart() {
    // Esto solo cierra el modal por ahora, ya luego lo conectamos con la lógica real
    this.activeModal.close('added');
  }

  cancel() {
    this.activeModal.dismiss('cancel');
  }
}
