import { CouponDTO } from './coupon.dto'; 
import { OrderItemDTO } from './orderitem.dto';  

export interface OrderShoesDTO {
  id: number;
  date: string;     // LocalDate in Java can be represented as an ISO 8601 string in TypeScript (yyyy-mm-dd)
  cuponUsed: string;
  country: string;
  firstName: string;
  secondName: string;
  email: string;
  address: string;
  numerPhone: string;
  summary: number;  
  state: string;
  userId: number;
  coupon: CouponDTO;  
  orderItems: OrderItemDTO[]; 
}
