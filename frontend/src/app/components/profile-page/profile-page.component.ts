import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { Router } from '@angular/router';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.css']
})
export class ProfilePageComponent implements OnInit {
  loading = true;
  error = false;

  constructor(
    private loginService: LoginService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.checkAuthenticationAndLoadProfile();
  }

  private checkAuthenticationAndLoadProfile(): void {
    
    this.loginService.checkSession().pipe(take(1)).subscribe({
      next: (isLoggedIn) => {
        if (isLoggedIn) {
         
          if (!this.loginService.user) {
            this.loginService.getCurrentUser().pipe(take(1)).subscribe({
              next: (user) => {
                if (user) {
                  this.loginService.user = user;
                  this.loading = false;
                } else {
                  this.handleUnauthorizedAccess();
                }
              },
              error: () => this.handleUnauthorizedAccess()
            });
          } else {
            this.loading = false;
          }
        } else {
          this.handleUnauthorizedAccess();
        }
      },
      error: () => this.handleUnauthorizedAccess()
    });
  }

  private handleUnauthorizedAccess(): void {
    this.loading = false;
    this.error = true;
    console.warn('Redirigiendo al login: usuario no autenticado');
    this.router.navigate(['/login']);
  }
}