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
              this.message = 'Successfully logged in.';
              this.loginSuccess.emit();
              this.closeModal.emit();
              this.router.navigate(['/']);
            } else {
              this.isSuccess = false;
              this.message = response.message || 'Authentication failed. Please check your credentials and try again.';
              this.loginError = this.message;
            }
          },
          error: (err) => {
            this.isLoading = false;
            this.isSuccess = false;
            if (err.status === 500) {
              this.message = 'Incorrect username or password. Please try again';
            } else if (err.status === 0) {
              this.message = 'Cannot connect to the server. Please check your internet connection';
            } else {
              this.message = 'Unexpected error. Please try again';
            }
            this.loginError = this.message;
            console.error('Login Error: ', err);
          }
        });
    }
  }

  navigateToRegister(): void {
    this.closeModal.emit();
    this.router.navigate(['/']);
  }
}