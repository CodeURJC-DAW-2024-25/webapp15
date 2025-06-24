import { Component, EventEmitter, Output } from '@angular/core';
import { NgForm } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';
import Cookies from 'js-cookie';// Import js-cookie

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
    public activeModalLogin: NgbActiveModal,
    private loginService: LoginService
  ) { }

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
            if (response.status === 'SUCCESS') {
              this.isSuccess = true;
              this.message = 'Successfully logged in.';
              this.closeModalLogin();

              this.loginService.getCurrentUser().subscribe({
                next: (user) => {
                  if (user && user.id !== undefined && user.id !== null) {
                    Cookies.set('userId', user.id.toString(), { expires: 7, path: '/' });
                    Cookies.set('username', user.username, { expires: 7, path: '/' });
                    Cookies.set('email', user.email, { expires: 7, path: '/' });
                    Cookies.set('firstname', user.firstname, { expires: 7, path: '/' });
                    Cookies.set('lastname', user.lastName, { expires: 7, path: '/' });


                    this.loginSuccess.emit();
                    this.closeModal.emit();
                    window.location.href = '/';

                  } else {
                    console.error('User ID is undefined or null, cannot set cookie.');
                  }
                },
                error: (err) => {
                  console.error('Error al obtener usuario:', err);
                }
              });




            } else {
              this.isSuccess = false;
              this.message = response.message || 'Authentication failed. Please check your credentials and try again.';
              this.loginError = this.message;
            }
          },
          error: (err) => {
            this.isLoading = false;
            this.isSuccess = false;
            this.message = err.message || 'Unexpected error. Please try again';
            this.loginError = this.message;
          }
        });
    } else {
      return;
    }
  }

  navigateToRegister(): void {
    this.closeModal.emit();
    this.router.navigate(['/']);
  }

  // Método para cerrar el modal
  closeModalLogin() {
    this.activeModalLogin.dismiss();

    const cartButton = document.getElementById('cart-button');
    if (cartButton) {
      cartButton.focus();
    }
  }
}
