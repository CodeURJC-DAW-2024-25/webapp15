import { HttpClient,HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import {ShoeDTO} from "../dtos/shoe.dto";

@Injectable({providedIn: 'root'})
export class ShoeService {

    private baseUrl = '/api/v1/Shoes';


    constructor (private http: HttpClient) { } // Injecting HttpClient for making HTTP requests
    
    getShoes(page:number,size:number = 9):Observable<any>{
        const params =  new HttpParams().set('page',page).set('size',size); // Setting up query parameters for pagination
        return this.http.get<any>(this.baseUrl,{params}); // Making a GET request to fetch shoes with pagination
    }

    getShoesByBrand(page: number, brand: string, size:number = 9): Observable<any> {
        const params= new HttpParams()
        .set('brand',brand)
        .set('page',page)
        .set('size',size); // Setting up query parameters for brand and pagination
        return this.http.get<any>(`${this.baseUrl}/brand`, {params}); // Making a GET request to fetch shoes by brand with pagination
    }
      
    getShoesByCategory(page: number, category: string,size:number=9): Observable<any> {
        const params= new HttpParams()
        .set('category',category)
        .set('page',page) // Setting up query parameters for category and pagination
        .set('size',size); // Setting up query parameters for category and pagination
        return this.http.get<any>(`${this.baseUrl}/category`,{params});
    }

    getImageUrl(shoeId:number, imageNumber:number):string{
        return `${this.baseUrl}/${shoeId}/image/${imageNumber}`; // Constructing the image URL for a specific shoe and image number
    }

    updateShoe(shoeId: number, shoe: ShoeDTO): Observable<ShoeDTO> {
        return this.http.put<ShoeDTO>(`${this.baseUrl}/${shoeId}`, shoe);
    }

    deleteShoe(shoeId: number): Observable<any> {
        return this.http.delete(`${this.baseUrl}/${shoeId}`);
    }

    getShoeById(shoeId: number): Observable<ShoeDTO> {
        return this.http.get<ShoeDTO>(`${this.baseUrl}/${shoeId}`);
  
    } 
    createShoe(shoe: ShoeDTO): Observable<ShoeDTO> {
        return this.http.post<ShoeDTO>(this.baseUrl, shoe);
    }

    uploadImage(shoeId: number, imageNumber: number, formData: FormData): Observable<any> {
        // Make sure Content-Type is not set automatically (let browser set it with boundary)
        return this.http.post(`${this.baseUrl}/${shoeId}/image/${imageNumber}`, formData, {
            reportProgress: true,
            observe: 'response'
        });
    }
}