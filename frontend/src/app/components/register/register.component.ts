// register.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RegisterService } from '../../services/register.service';
import { UserDTO } from '../../dtos/user.dto';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: '../../../assets/css/style.css',
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  error: string | null = null;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private registerService: RegisterService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      emailRepeated: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    }, {
      validators: this.emailMatchValidator
    });
  }

  emailMatchValidator(form: FormGroup) {
    const email = form.get('email')?.value;
    const emailRepeated = form.get('emailRepeated')?.value;
    
    if (email && emailRepeated && email !== emailRepeated) {
      form.get('emailRepeated')?.setErrors({ emailMismatch: true });
      return { emailMismatch: true };
    }
    
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.markFormGroupTouched(this.registerForm);
      return;
    }

    this.isSubmitting = true;
    this.error = null;

    const userData: UserDTO = {
       // Will be assigned by the server
       id: undefined,
      firstname: this.registerForm.value.firstName,
      lastName: this.registerForm.value.lastName,
      username: this.registerForm.value.username,
      email: this.registerForm.value.email,
      orders: null,
      password: this.registerForm.value.password,
      roles: ['USER'], // Default role
      
    };

    this.registerService.createUser(userData).subscribe({
      next: (response) => {
        console.log('Registration successful:', response);
        this.isSubmitting = false;
        // Redirect to login page after successful registration
        this.router.navigate(['/shop']);
      },
      error: (err) => {
        console.error('Registration error:', err);
        this.isSubmitting = false;
        this.error = err.error?.message || 'Registration failed. Please try again.';
      }
    });
  }

  // Helper to mark all controls as touched for validation display
  markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/shop']);
  }
}