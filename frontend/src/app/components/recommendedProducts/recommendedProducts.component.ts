import { Component, OnInit, AfterViewInit } from '@angular/core';
import { RecommendedProductsService } from '../../services/recommendedProducts.service';
import Swiper from 'swiper';
import { Navigation } from 'swiper/modules';
import { LoginService } from '../../services/login.service';

Swiper.use([Navigation]);

interface Product {
  id: number;
  name: string;
  price: number;
  imageUrl1: string;
}

@Component({
  selector: 'app-recommended-products',
  templateUrl: './recommended-products.component.html',
  styleUrls: [
    '../../../assets/css/style.css',
    '../../../assets/css/vendor.css',
    './recommended-products.component.css'
  ]
})
export class RecommendedProductsComponent implements OnInit, AfterViewInit {
  recommendedShoes: Product[] = [];
  swiper: Swiper | undefined;
  isLoading: boolean = true;
  errorMessage: string | null = null;
  isAuthenticated: boolean = false;
  hasRecommendedShoes: boolean = false;

  constructor(
    public recommendedProductsService: RecommendedProductsService,
    private loginService: LoginService
  ) { }

  ngOnInit(): void {
    this.checkAuthentication();
    if (this.isAuthenticated) {
      this.loadRecommendedProducts();
    }
  }

  ngAfterViewInit(): void {
    this.initSwiper();
  }

  checkAuthentication(): void {
    //this.isAuthenticated = this.loginService.isAuthenticated();
  }

  loadRecommendedProducts(): void {
    this.isLoading = true;
    this.errorMessage = null;
    
    this.recommendedProductsService.getRecommendedProducts().subscribe({
      next: (response: any) => {
        this.recommendedShoes = response.recommendedShoes?.map((product: any) => ({
          id: product.id,
          name: product.name,
          price: product.price,
          imageUrl1: product.imageUrl1
        })) || [];
        
        this.hasRecommendedShoes = this.recommendedShoes.length > 0;
        this.isLoading = false;
        setTimeout(() => this.initSwiper(), 0);
      },
      error: (err) => {
        console.error('Error loading recommended products:', err);
        this.isLoading = false;
        this.errorMessage = 'Could not load recommended products. Please try again later.';
        this.hasRecommendedShoes = false;
      }
    });
  }

  initSwiper(): void {
    if (this.recommendedShoes.length > 0) {
      this.swiper = new Swiper('.swiper-container', {
        slidesPerView: 5,
        spaceBetween: 20,
        navigation: {
          nextEl: '.swiper-button-next',
          prevEl: '.swiper-button-prev',
        },
        observer: true,
        observeParents: true,
        watchOverflow: true,
        breakpoints: {
          320: { slidesPerView: 1, spaceBetween: 10 },
          640: { slidesPerView: 2, spaceBetween: 15 },
          768: { slidesPerView: 3, spaceBetween: 15 },
          1024: { slidesPerView: 4, spaceBetween: 20 },
          1200: { slidesPerView: 5, spaceBetween: 20 }
        }
      });
    }
  }
}