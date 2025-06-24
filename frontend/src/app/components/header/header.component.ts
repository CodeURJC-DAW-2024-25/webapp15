import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CartModalComponent } from '../cartModal/cartModal.component';
import { LoginModalComponent } from '../login-modal/login-modal.component';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['../../../assets/css/style.css']
})
export class HeaderComponent {

  isAdmin = false;
  isAuthenticated = false;
  cartItems: any[] = []; 
  subtotal: number = 0; 
  isUser = false;

  constructor(private http: HttpClient, private modalService: NgbModal,public loginService: LoginService) { }

  ngOnInit(): void {
    this.loginService.reqIsLogged();
    setTimeout(() => {
     
      this.isAuthenticated = this.loginService.logged;
      this.isAdmin = this.loginService.user?.roles.includes('ROLE_ADMIN') ?? false;
      this.isUser = this.loginService.user?.roles.includes('ROLE_USER') ?? false;

      console.log("prueba de que es admin:", this.isAdmin);
      console.log("Prueba de que es user:", this.isUser);

    }, 300);

  }
  openCartModal() {
    const modalRef = this.modalService.open(CartModalComponent, {
      centered: true
    });
  }
  openLoginModal() {
    const modalRef = this.modalService.open(LoginModalComponent,{
      centered: true
    })
  }
}