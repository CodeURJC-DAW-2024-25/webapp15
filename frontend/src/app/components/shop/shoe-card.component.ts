import { Component, Input } from '@angular/core';
import { ShoeDTO } from '../../dtos/shoe.dto';
import { ShoeService } from '../../services/shoe.service';

@Component({
    selector: 'app-shoe-card',
    templateUrl: './shoe-card.component.html',
    styleUrl: '../../../assets/css/style.css',
  })
  export class ShoeCardComponent {

    @Input() shoe!: ShoeDTO; //recieve a shoe object from the parent component
  
    constructor(public shoeService: ShoeService) {}
  }