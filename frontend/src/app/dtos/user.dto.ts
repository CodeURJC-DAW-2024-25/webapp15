import { OrderShoesDTO } from './ordershoes.dto'; 


export interface UserDTO {
    id: number;
    imageString?: string | null;
    firstname: string;
    lastName: string;
    roles: string[];
    username: string;
    email: string;
    orders?: OrderShoesDTO[] | null;
    password: string;
  }
  