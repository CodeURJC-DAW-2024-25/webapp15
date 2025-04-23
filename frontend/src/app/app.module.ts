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
import { ShoeEditComponent } from './components/shoe-edit/shoe-edit.component';
import { ProfileDataComponent } from './components/profile-data/profile-data.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AdminComponent } from './components/admin/admin.component';
import { AdminDashboardComponent } from './components/admin/admin-dashboard.component';
import { OrderCountChartComponent } from './components/admin/order-count-chart.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { MoneyGainedChartComponent } from './components/admin/money-gained-chart.component';

@NgModule({
  declarations: [
    ProfileDataComponent,
    AppComponent,
    FooterComponent,
    CouponComponent,
    LoginModalComponent,
    ShopComponent,
    ShoeCardComponent,
    ShoeEditComponent,
    ProfileDataComponent,
    AdminComponent,
    AdminDashboardComponent,
    OrderCountChartComponent,
    MoneyGainedChartComponent
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    RouterModule.forRoot([]),
    ReactiveFormsModule,
    NgxChartsModule
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch())
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
