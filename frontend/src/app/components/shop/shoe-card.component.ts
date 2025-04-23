import { Component, Input, Output,EventEmitter } from '@angular/core';
import { ShoeDTO } from '../../dtos/shoe.dto';
import { ShoeService } from '../../services/shoe.service';

@Component({
    selector: 'app-shoe-card',
    templateUrl: './shoe-card.component.html',
    styleUrl: '../../../assets/css/style.css',
  })
  export class ShoeCardComponent {

    @Input() shoe!: ShoeDTO; //recieve a shoe object from the parent component
    @Output() deleteClick = new EventEmitter<ShoeDTO>(); //emit an event when the delete button is clickede  
    constructor(public shoeService: ShoeService) {}
  }