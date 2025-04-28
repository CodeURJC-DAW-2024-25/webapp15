import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

export interface ShippingFormData {
  country: string;
  coupon: string;
  firstName: string;
  lastName: string;
  email: string;
  address: string;
  phone: string;
}

@Component({
  selector: 'app-shipping-form',
  templateUrl: './shipping-form.component.html',
  styleUrl: '../../../../assets/css/style.css',
})
export class ShippingFormComponent {

  shippingForm: FormGroup;

  @Output() formSubmit = new EventEmitter<ShippingFormData>();

  constructor(private fb: FormBuilder) {
    this.shippingForm = this.fb.group({
      country: ['', Validators.required],
      coupon: [''],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      address: ['', Validators.required],
      phone: ['', Validators.required],
    });
  }

  submitForm() {
    if (this.shippingForm.valid) {
      this.formSubmit.emit(this.shippingForm.value);
    } else {
      console.log('Formulario inválido, revisa los campos.');
    }
  }
}
