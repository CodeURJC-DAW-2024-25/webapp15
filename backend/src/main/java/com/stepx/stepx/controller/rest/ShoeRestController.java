package com.stepx.stepx.controller.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.stepx.stepx.dto.ReviewDTO;
import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.dto.ShoeSizeStockDTO;
import com.stepx.stepx.mapper.ShoeMapper;
import com.stepx.stepx.repository.UserRepository;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.ReviewService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.ShoeSizeStockService;
import com.stepx.stepx.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/Shoe")
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
     

    //get all shoes
    @GetMapping("/All")
    public ResponseEntity<?> getAllShoes() {
        List<ShoeDTO> shoes = shoeService.findAll();
        if (shoes.isEmpty()) {
            return ResponseEntity.status(404)
            .body(Collections.singletonMap("error", "No shoes found"));
        }
        return ResponseEntity.ok(shoes);
    }

    //get paged shoes
    @GetMapping
    public ResponseEntity<?> getPagedShoes(@RequestParam int page, @RequestParam int size) {
        Page<ShoeDTO> shoes = shoeService.getPagedShoes(page,size);
        if(shoes.isEmpty()){
            return ResponseEntity.status(404)
            .body(Collections.singletonMap("error", "No shoes found"));
        }
        return ResponseEntity.ok(shoes);
    }

    //get one shoe
    @GetMapping("/{shoeId}")
    public ResponseEntity<?> getOneShoe(@PathVariable Long shoeId) {
        Optional<ShoeDTO> shoeDTO = shoeService.getShoeById(shoeId);
        if (!shoeDTO.isPresent()) {
            return ResponseEntity.status(404)
            .body(Collections.singletonMap("error", "Shoe not found"));
        }
        
        return ResponseEntity.ok(shoeDTO.get());
    }

    //get image from shoe
    @GetMapping("/{shoeId}/image/{imageNumber}")
    public ResponseEntity<?> getImage(@PathVariable Long shoeId, @PathVariable int imageNumber) throws SQLException {
        Resource shoeImage=shoeService.getImage(shoeId, imageNumber);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
            .body(shoeImage);
    }
    
    
    //create a shoe
    @PostMapping()
    public ResponseEntity<?> createShoe(@RequestBody ShoeDTO shoeDTO) throws IOException, SQLException {
        ShoeDTO saved= shoeService.saveShoe(shoeDTO);
        URI location=ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    //create image for shoe
    @PostMapping("/{shoeId}/image/{imageNumber}")
    public ResponseEntity<?> loadImage(@PathVariable Long shoeId, @PathVariable int imageNumber,@RequestParam("file") MultipartFile file) throws IOException {
        
        URI location=ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

        shoeService.storeImage(shoeId,imageNumber,file.getInputStream(),file.getSize());

        return ResponseEntity.created(location).build();
    }
    
    //update shoe
    @PutMapping("/{shoeId}")
    public ResponseEntity<?> updateShoe(@PathVariable Long shoeId, @RequestBody ShoeDTO shoeDTO) {
        
        Optional<ShoeDTO> shoeOptional=shoeService.updateAllShoe(shoeId,shoeDTO);
        if (!shoeOptional.isPresent()) {
            return ResponseEntity.status(404)
            .body(Collections.singletonMap("error", "Shoe not found"));
        }

        return ResponseEntity.ok(shoeOptional.get());
    }
    

    @GetMapping
    public ResponseEntity<List<ShoeDTO>> getShoes(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(shoeService.getNineShoes(page).getContent());
        
    }


   
    @GetMapping("/create-product")
    public ResponseEntity<Map<String, Object>> getCreateProductData(HttpServletRequest request) {
        boolean admin = request.isUserInRole("ROLE_ADMIN");
        if (!admin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "You do not have permission to create products."));
        }

        // Aquí podrías devolver los datos necesarios para el formulario, si es necesario
        // Por ejemplo: categorías, marcas, etc.
        Map<String, Object> response = new HashMap<>();
        response.put("sizes", Arrays.asList("S", "M", "L", "XL"));
        response.put("categories", Arrays.asList("Sportswear", "Casual", "Outdoor"));
        response.put("brands", Arrays.asList("Nike", "Adidas", "Puma"));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteShoe(@PathVariable Long id) {
        shoeService.deleteShoe(id);
        return ResponseEntity.ok("Shoe deleted successfully.");
    }


  /* @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id, @RequestParam(required = false) String action, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Optional<ShoeDTO> product = shoeService.getShoeById(id);

        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
        }

        ShoeDTO shoe = product.get();
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
    }*/

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

    // @PostMapping("/single-product/loadMoreReviews")
    // public ResponseEntity<Map<String, Object>> loadMoreReviews(@RequestParam int page, @RequestParam Long shoeId) {
    //     int limit = 2;
    //     List<ReviewDTO> reviews = reviewService.getPagedReviewsByShoeId(shoeId, page, limit);

    //     Map<String, Object> response = new HashMap<>();
    //     response.put("reviews", reviews);

    //     return ResponseEntity.ok(response);
    // }

    /*@GetMapping("/{id}/image/{imageNumber}")
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
    }   */ 
    // @GetMapping("/single-product/{id}")
    // public ResponseEntity<Map<String, Object>> showSingleProduct(@PathVariable Long id, HttpServletRequest request) {
    //     Map<String, Object> response = new HashMap<>();
    //     Optional<ShoeDTO> op = shoeService.getShoeById(id);
    //     if (op.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
    //     }

    //     ShoeDTO shoe = op.get();
    //     response.put("product", shoe);

    //     // Fetching stock and reviews
    //     Optional<Integer> stockS = shoeSizeStockService.getStockByShoeAndSize(id, "S");
    //     Optional<Integer> stockM = shoeSizeStockService.getStockByShoeAndSize(id, "M");
    //     Optional<Integer> stockL = shoeSizeStockService.getStockByShoeAndSize(id, "L");
    //     Optional<Integer> stockXL = shoeSizeStockService.getStockByShoeAndSize(id, "XL");

    //     int initialReviewsCount = 2;
    //     List<ReviewDTO> reviews = reviewService.getPagedReviewsByShoeId(id, 0, initialReviewsCount);

    //     response.put("stockS", stockS.orElse(0) == 0);
    //     response.put("stockM", stockM.orElse(0) == 0);
    //     response.put("stockL", stockL.orElse(0) == 0);
    //     response.put("stockXL", stockXL.orElse(0) == 0);
    //     response.put("reviews", reviews);
    //     response.put("hasReviews", reviews != null && !reviews.isEmpty());

    //     return ResponseEntity.ok(response);
    // }

    // @GetMapping("/shop")
    // public ResponseEntity<Page<ShoeDTO>> getAllProducts() {
    //     Page<ShoeDTO> shoes = shoeService.getNineShoes(0);
    //         boolean more = shoes.getTotalPages() > 1;
            
    //     return ResponseEntity.ok(shoes);
    // }
}
 
    

