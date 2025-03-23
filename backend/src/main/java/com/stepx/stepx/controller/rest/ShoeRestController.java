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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.stepx.stepx.dto.OrderItemDTO;
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
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/shoe")
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
    public ResponseEntity<List<ShoeDTO>> getShoes(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(shoeService.getNineShoes(page).getContent());
        
    }

    private String convertToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }


    @PostMapping
    public ResponseEntity<ShoeDTO> createShoe(@Valid @RequestBody ShoeDTO shoeDTO) throws SQLException {
        ShoeDTO savedDTO =shoeService.saveShoe(shoeDTO);
        if (savedDTO ==null) {
            return ResponseEntity.badRequest().build();
        }
        URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(savedDTO.id())
        .toUri();
    return ResponseEntity.created(location).body(savedDTO);
    }

     @PutMapping("/{id}")
     public ResponseEntity<ShoeDTO> updateShoe(@RequestBody ShoeDTO shoeDTO) {
         Optional<ShoeDTO> shoe = shoeService.updateAllShoe(shoeDTO);//get the orderitem
         if (shoe.isEmpty()) {
             return ResponseEntity.notFound().build();
         }
         return ResponseEntity.ok(shoe.get());
     }

     //delete a orderitem
    @DeleteMapping("/{id}")
    public ResponseEntity<ShoeDTO> deleteShoe(@PathVariable Long id) {
        Optional<ShoeDTO> deleted = shoeService.deleteShoe(id);//implement function
        return deleted.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
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

    // @DeleteMapping("/delete/{id}")
    // public ResponseEntity<String> deleteShoe(@PathVariable Long id) {
    //     shoeService.deleteShoe(id);
    //     return ResponseEntity.ok("Shoe deleted successfully.");
    // }


    @GetMapping("/{id}")
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
    }

    
    @PostMapping("/single-product/loadMoreReviews")
    public ResponseEntity<Map<String, Object>> loadMoreReviews(@RequestParam int page, @RequestParam Long shoeId) {
        int limit = 2;
        List<ReviewDTO> reviews = reviewService.getPagedReviewsByShoeId(shoeId, page, limit);

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
        Optional<ShoeDTO> op = shoeService.getShoeById(id);
        if (op.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
        }

        ShoeDTO shoe = op.get();
        response.put("product", shoe);

        // Fetching stock and reviews
        Optional<Integer> stockS = shoeSizeStockService.getStockByShoeAndSize(id, "S");
        Optional<Integer> stockM = shoeSizeStockService.getStockByShoeAndSize(id, "M");
        Optional<Integer> stockL = shoeSizeStockService.getStockByShoeAndSize(id, "L");
        Optional<Integer> stockXL = shoeSizeStockService.getStockByShoeAndSize(id, "XL");

        int initialReviewsCount = 2;
        List<ReviewDTO> reviews = reviewService.getPagedReviewsByShoeId(id, 0, initialReviewsCount);

        response.put("stockS", stockS.orElse(0) == 0);
        response.put("stockM", stockM.orElse(0) == 0);
        response.put("stockL", stockL.orElse(0) == 0);
        response.put("stockXL", stockXL.orElse(0) == 0);
        response.put("reviews", reviews);
        response.put("hasReviews", reviews != null && !reviews.isEmpty());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/shop")
    public ResponseEntity<Page<ShoeDTO>> getAllProducts() {
        Page<ShoeDTO> shoes = shoeService.getNineShoes(0);
            boolean more = shoes.getTotalPages() > 1;
            
        return ResponseEntity.ok(shoes);
    }
}
 
    

