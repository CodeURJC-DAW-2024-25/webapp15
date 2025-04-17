import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { FooterComponent } from './components/footer/footer.component';
import { CouponComponent } from './components/coupon/coupon.component';
import { LoginModalComponent } from './components/login-modal/login-modal.component';

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    CouponComponent,
    LoginModalComponent
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    RouterModule.forRoot([])
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch())
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
