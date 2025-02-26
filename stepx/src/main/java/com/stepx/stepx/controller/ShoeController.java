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
import com.stepx.stepx.model.ShoeSizeStock;
import com.stepx.stepx.service.CartService;
import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.ShoeSizeStockService;

import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/shop")
public class ShoeController {
    @Autowired
    private ProductsService productsService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ShoeSizeStockService shoeSizeStockService;

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

    @GetMapping("/{id}")
    public String getProductById(Model model, @PathVariable Long id, @RequestParam(required = false) String action) {
        
        Optional<Shoe> product = shoeService.getShoeById(id);

        if (product.isEmpty()) {
            model.addAttribute("error", "Product not found");
            return "partials/error-modal"; 
        }

        model.addAttribute("product", product.get()); //importat to do a get from the Optional that the service returns

        
        if ("quick".equals(action)) {
            return "partials/quick-view-modal";  
        } else if ("confirmation".equals(action)) {

            //need a confirmation if the stock of the default size is 0
            Optional <ShoeSizeStock> stock= shoeSizeStockService.getStockByShoeAndSize(id, "M");

            if(stock.isPresent()&&stock.get().getStock()==0){
                   model.addAttribute("error", true);
            }
            return "partials/cart-confirmation-view"; 
        } else {
            return "partials/error-modal";
        }
    }
    
}
