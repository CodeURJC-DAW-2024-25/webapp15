import { Component, EventEmitter, Output } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-login-modal',
  templateUrl: './login-modal.component.html',
  styleUrls: ['./login-modal.component.css']
})
export class LoginModalComponent {
  
  @Output() closeModal = new EventEmitter<void>();
  @Output() loginSuccess = new EventEmitter<void>();

  credentials = {
    username: '',
    password: ''
  };
  
  // Properties to fix issues or errors
  isLoading = false;
  message = '';
  isSuccess = false;
  showPassword = false;
  loginError: string | null = null;

  constructor(
    private router: Router,
    private loginService: LoginService
  ) {}

 
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(form: NgForm): void {
    if (form.valid) {
      this.isLoading = true;
      this.message = '';
      this.loginError = null;
      this.isSuccess = false;

      this.loginService.logIn(this.credentials.username, this.credentials.password)
        .subscribe({
          next: (response) => {
            this.isLoading = false;
            if (response.status === 'SUCCESS') {
              this.isSuccess = true;
              this.message = 'Inicio de sesión exitoso';
              this.loginSuccess.emit();
              this.closeModal.emit();
              this.router.navigate(['/']);
            } else {
              this.isSuccess = false;
              this.message = response.message || 'Error en la autenticación';
              this.loginError = this.message;
            }
          },
          error: (err) => {
            this.isLoading = false;
            this.isSuccess = false;
            if (err.status === 500) {
              this.message = 'Usuario o contraseña incorrectos';
            } else if (err.status === 0) {
              this.message = 'No se puede conectar con el servidor';
            } else {
              this.message = 'Error inesperado. Intente nuevamente';
            }
            this.loginError = this.message;
            console.error('Error en login:', err);
          }
        });
    }
  }

  navigateToRegister(): void {
    this.closeModal.emit();
    this.router.navigate(['/']);
  }
}