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
import { ShopComponent } from './components/shop/shop.component';
import { ShoeCardComponent } from './components/shop/shoe-card.component';
import { ProfileDataComponent } from './components/profile-data/profile-data.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ProfilePurchaseHistoryComponent } from './components/profile-purchase-history/profile-purchase-history.component';

@NgModule({
  declarations: [
    ProfileDataComponent,
    AppComponent,
    FooterComponent,
    CouponComponent,
    LoginModalComponent,
    ShopComponent,
    ShoeCardComponent,
    ProfileDataComponent,
    ProfilePurchaseHistoryComponent
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    RouterModule.forRoot([]),
    ReactiveFormsModule
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch())
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
