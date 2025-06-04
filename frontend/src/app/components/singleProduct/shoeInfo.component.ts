import { Component, OnInit, AfterViewInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SingleProductService } from '../../services/singleProduct.service';
import { ShoeDTO } from '../../dtos/shoe.dto';
import { LoginService } from '../../services/login.service';
import { UserService } from '../../services/user.service';
import { ShoeService } from '../../services/shoe.service';
import Swiper from 'swiper';
import { Navigation, Pagination, Thumbs } from 'swiper/modules';
import { ReviewDTO } from '../../dtos/review.dto';
import { ReviewService } from '../../services/reviews.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

Swiper.use([Navigation, Pagination, Thumbs]);

@Component({
  selector: 'app-product-info',
  templateUrl: './shoeInfo.component.html',
  styleUrls: ['../../../assets/css/style.css', '../../../assets/css/starCss.css']
})
export class ShoeInfoComponent implements OnInit, AfterViewInit {
  product: ShoeDTO | null = null;
  selectedSize: string | null = null;
  quantity: number = 1;
  isAuthenticated: boolean = false;
  swiperInitialized: boolean = false;
  reviews: ReviewDTO[] = [];
  isAdmin: boolean = false;
  reviewForm!: FormGroup;
  currentPage: number = 0;

  constructor(
    private route: ActivatedRoute,
    private productService: SingleProductService,
    public loginService: LoginService,
    public userService: UserService,
    public shoeService: ShoeService,
    private reviewService: ReviewService,// <- NUEVO
    private fb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const productId = params['id'];
      this.loadProduct(productId);
    });

    this.loginService.reqIsLogged();
    this.isAuthenticated = this.loginService.logged;


    this.reviewForm = this.fb.group({
      description: ['', Validators.required],
      rating: [null, Validators.required]
    });
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
        this.currentPage = 0
        // ✅ Cargar reseñas iniciales del producto
        this.reviewService.getReviewsByShoeId(productId, this.currentPage).subscribe({
          next: (reviews) => {
            this.reviews = reviews;
          },
          error: (err) => {
            console.error('Error loading reviews:', err);
          }
        });

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
  getStarsArray(rating: number): number[] {
    return Array(rating).fill(0);
  }

  deleteReview(reviewId: number): void {
    // Lógica para eliminar la reseña
    console.log('Eliminando reseña con ID:', reviewId);
  }

  loadMoreReviews(productId: number): void {
    this.currentPage++; // Incrementar la página
    this.reviewService.getReviewsByShoeId(productId, this.currentPage).subscribe({
      next: (newReviews) => {
        if (newReviews.length === 0) {
          console.log('No hay más reseñas para cargar.');
          this.currentPage--; // Si no hay más, deshacer el incremento
          return;
        }
        // ✅ Filtrar duplicados por ID
        // Crea un Set con los ids ya cargados
        const existingIds = new Set(this.reviews.map(r => r.id));

        // Filtra las nuevas reseñas que no estén ya cargadas
        const uniqueNewReviews = newReviews.filter(r => !existingIds.has(r.id));
        this.reviews = [...this.reviews, ...uniqueNewReviews]; // Agregar a las ya cargadas
      },
      error: (err) => {
        console.error('Error al cargar más reseñas:', err);
        this.currentPage--; // Revertir incremento en caso de error
      }
    });
  }

  submitReview(): void {
    if (this.reviewForm.valid && this.product && this.loginService.user) {
      const reviewData: ReviewDTO = {
        ...this.reviewForm.value,
        shoeId: this.product.id,
        userId: this.loginService.user.id
      };

      console.log('📤 Enviando reseña:', reviewData);

      this.reviewService.submitReview(reviewData).subscribe({
        next: (savedReview) => {
          console.log('✅ Reseña guardada:', savedReview);

          // Limpia el formulario
          this.reviewForm.reset();

          // Inserta la nueva reseña al inicio
          this.reviews.unshift(savedReview);
        },
        error: (err) => {
          console.error('❌ Error al guardar la reseña:', err);
          alert('Hubo un problema al enviar tu reseña.');
        }
      });
    } else {
      alert('Por favor completa todos los campos antes de enviar.');
    }
  }
  



}
