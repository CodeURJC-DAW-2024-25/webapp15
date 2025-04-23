import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ShoeService } from '../../services/shoe.service';
import { ShoeDTO } from '../../dtos/shoe.dto';


@Component({
  selector: 'app-create-shoe',
  templateUrl: './create-shoe.component.html',
  styleUrl: '../../../assets/css/style.css'
})
export class CreateShoeComponent implements OnInit {
  shoeForm: FormGroup;
  selectedFiles: { [key: string]: File } = {};
  isSubmitting = false;
  errorMessage: string | null = null;
  uploadStatus = {
    shoe: false,
    images: false
  };

  brands = ['NIKE', 'PUMA', 'ADIDAS', 'NEW_BALANCE', 'VANS'];
  categories = ['SPORT', 'CASUAL', 'URBAN'];

  constructor(
    private fb: FormBuilder,
    private shoeService: ShoeService,
    private router: Router
  ) {
    this.shoeForm = this.fb.group({
      name: ['', Validators.required],
      shortDescription: ['', Validators.required],
      longDescription: ['', Validators.required],
      price: [null, [Validators.required, Validators.min(0)]],
      brand: ['', Validators.required],
      category: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  onFileSelected(event: any, imageNumber: number): void {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.selectedFiles[`image${imageNumber}`] = file;
    }
  }

  submitForm(): void {
    if (this.shoeForm.invalid) {
      this.markFormGroupTouched(this.shoeForm);
      return;
    }
  
    if (!this.selectedFiles['image1']) {
      this.errorMessage = 'At least one image is required.';
      return;
    }
  
    this.isSubmitting = true;
    this.errorMessage = null;
  
    const shoeData: ShoeDTO = {
      id: 0,
      name: this.shoeForm.value.name,
      shortDescription: this.shoeForm.value.shortDescription,
      longDescription: this.shoeForm.value.longDescription,
      price: this.shoeForm.value.price,
      brand: this.shoeForm.value.brand,
      category: this.shoeForm.value.category,
      imageUrl1: '',
      imageUrl2: '',
      imageUrl3: '',
      sizeStocks: [],
      reviews: []
    };
  
    this.shoeService.createShoe(shoeData).subscribe({
      next: (createdShoe: ShoeDTO) => {
        this.uploadStatus.shoe = true;
        console.log('Shoe created successfully with ID:', createdShoe.id);
  
        this.uploadImagesSequentially(createdShoe.id)
          .then(() => {
            console.log('All images uploaded successfully');
            this.isSubmitting = false;
            this.router.navigate(['/shop']); //
          })
          .catch(error => {
            console.error('Error uploading images:', error);
            this.errorMessage = 'Shoe created but there was an error uploading one or more images.';
            this.isSubmitting = false;
          });
      },
      error: error => {
        console.error('Error creating shoe:', error);
        this.errorMessage = 'Failed to create shoe. Please try again.';
        this.isSubmitting = false;
      }
    });
  }
  

  // Upload images one by one to ensure each one completes
  private async uploadImagesSequentially(shoeId: number): Promise<void> {
    try {
      // Process image 1 if exists
      if (this.selectedFiles['image1']) {
        const formData1 = new FormData();
        formData1.append('file', this.selectedFiles['image1']);
        await this.uploadSingleImage(shoeId, 1, formData1);
        console.log('Image 1 uploaded successfully');
      }
      
      // Process image 2 if exists
      if (this.selectedFiles['image2']) {
        const formData2 = new FormData();
        formData2.append('file', this.selectedFiles['image2']);
        await this.uploadSingleImage(shoeId, 2, formData2);
        console.log('Image 2 uploaded successfully');
      }
      
      // Process image 3 if exists
      if (this.selectedFiles['image3']) {
        const formData3 = new FormData();
        formData3.append('file', this.selectedFiles['image3']);
        await this.uploadSingleImage(shoeId, 3, formData3);
        console.log('Image 3 uploaded successfully');
      }
      
      return Promise.resolve();
    } catch (error) {
      return Promise.reject(error);
    }
  }

  private uploadSingleImage(shoeId: number, imageNumber: number, formData: FormData): Promise<any> {
    return new Promise((resolve, reject) => {
      this.shoeService.uploadImage(shoeId, imageNumber, formData).subscribe({
        next: result => resolve(result),
        error: error => reject(error)
      });
    });
  }
  

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if ((control as any).controls) {
        this.markFormGroupTouched(control as FormGroup);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/shoes']);
  }

  get formControls() {
    return this.shoeForm.controls;
  }
}