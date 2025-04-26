import { OrderShoesDTO } from './ordershoes.dto'; 


export interface UserDTO {
    id?: number;
    imageString?: string;
    firstname: string;
    lastName: string;
    roles: string[];
    username: string;
    email: string;
    orders: OrderShoesDTO[] | null;  // Definir OrderShoesDTO también en TypeScript
    password: string;
  }
  