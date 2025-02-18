package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.stepx.stepx.model.product;

@Service
public class ProductsService {
    
    private ConcurrentMap<Long, product> products= new ConcurrentHashMap<>();//allows to use the map in a concurrent way
    private AtomicLong nextId = new AtomicLong();//allows to work with the id in a concurrent way

    public ProductsService(){
        save(new product("Nike Air Yellow",
        "The Nike Air Max 270 React ENG combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort and a striking visual experience.",
        "The shoes upper features lightweight, no-sew materials that create a modern aesthetic that looks as good as it feels.",
        90,
        "Nike",
        new ArrayList<String>(Arrays.asList("/images/shopimages/Yellow-nike-shop-shoe.jpg"))
        ));

        save(new product("Red Nike Runnin", "Perfect for run long distances",
        "the style and confy of the shoe is somthing that most of the brands would like to have",
        100,
        "nike",
        new ArrayList<>(Arrays.asList("/images/shopimages/Red-nike-shop-shoe.jpg"))));
        
        save(new product("coloredPair", "Perfect for run long distances",
        "the style and confy of the shoe is somthing that most of the brands would like to have",
        70,
        "reebok",
        new ArrayList<>(Arrays.asList("/images/shopimages/paircolored-shop-shoe.jpg"))));

       save(new product("orange-white-shoe", "confy", "stylish and moder shoe for mature people", 80, "nike", 
       new ArrayList<>(Arrays.asList("/images/shopimages/orange-white-shop-shoe.jpg"))));

       save(new product("pink-newbalance-shoe", "stylish", "suitable shoe for most of the world", 60, "new balance", 
       new ArrayList<>(Arrays.asList("/images/shopimages/pink-newbalance-shoe.jpg"))));

       save(new product("Nike-Air-Pink",
       "The Nike Air Max 270 React ENG combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort and a striking visual experience.",
       "The shoes upper features lightweight, no-sew materials that create a modern aesthetic that looks as good as it feels.",
       99,
       "Nike",
       new ArrayList<String>(Arrays.asList("/images/shopimages/Nike-Air-Pink.jpg"))
       ));

       save(new product("red-converse-shoe", "confy", "stylish and moder shoe for mature people", 120, "converse", 
       new ArrayList<>(Arrays.asList("/images/shopimages/red-converse-shoe.jpg"))));


       save(new product("Nike-Air-multicolor",
       "The Nike Air Max 270 React ENG combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort and a striking visual experience.",
       "The shoes upper features lightweight, no-sew materials that create a modern aesthetic that looks as good as it feels.",
       85,
       "Nike",
       new ArrayList<String>(Arrays.asList("/images/shopimages/Nike-Air-multicolor.jpg"))
       ));


       save(new product("Nike-Air-grey",
       "The Nike Air Max 270 React ENG combines a full-length React foam midsole with a 270 Max Air unit for unrivaled comfort and a striking visual experience.",
       "The shoes upper features lightweight, no-sew materials that create a modern aesthetic that looks as good as it feels.",
       134,
       "Nike",
       new ArrayList<String>(Arrays.asList("/images/shopimages/Nike-Air-grey.jpg"))
       ));
    }

    public void save(product product) { //añaadir un producto al mapa
        long id = nextId.incrementAndGet();
        product.setId(id);
        products.put(id, product);
    }
    
    public void deleteByid(Long id){
        products.remove(id);
    }

    public Collection<product> getProducts(){ //colection is an interface of all the collections(list, set, map)
        return products.values();
    }

    public product getProductById(Long id){
        return products.get(id);
    }

}
