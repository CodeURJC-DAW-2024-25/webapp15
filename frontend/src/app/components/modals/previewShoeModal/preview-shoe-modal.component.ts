import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ShoeDTO } from '../../../dtos/shoe.dto';
import { ShoeService } from '../../../services/shoe.service';

@Component({
  selector: 'app-preview-shoe-modal',
  templateUrl: './preview-shoe-modal.component.html',
})
export class PreviewShoeModalComponent {
  @Input() shoe!: ShoeDTO;

  constructor(
    public activeModal: NgbActiveModal,
    public shoeService: ShoeService
  ) {}
}
