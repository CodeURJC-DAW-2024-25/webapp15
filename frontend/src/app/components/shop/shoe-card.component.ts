import { Component, Input, Output,EventEmitter} from '@angular/core';
import { ShoeDTO } from '../../dtos/shoe.dto';
import { ShoeService } from '../../services/shoe.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DeleteShoeModalComponent } from '../deleteShoeModal/delete-shoe-modal.component';

/*
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ShoeDTO } from '../../dtos/shoe.dto';
import { ShoeService } from '../../services/shoe.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DeleteShoeModalComponent } from '../deleteShoeModal/delete-shoe-modal.component';
*/ 

@Component({
    selector: 'app-shoe-card',
    templateUrl: './shoe-card.component.html',
    styleUrl: '../../../assets/css/style.css',
  })
  export class ShoeCardComponent {

    @Input() shoe!: ShoeDTO; //recieve a shoe object from the parent component
   
    @Output() shoeDeleted = new EventEmitter<number>();

    constructor(public shoeService: ShoeService, private modalService: NgbModal) {}

    openDeleteModal(){
      const modalRef=this.modalService.open(DeleteShoeModalComponent,{
        centered:true
      });
      
      modalRef.componentInstance.shoe=this.shoe; //pass the shoe object to the modal

      modalRef.result.then(result => {
        if(result === 'confirm'){this.deleteShoe();}
      },()=>{}
      );
    }

    deleteShoe(): void {
      this.shoeService.deleteShoe(this.shoe.id!).subscribe(() => {
        this.shoeDeleted.emit(this.shoe.id!); // Emit the shoe ID to the parent component
      });
    }

  }