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
import { HeaderComponent } from './components/header/header.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap'; 
import { CartModalComponent } from './components/cartModal/cartModal.component'
import { FeaturedProductsComponent } from './components/featuredProducts/featuredProducts.component';
import { CreateShoeComponent } from './components/create-shoe/create-shoe.component'; 
import { DeleteShoeModalComponent } from './components/modals/deleteShoeModal/delete-shoe-modal.component';
import { OrderHistoryComponent } from './components/order-history/order-history.component';
import { ProfilePageComponent } from './components/profile-page/profile-page.component';
import { ShoeInfoComponent } from './components/singleProduct/shoeInfo.component';
import { RegisterComponent } from './components/register/register.component'
import { CheckoutComponent } from './components/CheckOut/checkout.component';
import { ShippingFormComponent } from './components/CheckOut/shipping-form/shipping-form.component';
import { CartSummaryComponent } from './components/CheckOut/cart-summary/cart-summary.component';
import { ImageCollageBigComponent } from './components/image-collage-big/image-collage-big.component';
import { RecommendedProductsComponent } from './components/recommendedProducts/recommendedProducts.component'
import { UserSpendingChartComponent } from './components/profile-data/profile-chart.component';
import { ImageCollageSmallComponent } from './components/image-collage-small/image-collage-small.component';
import { MainComponent } from './components/main/main.component';
import { ErrorComponent } from './components/error/error.component';



@NgModule({
  declarations: [
    ProfileDataComponent,
    RecommendedProductsComponent,
    ShoeInfoComponent,
    FeaturedProductsComponent,
    ImageCollageBigComponent,
    AppComponent,
    CartModalComponent,
    FooterComponent,
    HeaderComponent,
    CouponComponent,
    LoginModalComponent,
    ShopComponent,
    ShoeCardComponent,
    ShoeEditComponent,
    AdminComponent,
    AdminDashboardComponent,
    OrderCountChartComponent,
    MoneyGainedChartComponent,
    CreateShoeComponent,
    DeleteShoeModalComponent,
    OrderHistoryComponent,
    ProfilePageComponent,
    RegisterComponent,
    CheckoutComponent,
    ShippingFormComponent,
    CartSummaryComponent,
    UserSpendingChartComponent,
    ImageCollageSmallComponent,
    MainComponent,
    ErrorComponent
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    NgbModule,
    AppRoutingModule,
    FormsModule,
    RouterModule.forRoot([]),
    ReactiveFormsModule,
    NgxChartsModule
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch()),
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }