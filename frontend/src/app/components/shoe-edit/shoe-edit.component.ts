import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ShoeService } from '../../services/shoe.service';
import { ShoeDTO } from '../../dtos/shoe.dto';

@Component({
    selector: 'app-shoe-edit',
    templateUrl: './shoe-edit.component.html',
    styleUrl: '../../../assets/css/style.css'

})
export class ShoeEditComponent implements OnInit{

    shoe!: ShoeDTO; 
    loading = true;
    error   = '';

    imageFiles: { [key: string]: File } = {};
  
    constructor(
        private route: ActivatedRoute, // para obtener el id del zapato de la URL
        private router: Router,         // para redirigir a otra página después de editar
        private shoeService: ShoeService // servicio para obtener y actualizar los datos del zapato
    ){}
    
    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id'); // obtener el id del zapato de la URL

        this.shoeService.getShoeById(Number(id)).subscribe({
            next: (shoe) =>{
                this.shoe=shoe;
                this.loading=false;
            },
            error: () => {
                this.error = 'Unable to load shoe data';
                this.loading = false;
            }
        });
    }

    onFileChange(evt: Event, field: 'image1'|'image2'|'image3') {

        const file = (evt.target as HTMLInputElement).files?.[0];
        if (file) {
            this.imageFiles[field] = file; 
        }

      }

    save(): void {
        this.shoeService.updateShoe(this.shoe.id!,this.shoe).subscribe({
            next: () => {this.uploadImages(this.shoe.id)},
            error: () => {this.error = 'Unable to update shoe data';} // mostrar error si no se puede actualizar
        });
    }

    private uploadImages(id: number): void {

        const idx = { image1: 1, image2: 2, image3: 3 } as const;
        
        const uploads: Array<[number, File]> = Object
          .entries(this.imageFiles)                         // [['image2', File], ...]
          .map(([k, file]) => [idx[k as keyof typeof idx], file as File]);
      
        if (uploads.length === 0) {
          this.router.navigate(['/shop']);
          return;
        }
      
        /* ③ enviamos cada POST y contamos cuándo terminan todas */
        let pending = uploads.length;
      
        uploads.forEach(([n, file]) => {
          this.shoeService.updateImage(id, n as 1 | 2 | 3, file).subscribe({
            next: () => {
              pending--;
              if (pending === 0) {                // último en terminar → navegar
                this.router.navigate(['/shop']);
              }
            },

            error: () => {                        // si cualquier subida falla
              this.error = 'Image upload failed';
              pending = 0;                        // cancelamos conteo para que no navegue
            }
          });
        });
      }
      

    cancel(): void {
        this.router.navigate(['/shop']); // redirigir a la página de la tienda si se cancela
    }

}