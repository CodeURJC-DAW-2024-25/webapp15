import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginModalComponent } from './components/login-modal/login-modal.component';
import { ShopComponent } from './components/shop/shop.component';
<<<<<<< HEAD
import { ProfileDataComponent } from './components/profile-data/profile-data.component';
=======
import { AdminComponent } from './components/admin/admin.component';
>>>>>>> 59cbf27b0658cb5f584365dfbe28d53c48b9ecbb

const routes: Routes = [
  {path: 'ver-login', component: LoginModalComponent},
  {path:'shop',component:ShopComponent},
<<<<<<< HEAD
  {path: 'ver-profileData', component: ProfileDataComponent}
=======
  { path: 'admin', component: AdminComponent },

>>>>>>> 59cbf27b0658cb5f584365dfbe28d53c48b9ecbb
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
