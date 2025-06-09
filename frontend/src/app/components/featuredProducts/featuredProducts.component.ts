import { Component, OnInit, AfterViewInit } from '@angular/core';
import { FeaturedProductsService } from '../../services/featuredProducts.service';
import Swiper from 'swiper';
import { Navigation } from 'swiper/modules';

Swiper.use([Navigation]);
interface Product {
  id: number;
  name: string;
  price: number;
  imageUrl1: string;
}

@Component({
  selector: 'app-featured-products',
  templateUrl: './featuredProducts.component.html',
  styleUrls: ['../../../assets/css/style.css','../../../assets/css/vendor.css','./featuredProducts.component.css']
})
export class FeaturedProductsComponent implements OnInit, AfterViewInit {
  bestSellingShoes: Product[] = []; // Usamos la interfaz simplificada
  swiper: Swiper | undefined;
  isLoading: boolean = true;
  errorMessage: string | null = null;

  constructor(public featuredProductsService: FeaturedProductsService ) { }

  ngOnInit(): void {
    this.loadBestSellingProducts();
  }

  ngAfterViewInit(): void {
    this.initSwiper();
  }

  loadBestSellingProducts(): void {
    this.isLoading = true;
    this.errorMessage = null;
    
    this.featuredProductsService.getBestSellingProducts(10).subscribe({
      next: (response: any) => {
        // Extraemos solo los datos que necesitamos
        this.bestSellingShoes = response.bestSellingProducts.map((product: any) => ({
          id: product.id,
          name: product.name,
          price: product.price,
          imageUrl1: product.imageUrl1
        }));
        this.isLoading = false;
        setTimeout(() => this.initSwiper(), 0);
      },
      error: (err) => {
        console.error('Error loading best selling products:', err);
        this.isLoading = false;
        this.errorMessage = 'Could not load featured products. Please try again later.';
      }
    });
  }

  initSwiper(): void {
    if (this.bestSellingShoes.length > 0) {
      this.swiper = new Swiper('.swipperFeatured', {
        slidesPerView: 5,
        spaceBetween: 20, 
        navigation: {
          nextEl: '.swiper-button-next',
          prevEl: '.swiper-button-prev',
        },
        observer: true,
        observeParents: true,
        watchOverflow: true, // Añade esto para mejor manejo
        loop: true, // Opcional: para navegación infinita
        breakpoints: {
          320: { slidesPerView: 1, spaceBetween: 10 },
          640: { slidesPerView: 2, spaceBetween: 15 },
          768: { slidesPerView: 3, spaceBetween: 15 },
          1024: { slidesPerView: 4, spaceBetween: 20 },
          1200: { slidesPerView: 5, spaceBetween: 20 } // Cambiado a 5 para pantallas grandes
        }
      });
    }
  }
}