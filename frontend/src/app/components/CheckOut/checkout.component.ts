import { Component,OnInit } from '@angular/core';
import { ShippingFormData } from './shipping-form/shipping-form.component'; // Definiremos esta interfaz
import {LoginService} from '../../services/login.service';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrl: '../../../assets/css/style.css'
})
export class CheckoutComponent implements OnInit {

  constructor(private loginService:LoginService) {}

  ngOnInit(): void {
    this.loginService.reqIsLogged();
    setTimeout(() => {
      if (!this.loginService.user) {
        alert('Error: no se pudo obtener el usuario. Por favor, vuelve a iniciar sesión.');
        console.error('Usuario nulo después de intentar login.');
      }
    }, 500);  // Puedes ajustar el tiempo si quieres
  }
  

  onSubmit(formData: ShippingFormData) {
    console.log('Formulario de checkout enviado:', formData);
    // Aquí luego haremos el POST al backend para generar el ticket
  }
}
