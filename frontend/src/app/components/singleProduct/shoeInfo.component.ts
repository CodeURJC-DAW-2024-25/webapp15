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
import { OrderShoesService } from '../../services/order-shoes.service';
import { ShoeSizeStockService } from '../../services/shoesizestock.service';
import { OrderItemDTO } from '../../dtos/orderitem.dto';

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
  isUser: boolean = false;
  reviewForm!: FormGroup;
  currentPage: number = 0;

  constructor(
    private route: ActivatedRoute,
    private productService: SingleProductService,
    public loginService: LoginService,
    public userService: UserService,
    public shoeService: ShoeService,
    private reviewService: ReviewService,
    private fb: FormBuilder,
    private orderShoesService: OrderShoesService,
    private stockService: ShoeSizeStockService

  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const productId = params['id'];
      this.loadProduct(productId);
    });

    this.loginService.reqIsLogged();
    setTimeout(() => {
   
      this.isAuthenticated = this.loginService.logged;
      this.isAdmin = this.loginService.user?.roles.includes('ROLE_ADMIN') ?? false;
      this.isUser = this.loginService.user?.roles.includes('ROLE_USER') ?? false;

      console.log("prueba de que es admin:", this.isAdmin);
      console.log("Prueba de que es user:", this.isUser);

    }, 300);


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
        }, 100); 
      }
    };

    const observer = new MutationObserver(checkAndInit);
    observer.observe(document.body, { childList: true, subtree: true });
  }

  loadProduct(productId: number): void {
    this.productService.getProductById(productId).subscribe({
      next: (product) => {
        this.product = product;
        // try to init swiper
        setTimeout(() => this.tryInitSwiper(), 100);
        this.currentPage = 0
        // load comments of the shoe
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

  

  isSizeOutOfStock(size: string): boolean {
    if (!this.product) return false;
    const sizeStock = this.product.sizeStocks.find(s => s.size === size);
    return sizeStock ? sizeStock.stock <= 0 : true;
  }
  getStarsArray(rating: number): number[] {
    return Array(rating).fill(0);
  }

  loadMoreReviews(productId: number): void {
    this.currentPage++; // increase page 
    this.reviewService.getReviewsByShoeId(productId, this.currentPage).subscribe({
      next: (newReviews) => {
        if (newReviews.length === 0) {
          console.log('No hay más reseñas para cargar.');
          this.currentPage--; //if there is no more, decrement
          return;
        }
        // ✅ filter duplicates by ID
        //create a Set with already loaded ids
        const existingIds = new Set(this.reviews.map(r => r.id));

        // filter new reviews that are not already loaded
        const uniqueNewReviews = newReviews.filter(r => !existingIds.has(r.id));
        this.reviews = [...this.reviews, ...uniqueNewReviews]; // add the new reviews to the existing ones
      },
      error: (err) => {
        console.error('Error al cargar más reseñas:', err);
        this.currentPage--; // revert increment in case of error
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

          //  clean the form
          this.reviewForm.reset();

          // insert the review at the beginning
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

  deleteReview(reviewId: number): void {
    if (!reviewId || !this.product) return;

    if (!this.isAdmin) {
      alert('No tienes permisos para esta acción');
      return;
    }

    if (confirm('¿Estás seguro de que quieres eliminar esta reseña?')) {
      this.reviewService.deleteReview(reviewId).subscribe({
        next: () => {
        
          this.reviews = this.reviews.filter(r => r.id !== reviewId);

          
          this.reviewService.getReviewsByShoeId(this.product!.id, this.currentPage)
            .subscribe(updatedReviews => {
              this.reviews = updatedReviews;
            });
        },
        error: (err) => {
          console.error('Error eliminando reseña:', err);
          alert('No se pudo eliminar la reseña');
        }
      });
    }
  }

  addToCart(): void {
  if (!this.product || !this.selectedSize) {
    console.error('Product or size not selected');
    return;
  }

  const userId = this.loginService.user?.id;
  if (!userId) {
    alert("Debes iniciar sesión para añadir al carrito.");
    return;
  }

  // verify stock before continuing
  this.stockService.checkStock([this.product.id], [this.selectedSize]).subscribe({
    next: (stockMap) => {
      const key = `${this.product!.id}_${this.selectedSize}`;
      const availableStock = stockMap[key] ?? 0;

      if (availableStock < this.quantity) {
        alert('No hay suficiente stock disponible');
        return;
      }

      //  get the current cart
      this.orderShoesService.getCartByUserId(userId).subscribe({
        next: (cart) => {
          const updatedCart = { ...cart };
          
          const existingItem = updatedCart.orderItems?.find(item =>
            item.shoeId === this.product!.id && item.size === this.selectedSize
          );

          if (existingItem) {
            // update cuantity if it already exists
            existingItem.quantity += this.quantity;
          } else {
            //create a new order item
            const newItem: OrderItemDTO = {
              orderId: updatedCart.id,
              shoeId: this.product!.id,
              shoeName: this.product!.name,
              quantity: this.quantity,
              size: this.selectedSize!,
              price: this.product!.price
            };

            updatedCart.orderItems = [...(updatedCart.orderItems || []), newItem];
          }

          
          updatedCart.state = 'notFinished';

          // send update to the server
          this.orderShoesService.updateOrderShoe(updatedCart.id, updatedCart).subscribe({
            next: () => {
              console.log("Producto añadido al carrito");
              alert("Producto añadido al carrito");
            },
            error: (err) => {
              console.error("Error actualizando carrito:", err);
              alert("No se pudo añadir al carrito");
            }
          });
        },
        error: (err) => {
          console.error("Error obteniendo carrito:", err);
          alert("Error obteniendo carrito del usuario");
        }
      });
    },
    error: () => {
      console.error('Error consultando stock');
      alert('Error al verificar disponibilidad');
    }
  });
}




}
