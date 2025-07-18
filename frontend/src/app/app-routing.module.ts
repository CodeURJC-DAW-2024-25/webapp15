import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginModalComponent } from './components/login-modal/login-modal.component';
import { ShopComponent } from './components/shop/shop.component';
import { AdminComponent } from './components/admin/admin.component';
import { ShoeEditComponent } from './components/shoe-edit/shoe-edit.component';
import { CreateShoeComponent } from './components/create-shoe/create-shoe.component';
import { ShoeInfoComponent } from './components/singleProduct/shoeInfo.component';
import { ProfilePageComponent } from './components/profile-page/profile-page.component';
import { RegisterComponent } from './components/register/register.component';
import { CheckoutComponent } from './components/CheckOut/checkout.component';
import { MainComponent } from './components/main/main.component';
import { ErrorComponent } from './components/error/error.component';


const routes: Routes = [
  { path: '', component: MainComponent },
  { path: 'ver-login', component: LoginModalComponent },
  { path: 'shop', component: ShopComponent },
  { path: 'shop/edit/:id', component: ShoeEditComponent },
  { path: 'admin', component: AdminComponent },
  { path: 'create-shoe', component: CreateShoeComponent },
  { path: 'shop/single-product/:id', component: ShoeInfoComponent },
  { path: 'profilePage', component: ProfilePageComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'error', component: ErrorComponent },
  { path: '**', redirectTo: 'error' }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
