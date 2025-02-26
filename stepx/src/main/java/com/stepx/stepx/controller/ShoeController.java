package com.stepx.stepx.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.stepx.stepx.repository.ShoeSizeStockRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;

import com.stepx.stepx.model.User;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.Review;
import com.stepx.stepx.model.ShoeSizeStock;
import com.stepx.stepx.service.CartService;
import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.ReviewService;
import com.stepx.stepx.service.UserService;
import com.stepx.stepx.service.ShoeSizeStockService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/shop")
public class ShoeController {
    @Autowired
    private ProductsService productsService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

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

    @PostMapping("/create")
    public String createShoe(
            @RequestParam String name,
            @RequestParam String ShortDescription,
            @RequestParam String LongDescription,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) MultipartFile image1,
            @RequestParam(required = false) MultipartFile image2,
            @RequestParam(required = false) MultipartFile image3,
            @RequestParam String brand,
            @RequestParam String category) throws IOException, SQLException {

        // Create a new Shoe object
        Shoe shoe = new Shoe();
        shoe.setName(name);
        shoe.setDescription(ShortDescription);
        shoe.setPrice(price);
        shoe.setBrand(Shoe.Brand.valueOf(brand));
        shoe.setCategory(Shoe.Category.valueOf(category));
        shoe.setLongDescription(LongDescription);

        // Convert images to Blob and set them
        if (image1 != null && !image1.isEmpty()) {
            shoe.setImage1(new SerialBlob(image1.getBytes()));
        }
        if (image2 != null && !image2.isEmpty()) {
            shoe.setImage2(new SerialBlob(image2.getBytes()));
        }
        if (image3 != null && !image3.isEmpty()) {
            shoe.setImage3(new SerialBlob(image3.getBytes()));
        }

        // Save the shoe to the database
        shoeService.saveShoe(shoe);

        ShoeSizeStock stock = new ShoeSizeStock();
        stock.setShoe(shoe);
        stock.setSize("M");
        stock.setStock(10);
        shoeSizeStockService.saveStock(stock);

        return "redirect:/shop"; // Redirect to shop page after creation
    }

    @PostMapping("/delete/{id}")
    public String deleteShoe(@PathVariable Long id) {

        shoeService.deleteShoe(id);

        return "redirect:/shop";
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
        Optional<Integer> stockS=shoeSizeStockService.getStockByShoeAndSize(id, "S");
        Optional<Integer> stockM=shoeSizeStockService.getStockByShoeAndSize(id, "M");
        Optional<Integer> stockL=shoeSizeStockService.getStockByShoeAndSize(id, "L");
        Optional<Integer> stockXL=shoeSizeStockService.getStockByShoeAndSize(id, "XL");
        List<Review> review = reviewService.getReviewsByShoe(id);
        if (op.isPresent()) {
            Shoe shoe = op.get();
            model.addAttribute("stockS", stockS.get()==0);
            model.addAttribute("stockM", stockM.get()==0);
            model.addAttribute("stockL", stockL.get()==0);
            model.addAttribute("stockXL", stockXL.get()==0);
            model.addAttribute("product", shoe);
            if (review != null) {
                model.addAttribute("review", review);

            } else {
                System.out.println("no hay lista de reviews");
            }
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

        model.addAttribute("product", product.get()); // importat to do a get from the Optional that the service returns

        if ("quick".equals(action)) {
            return "partials/quick-view-modal";
        } else if ("confirmation".equals(action)) {

            //need a confirmation if the stock of the default size is 0
            Optional <Integer> stock= shoeSizeStockService.getStockByShoeAndSize(id, "M");

            if(stock.isPresent()&&stock.get().getStock()==0){
                   model.addAttribute("error", true);
            }
            return "partials/cart-confirmation-view";
        } else {
            return "partials/error-modal";
        }
    }

    @GetMapping("/loadMoreShoes/")
    public String getMore( Model model,@RequestParam int currentPage) {

        Page<Shoe> shoePage=shoeService.getShoesPaginated(currentPage);
        boolean more= currentPage< shoePage.getTotalPages()-1;
        System.out.println(more);
        model.addAttribute("hasMoreShoes",more);
        model.addAttribute("shoes", shoePage.getContent());
        return "partials/loadMoreShoe";
    }
    
    @GetMapping("/{userId}/imageUser")
    public ResponseEntity<Resource> getProfileImage(@PathVariable Long userId) {
        Optional<User> userOptional = userService.findUserById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Blob image = user.getImageUser(); // Asegúrate de tener este método en User
    
            if (image != null) {
                try {
                    Resource file = new InputStreamResource(image.getBinaryStream());
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

}
