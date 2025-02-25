package com.stepx.stepx.controller;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;

import com.stepx.stepx.model.Product;
import com.stepx.stepx.model.Shoe;

import com.stepx.stepx.service.CartService;
import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.ShoeService;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/shop")
public class ShoeController {
    @Autowired
    private ProductsService productsService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ShoeService shoeService;

    
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @GetMapping()
    public String showShop(Model model) {

        List<Shoe> shoes = shoeService.getAllShoes();
        
        model.addAttribute("shoes", shoes);
        
        return "shop";
    }

     @GetMapping("/{id}/image/{imageNumber}")
    public ResponseEntity<Resource> getShoeImage(@PathVariable Long id, @PathVariable int imageNumber) {
        Optional<Shoe> op = shoeService.getShoeById(id);
        if (op.isPresent()) {
            Shoe shoe = op.get();
            Blob image = null;

            // Seleccionar la imagen según el número solicitado
            switch (imageNumber) {
                case 1 -> image = shoe.getImage1();
                case 2 -> image = shoe.getImage2();
                case 3 -> image = shoe.getImage3();
            }

            if (image != null) {
                try {
                    Resource file = new InputStreamResource(image.getBinaryStream());
                    System.out.println(image.length());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, "image/jpg")
                            .contentLength(image.length())
                            .body(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/single-product/{id}")
    public String showSingleProduct(Model model, @PathVariable Long id) {
        Optional<Shoe> op = shoeService.getShoeById(id);
        if (op.isPresent()) {
            Shoe shoe = op.get();
            model.addAttribute("product", shoe);
            return "single-product";
        }
        return "shop";
    }
    
}
