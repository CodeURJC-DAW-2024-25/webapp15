import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginModalComponent } from './components/login-modal/login-modal.component';

const routes: Routes = [{path: 'ver-login', component: LoginModalComponent}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
