import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ShoeDTO } from '../../dtos/shoe.dto';
import { ShoeService } from '../../services/shoe.service';

@Component({
  selector: 'app-delete-shoe-modal',
  templateUrl: './delete-shoe-modal.component.html'
})
export class DeleteShoeModalComponent {
  @Input() shoe!: ShoeDTO;

  constructor(public shoeService :ShoeService,public activeModal: NgbActiveModal) {}

  confirmDelete() {
    this.activeModal.close('confirm'); // Devuelve 'confirm' al padre
  }

  cancel() {
    this.activeModal.dismiss('cancel');
  }
}
