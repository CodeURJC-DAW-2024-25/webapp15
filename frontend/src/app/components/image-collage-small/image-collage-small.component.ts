import { Component, OnInit, AfterViewInit } from '@angular/core';

declare var jarallax: any; // <- para que TypeScript no se queje

@Component({
  selector: 'app-image-collage-small',
  templateUrl: './image-collage-small.component.html',
  styleUrls: ['../../../assets/css/style.css','./image-collage-small.component.css']
})
export class ImageCollageSmallComponent {

  constructor() { }

}
