package com.stepx.stepx.controller.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stepx.stepx.dto.BasicShoeDTO;
import com.stepx.stepx.dto.BasicShoeSizeStockDTO;
import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.mapper.ShoeMapper;
import com.stepx.stepx.model.Review;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.ShoeSizeStock;
import com.stepx.stepx.repository.UserRepository;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.ReviewService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.ShoeSizeStockService;
import com.stepx.stepx.service.UserService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api")
public class ShoeRestController {

    @Autowired
    private ShoeService shoeService;

    @Autowired
    private ShoeSizeStockService shoeSizeStockService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired ShoeMapper shoeMapper;
     

    @GetMapping
    public ResponseEntity<List<Shoe>> getShoes(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(shoeService.getNineShoes(page).getContent());
        
    }

    // @PostMapping("/create")
    // public ResponseEntity<String> createShoe(
    //         @RequestParam String name,
    //         @RequestParam String shortDescription,
    //         @RequestParam String longDescription,
    //         @RequestParam BigDecimal price,
    //         @RequestParam(required = false) MultipartFile image1,
    //         @RequestParam(required = false) MultipartFile image2,
    //         @RequestParam(required = false) MultipartFile image3,
    //         @RequestParam String brand,
    //         @RequestParam String category) throws IOException, SQLException {

    //     Shoe shoe = new Shoe();
    //     shoe.setName(name);
    //     shoe.setDescription(shortDescription);
    //     shoe.setPrice(price);
    //     shoe.setBrand(Shoe.Brand.valueOf(brand));
    //     shoe.setCategory(Shoe.Category.valueOf(category));
    //     shoe.setLongDescription(longDescription);

    //     if (image1 != null && !image1.isEmpty()) {
    //         shoe.setImage1(new SerialBlob(image1.getBytes()));
    //     }
    //     if (image2 != null && !image2.isEmpty()) {
    //         shoe.setImage2(new SerialBlob(image2.getBytes()));
    //     }
    //     if (image3 != null && !image3.isEmpty()) {
    //         shoe.setImage3(new SerialBlob(image3.getBytes()));
    //     }

    //     shoeService.saveShoe(shoe);

    //     for (String size : new String[]{"S", "M", "L", "XL"}) {
    //         ShoeSizeStock stock = new ShoeSizeStock();
    //         stock.setShoe(shoe);
    //         stock.setSize(size);
    //         stock.setStock(10);
    //         shoeSizeStockService.saveStock(stock);
    //     }

    //     return ResponseEntity.ok("Shoe created successfully.");
    // }

    // @DeleteMapping("/delete/{id}")
    // public ResponseEntity<String> deleteShoe(@PathVariable Long id) {
    //     shoeService.deleteShoe(id);
    //     return ResponseEntity.ok("Shoe deleted successfully.");
    // }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id, @RequestParam(required = false) String action, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Optional<Shoe> product = shoeService.getShoeById(id);

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
        }

        Shoe shoe = product.get();
        response.put("product", shoe);

        if ("quick".equals(action)) {
            response.put("view", "quick-view-modal");
        } else if ("confirmation".equals(action)) {
            Optional<Integer> stock = shoeSizeStockService.getStockByShoeAndSize(id, "M");
            response.put("stockError", stock.isPresent() && stock.get() == 0);
            response.put("view", "cart-confirmation-view");
        } else if ("delete".equals(action)) {
            response.put("view", "deleteShoeModal");
        } else {
            response.put("view", "error-modal");
        }

        return ResponseEntity.ok(response);
    }

    // @GetMapping("/loadMoreShoes/")
    // public ResponseEntity<Map<String, Object>> getMore(@RequestParam int currentPage, HttpServletRequest request) {
    //     Page<Shoe> shoePage = shoeService.getShoesPaginated(currentPage);
    //     boolean more = currentPage < shoePage.getTotalPages() - 1;

    //     Map<String, Object> response = new HashMap<>();
    //     response.put("hasMoreShoes", more);
    //     response.put("shoes", shoePage.getContent());

    //     return ResponseEntity.ok(response);
    // }

    // @GetMapping("/{userId}/imageUser")
    // public ResponseEntity<Resource> getProfileImage(@PathVariable Long userId, HttpServletRequest request) {
    //     Optional<User> userOptional = userService.findUserById(userId);

    //     if (userOptional.isPresent()) {
    //         User user = userOptional.get();
    //         Blob image = user.getImageUser();

    //         if (image != null) {
    //             try {
    //                 Resource file = new InputStreamResource(image.getBinaryStream());
    //                 return ResponseEntity.ok()
    //                         .header(HttpHeaders.CONTENT_TYPE, "image/jpg")
    //                         .contentLength(image.length())
    //                         .body(file);
    //             } catch (Exception e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //     }

    //     return ResponseEntity.notFound().build();
    // }

    // @PostMapping("/submit/{id}")
    // public ResponseEntity<Map<String, Object>> publishReview(@PathVariable Long id, @RequestParam("rating") int rating,
    //         @RequestParam String description, HttpServletRequest request) {
    //     Map<String, Object> response = new HashMap<>();
    //     String username = request.getUserPrincipal().getName();
    //     User user = userRepository.findByUsername(username).orElseThrow();
    //     Shoe shoe = shoeService.getShoeById(id).orElseThrow(() -> new RuntimeException("Shoe not found"));
    //     LocalDate date = LocalDate.now();
    //     Review review = new Review(rating, description, shoe, user, date);
    //     reviewService.save(review);

    //     response.put("message", "Review published successfully");
    //     return ResponseEntity.ok(response);
    // }

    // @GetMapping("/{productId}/deleteReview/{id}")
    // public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long productId, @PathVariable Long id, HttpServletRequest request) {
    //     Map<String, Object> response = new HashMap<>();
    //     reviewService.deleteReview(id);

    //     List<Review> reviews = reviewService.getReviewsByShoe(productId);
    //     response.put("reviews", reviews);

    //     return ResponseEntity.ok(response);
    // }

    @PostMapping("/single-product/loadMoreReviews")
    public ResponseEntity<Map<String, Object>> loadMoreReviews(@RequestParam int page, @RequestParam Long shoeId) {
        int limit = 2;
        List<Review> reviews = reviewService.getPagedReviewsByShoeId(shoeId, page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviews);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/image/{imageNumber}")
    public ResponseEntity<Resource> getShoeImage(@PathVariable Long id, @PathVariable int imageNumber) {
        try {
            Resource imageResource = shoeService.getShoeImage(id, imageNumber);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg") // Cambia a dinámico si es necesario
                    .body(imageResource);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SQLException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }    
    @GetMapping("/single-product/{id}")
    public ResponseEntity<Map<String, Object>> showSingleProduct(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Optional<Shoe> op = shoeService.getShoeById(id);
        if (op.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
        }

        Shoe shoe = op.get();
        response.put("product", shoe);

        // Fetching stock and reviews
        Optional<Integer> stockS = shoeSizeStockService.getStockByShoeAndSize(id, "S");
        Optional<Integer> stockM = shoeSizeStockService.getStockByShoeAndSize(id, "M");
        Optional<Integer> stockL = shoeSizeStockService.getStockByShoeAndSize(id, "L");
        Optional<Integer> stockXL = shoeSizeStockService.getStockByShoeAndSize(id, "XL");

        int initialReviewsCount = 2;
        List<Review> reviews = reviewService.getPagedReviewsByShoeId(id, 0, initialReviewsCount);

        response.put("stockS", stockS.orElse(0) == 0);
        response.put("stockM", stockM.orElse(0) == 0);
        response.put("stockL", stockL.orElse(0) == 0);
        response.put("stockXL", stockXL.orElse(0) == 0);
        response.put("reviews", reviews);
        response.put("hasReviews", reviews != null && !reviews.isEmpty());

        return ResponseEntity.ok(response);
    }
}
 
    

