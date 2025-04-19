import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginModalComponent } from './components/login-modal/login-modal.component';
import { ShopComponent } from './components/shop/shop.component';
import { ProfileDataComponent } from './components/profile-data/profile-data.component';

const routes: Routes = [
  {path: 'ver-login', component: LoginModalComponent},
  {path:'shop',component:ShopComponent},
  {path: 'ver-profileData', component: ProfileDataComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
