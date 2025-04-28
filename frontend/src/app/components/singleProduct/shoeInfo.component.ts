import { Component, OnInit, AfterViewInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SingleProductService } from '../../services/singleProduct.service';
import { ShoeDTO } from '../../dtos/shoe.dto';
import { LoginService } from '../../services/login.service';
import { ShoeService } from '../../services/shoe.service';
import Swiper from 'swiper';
import { Navigation, Pagination, Thumbs } from 'swiper/modules';

Swiper.use([Navigation, Pagination, Thumbs]);

@Component({
  selector: 'app-product-info',
  templateUrl: './shoeInfo.component.html',
  styleUrls: ['../../../assets/css/style.css','../../../assets/css/starCss.css']
})
export class ShoeInfoComponent implements OnInit, AfterViewInit {
  product: ShoeDTO | null = null;
  selectedSize: string | null = null;
  quantity: number = 1;
  isAuthenticated: boolean = false;
  swiperInitialized: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private productService: SingleProductService,
    public loginService: LoginService,
    public shoeService: ShoeService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const productId = params['id'];
      this.loadProduct(productId);
    });

    this.loginService.reqIsLogged();
    this.isAuthenticated = this.loginService.logged;
  }

  ngAfterViewInit(): void {
    this.tryInitSwiper();
  }

  private tryInitSwiper(): void {
    const checkAndInit = () => {
      if (this.product && !this.swiperInitialized) {
        setTimeout(() => {
          new Swiper('.product-large-slider', {
            slidesPerView: 1,
            spaceBetween: 10,
            pagination: {
              el: '.swiper-pagination',
              clickable: true,
            },
          });

          new Swiper('.product-thumbnail-slider', {
            slidesPerView: 3,
            spaceBetween: 10,
            direction: 'vertical',
            watchSlidesProgress: true,
          });

          this.swiperInitialized = true;
        }, 100); // delay para asegurar que el DOM esté listo
      }
    };

    const observer = new MutationObserver(checkAndInit);
    observer.observe(document.body, { childList: true, subtree: true });
  }

  loadProduct(productId: number): void {
    this.productService.getProductById(productId).subscribe({
      next: (product) => {
        this.product = product;
        // También intentar inicializar el swiper aquí por si se cargó después
        setTimeout(() => this.tryInitSwiper(), 100);
      },
      error: (err) => {
        console.error('Error loading product:', err);
      }
    });
  }

  selectSize(size: string): void {
    this.selectedSize = size;
  }

  increaseQuantity(): void {
    this.quantity++;
  }

  decreaseQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  addToCart(): void {
    if (!this.product || !this.selectedSize) {
      console.error('Product or size not selected');
      return;
    }

    console.log(`Adding to cart: Product ID ${this.product.id}, Size ${this.selectedSize}, Quantity ${this.quantity}`);
  }

  isSizeOutOfStock(size: string): boolean {
    if (!this.product) return false;
    const sizeStock = this.product.sizeStocks.find(s => s.size === size);
    return sizeStock ? sizeStock.stock <= 0 : true;
  }
}
