import { ReviewDTO } from './review.dto'; 
import { ShoeSizeStockDTO } from './shoesizestock.dto'; 


export interface ShoeDTO {
    id: number;                    // Long in Java mapped to number in TypeScript
    name: string;
    shortdescription: string;       // Short description of the shoe
    longDescription: string;       // Long description of the shoe
    price: number;                  // BigDecimal in Java mapped to number in TypeScript
    brand: string;
    category: string;
    imageUrl1: string;             // Image URL for the first image
    imageUrl2: string;             // Image URL for the second image
    imageUrl3: string;             // Image URL for the third image
    sizeStocks: ShoeSizeStockDTO[]; // List of available sizes and stock
    reviews: ReviewDTO[];           // List of reviews for the shoe
  }
  