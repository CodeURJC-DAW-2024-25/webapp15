import { Component } from '@angular/core';
import { ShippingFormData } from './shipping-form/shipping-form.component'; // Definiremos esta interfaz

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrl: '../../../assets/css/style.css'
})
export class CheckoutComponent {

  constructor() {}

  onSubmit(formData: ShippingFormData) {
    console.log('Formulario de checkout enviado:', formData);
    // Aquí luego haremos el POST al backend para generar el ticket
  }
}
