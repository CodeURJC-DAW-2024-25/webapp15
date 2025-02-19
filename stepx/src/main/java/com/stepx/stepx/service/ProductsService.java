package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.stepx.stepx.model.Product;
import com.stepx.stepx.model.Product;

@Service
public class ProductsService {
    
    private ConcurrentMap<Long, Product> products= new ConcurrentHashMap<>();//allows to use the map in a concurrent way
    private ConcurrentMap<Long, Product> products= new ConcurrentHashMap<>();//allows to use the map in a concurrent way
    private AtomicLong nextId = new AtomicLong();//allows to work with the id in a concurrent way

    public ProductsService(){
        save(new Product("Nike Air Yellow",
        "The Nike Air Max 270 React ENG combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort and a striking visual experience.",
        "The shoes upper features lightweight, no-sew materials that create a modern aesthetic that looks as good as it feels.",
        90,
        "Nike",
        new ArrayList<String>(Arrays.asList("/images/shopimages/Yellow-nike-shop-shoe.jpg", "/images/shopimages/Red-nike-shop-shoe.jpg")),
        new ArrayList<Integer>(Arrays.asList(94, 55, 28,10)),
        "sport"
        ));
        save(new Product("Red Nike Runnin", "Perfect for run long distances",
        "the style and confy of the shoe is somthing that most of the brands would like to have",
        100,
        "nike",
        new ArrayList<>(Arrays.asList("/images/shopimages/Red-nike-shop-shoe.jpg","/images/shopimages/Red-nike-shop-shoe.jpg")),
        new ArrayList<Integer>(Arrays.asList(50, 10, 80,25)),
        "casual"
        ));
        
        save(new Product("coloredPair", "Perfect for run long distances",
        "the style and confy of the shoe is somthing that most of the brands would like to have",
        70,
        "reebok",
        new ArrayList<>(Arrays.asList("/images/shopimages/paircolored-shop-shoe.jpg","/images/shopimages/Red-nike-shop-shoe.jpg")),
        new ArrayList<Integer>(Arrays.asList(80, 21, 56,52)),
        "urban"
        ));

       save(new product("orange-white-shoe", "confy", "stylish and moder shoe for mature people", 80, "nike", 
       new ArrayList<>(Arrays.asList("/images/shopimages/orange-white-shop-shoe.jpg","/images/shopimages/Red-nike-shop-shoe.jpg")),
       new ArrayList<Integer>(Arrays.asList(72, 17, 84,84)),
       "urban"
       ));

       save(new product("pink-newbalance-shoe", "stylish", "suitable shoe for most of the world", 60, "new balance", 
       new ArrayList<>(Arrays.asList("/images/shopimages/pink-newbalance-shoe.jpg","/images/shopimages/Red-nike-shop-shoe.jpg")),
       new ArrayList<Integer>(Arrays.asList(76, 34, 3,16)),
       "casual"
       ));

       save(new Product("Nike-Air-Pink",
       "The Nike Air Max 270 React ENG combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort and a striking visual experience.",
       "The shoes upper features lightweight, no-sew materials that create a modern aesthetic that looks as good as it feels.",
       99,
       "Nike",
       new ArrayList<String>(Arrays.asList("/images/shopimages/Nike-Air-Pink.jpg","/images/shopimages/Red-nike-shop-shoe.jpg")),
       new ArrayList<Integer>(Arrays.asList(78, 90, 36,12)),
       "sport"
       ));

       save(new product("red-converse-shoe", "confy", "stylish and moder shoe for mature people", 120, "converse", 
       new ArrayList<>(Arrays.asList("/images/shopimages/red-converse-shoe.jpg","/images/shopimages/Red-nike-shop-shoe.jpg")),
       new ArrayList<Integer>(Arrays.asList(26, 46, 84,47)),
       "casual"
       ));


       save(new Product("Nike-Air-multicolor",
       "The Nike Air Max 270 React ENG combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort and a striking visual experience.",
       "The shoes upper features lightweight, no-sew materials that create a modern aesthetic that looks as good as it feels.",
       85,
       "Nike",
       new ArrayList<String>(Arrays.asList("/images/shopimages/Nike-Air-multicolor.jpg","/images/shopimages/Red-nike-shop-shoe.jpg")),
       new ArrayList<Integer>(Arrays.asList(26, 43, 62,79)),
         "sport"
       ));


       save(new Product("Nike-Air-grey",
       "The Nike Air Max 270 React ENG combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort and a striking visual experience.",
       "The shoes upper features lightweight, no-sew materials that create a modern aesthetic that looks as good as it feels.",
       134,
       "Nike",
       new ArrayList<String>(Arrays.asList("/images/shopimages/Nike-Air-grey.jpg","/images/shopimages/Red-nike-shop-shoe.jpg")),
       new ArrayList<Integer>(Arrays.asList(61, 34, 72,69)),
       "casual"
       ));
    }


    public void save(Product product) { //añaadir un producto al mapa
        long id = nextId.incrementAndGet();
        product.setId(id);
        products.put(id, product);
    }
    
    public void deleteByid(Long id){
        products.remove(id);
    }

    public Collection<Product> getProducts(){ //colection is an interface of all the collections(list, set, map)
        return products.values();
    }


    public Product getProductById(Long id){
        return products.get(id);
    }

}
