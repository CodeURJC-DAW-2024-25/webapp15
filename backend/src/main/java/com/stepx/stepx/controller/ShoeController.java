package com.stepx.stepx.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.stepx.stepx.model.Review;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.ShoeSizeStock;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.UserRepository;
import com.stepx.stepx.service.ReviewService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.ShoeSizeStockService;
import com.stepx.stepx.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/shop")
public class ShoeController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShoeSizeStockService shoeSizeStockService;

    @Autowired
    private ShoeService shoeService;

    @Autowired
    private UserRepository userRepository;

    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("showError", false);

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", csrfToken.getToken());

        model.addAttribute("headerName", csrfToken.getHeaderName());

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            model.addAttribute("username", username);

            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

            User user = userRepository.findByUsername(username).get();

            model.addAttribute("id", user.getId());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("imageBlob", user.getImageUser());
            model.addAttribute("lastName", user.getLastName());
            model.addAttribute("firstname", user.getFirstName());
            model.addAttribute("user_id", user.getId());

        }
    }

    @GetMapping()
    public String showShop(Model model, HttpServletRequest request) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");

        model.addAttribute("token", csrfToken.getToken());
        model.addAttribute("headerName", csrfToken.getHeaderName());

        Page<Shoe> shoes = shoeService.getNineShoes(0);
        boolean more = 0 < shoes.getTotalPages() - 1;
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        model.addAttribute("shoes", shoes.getContent());
        model.addAttribute("hasMoreShoes", more);
        return "shop";
    }

    @GetMapping("/resetFilters")
    public String resetFilters(Model model, HttpServletRequest request) {
        Page<Shoe> shoes = shoeService.getNineShoes(0);
        boolean more = 0 < shoes.getTotalPages() - 1;

        boolean isAuthenticated = request.getUserPrincipal() != null;// if its registered at least
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            User user = userRepository.findByUsername(username).orElseThrow();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        }

        model.addAttribute("shoes", shoes.getContent());
        model.addAttribute("hasMoreShoes", more);
        return "partials/loadMoreShoe"; // Return only part of the products!
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
            @RequestParam String category,
            HttpServletRequest request, Model model) throws IOException, SQLException {

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if (isAuthenticated) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

        }
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

        shoeService.saveShoe(shoe);

        ShoeSizeStock stock1 = new ShoeSizeStock();
        stock1.setShoe(shoe);
        stock1.setSize("S");
        stock1.setStock(10);
        shoeSizeStockService.saveStock(stock1);
        ShoeSizeStock stock2 = new ShoeSizeStock();
        stock2.setShoe(shoe);
        stock2.setSize("M");
        stock2.setStock(10);
        shoeSizeStockService.saveStock(stock2);
        ShoeSizeStock stock3 = new ShoeSizeStock();
        stock3.setShoe(shoe);
        stock3.setSize("L");
        stock3.setStock(10);
        shoeSizeStockService.saveStock(stock3);
        ShoeSizeStock stock4 = new ShoeSizeStock();
        stock4.setShoe(shoe);
        stock4.setSize("XL");
        stock4.setStock(10);
        shoeSizeStockService.saveStock(stock4);

        return "redirect:/shop"; // Redirect to shop page after creation
    }

    @PostMapping("/delete/{id}")
    public String deleteShoe(@PathVariable Long id, Model model, HttpServletRequest request) {

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if (isAuthenticated) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

        }

        shoeService.deleteShoe(id);

        return "redirect:/shop";
    }

    @GetMapping("/{id}/image/{imageNumber}")
    public ResponseEntity<Resource> getShoeImage(@PathVariable Long id, @PathVariable int imageNumber, Model model,
            HttpServletRequest request) {
        Optional<Shoe> op = shoeService.getShoeById(id);

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            User user = userRepository.findByUsername(username).orElseThrow();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        }

        if (op.isPresent()) {
            Shoe shoe = op.get();
            Blob image = switch (imageNumber) {
                case 1 -> shoe.getImage1();
                case 2 -> shoe.getImage2();
                case 3 -> shoe.getImage3();
                default -> null;
            };

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

    @GetMapping("/single-product/{id}")
    public String showSingleProduct(Model model, @PathVariable Long id, HttpServletRequest request) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", csrfToken.getToken());
        model.addAttribute("headerName", csrfToken.getHeaderName());

        long maxItems = shoeService.getTotalShoes();

        if (maxItems < id) {
            return "redirect:/errorPage?errorType=greaterId";
        }

        boolean isAuthenticated = request.getUserPrincipal() != null;
        // model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            User user = userRepository.findByUsername(username).orElseThrow();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

        }

        Optional<Shoe> op = shoeService.getShoeById(id);
        Optional<Integer> stockS = shoeSizeStockService.getStockByShoeAndSize(id, "S");
        Optional<Integer> stockM = shoeSizeStockService.getStockByShoeAndSize(id, "M");
        Optional<Integer> stockL = shoeSizeStockService.getStockByShoeAndSize(id, "L");
        Optional<Integer> stockXL = shoeSizeStockService.getStockByShoeAndSize(id, "XL");
        int initialReviewsCount = 2;

        List<Review> reviews = reviewService.getPagedReviewsByShoeId(id, 0, initialReviewsCount);

        if (op.isPresent()) {
            Shoe shoe = op.get();
            model.addAttribute("stockS", stockS.orElse(0) == 0);
            model.addAttribute("stockM", stockM.orElse(0) == 0);
            model.addAttribute("stockL", stockL.orElse(0) == 0);
            model.addAttribute("stockXL", stockXL.orElse(0) == 0);
            model.addAttribute("product", shoe);
            if (reviews != null) {
                model.addAttribute("review", reviews);
                model.addAttribute("hasReviews", true);

            }else{
                model.addAttribute("hasReviews", false);
            }
            return "single-product";

        }
        return "shop";
    }

    @GetMapping("/{id}")
    public String getProductById(Model model, @PathVariable Long id, @RequestParam(required = false) String action,
            HttpServletRequest request) {

        boolean isAuthenticated = request.getUserPrincipal() != null;// false if user doesnt exist(not registered)
        model.addAttribute("isAuthenticated", isAuthenticated);// true if is registered at least
        Optional<Shoe> product = shoeService.getShoeById(id);// obtain shoe

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            User user = userRepository.findByUsername(username).orElseThrow();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));// if user is admin or not
        }

        if (product.isEmpty()) {
            model.addAttribute("error", "Product not found");
            return "partials/error-modal";
        }

        model.addAttribute("product", product.get()); // importat to do a get from the Optional that the service returns

        if ("quick".equals(action)) {

            return "partials/quick-view-modal";

        } else if ("confirmation".equals(action)) {
            // need a confirmation if the stock of the default size is 0
            Optional<Integer> stock = shoeSizeStockService.getStockByShoeAndSize(id, "M");
            if (stock.isPresent() && stock.get() == 0) {
                model.addAttribute("error", true);
            }
            return "partials/cart-confirmation-view";

        } else if ("delete".equals(action)) {
            return "partials/deleteShoeModal";
        } else {
            return "partials/error-modal";
        }
    }

    @GetMapping("/loadMoreShoes/")
    public String getMore(Model model, @RequestParam int currentPage, HttpServletRequest request) {

        Page<Shoe> shoePage = shoeService.getShoesPaginated(currentPage);
        boolean more = currentPage < shoePage.getTotalPages() - 1;
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        model.addAttribute("hasMoreShoes", more);
        model.addAttribute("shoes", shoePage.getContent());

        return "partials/loadMoreShoe";
    }

    @GetMapping("/{userId}/imageUser")
    public ResponseEntity<Resource> getProfileImage(@PathVariable Long userId, Model model,
            HttpServletRequest request) {
        Optional<User> userOptional = userService.findUserById(userId);

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if (userOptional.isPresent()) {
            Blob image = user.getImageUser();

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


    @GetMapping("/{userId}/imageUserReview")
    public ResponseEntity<Resource> getProfileImageForReview(@PathVariable Long userId, Model model,
            HttpServletRequest request) {
        Optional<User> userOptional = userService.findUserById(userId);

        if (userOptional.isPresent()) {
            User user = userRepository.findByUsername(userOptional.get().getUsername()).orElseThrow();
            Blob image = user.getImageUser(); 

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

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        Optional<Shoe> op = shoeService.getShoeById(id);
        model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        if (op.isPresent()) {
            model.addAttribute("shoe", op.get());
            return "edit-product"; // Name of the edit form template
        }
        return "redirect:/shop"; // Redirect to shop page if shoe not found
    }

    @PostMapping("/edit/{id}")
    public String updateShoe(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description, // Ensure this matches the form
            @RequestParam String LongDescription,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) MultipartFile image1,
            @RequestParam(required = false) MultipartFile image2,
            @RequestParam(required = false) MultipartFile image3,
            @RequestParam String brand,
            @RequestParam String category, HttpServletRequest request, Model model) throws IOException, SQLException {

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        Optional<Shoe> op = shoeService.getShoeById(id);
        if (op.isPresent()) {
            Shoe shoe = op.get();
            shoe.setName(name);
            shoe.setDescription(description); // This should match 'description'
            shoe.setLongDescription(LongDescription);
            shoe.setPrice(price);
            shoe.setBrand(Shoe.Brand.valueOf(brand));
            shoe.setCategory(Shoe.Category.valueOf(category));

            // Update images if new files are provided
            if (image1 != null && !image1.isEmpty()) {
                shoe.setImage1(new javax.sql.rowset.serial.SerialBlob(image1.getBytes()));
            }
            if (image2 != null && !image2.isEmpty()) {
                shoe.setImage2(new javax.sql.rowset.serial.SerialBlob(image2.getBytes()));
            }
            if (image3 != null && !image3.isEmpty()) {
                shoe.setImage3(new javax.sql.rowset.serial.SerialBlob(image3.getBytes()));
            }

            shoeService.saveShoe(shoe);
        }

        return "redirect:/shop";
    }

    @GetMapping("/getByBrand") // first 9 shoes of same brand
    public String getByBrand(@RequestParam String brand, Model model, HttpServletRequest request) {
        try {
            int currentPage = 0;
            Page<Shoe> shoes = shoeService.getShoesByBrand(currentPage, brand);
            boolean more = currentPage <= shoes.getTotalPages() - 1;
            boolean isAuthenticated = request.getUserPrincipal() != null;
            model.addAttribute("isAuthenticated", isAuthenticated);

            if (isAuthenticated) {
                String username = request.getUserPrincipal().getName();
                User user = userRepository.findByUsername(username).orElseThrow();
                model.addAttribute("username", user.getUsername());
                model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

            }

            model.addAttribute("hasMoreShoes", more);
            model.addAttribute("shoes", shoes.getContent());
            return "partials/loadMoreShoe";
        } catch (IllegalArgumentException e) {
            return "error";
        }
    }

    @GetMapping("/getByCategory") // first 9 shoes of same category
    public String getByCategory(@RequestParam String category, Model model, HttpServletRequest request) {
        try {
            int currentPage = 0;
            Page<Shoe> shoes = shoeService.getShoesByCategory(currentPage, category);
            boolean more = currentPage <= shoes.getTotalPages() - 1;

            boolean isAuthenticated = request.getUserPrincipal() != null;
            model.addAttribute("isAuthenticated", isAuthenticated);

            model.addAttribute("shoes", shoes.getContent());
            model.addAttribute("hasMoreShoes", more);
            return "partials/loadMoreShoe";

        } catch (IllegalArgumentException e) {
            System.err.println("Error: categoria no válida: " + category);
            return "error"; // if enum is not valid returns a view
        }

    }

    @GetMapping("/loadMoreShoesByBrand")
    public String getMoreByBrand(@RequestParam String brand, Model model, @RequestParam int currentPage,
            HttpServletRequest request) {
        Page<Shoe> paginatedShoe = shoeService.getShoesPaginatedByBrand(currentPage, brand);
        boolean more = currentPage < paginatedShoe.getTotalPages() - 1;

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        model.addAttribute("shoes", paginatedShoe.getContent());
        model.addAttribute("hasMoreShoes", more);
        return "partials/loadMoreShoe";
    }

    @GetMapping("/loadMoreShoesByCategory")
    public String loadMoreShoesByCategory(@RequestParam String category, @RequestParam int currentPage, Model model,
            HttpServletRequest request) {
        Page<Shoe> paginatedShoes = shoeService.getShoesPaginatedByCategory(currentPage, category);
        boolean more = currentPage < paginatedShoes.getTotalPages() - 1;

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            String username = request.getUserPrincipal().getName();
            User user = userRepository.findByUsername(username).orElseThrow();
            model.addAttribute("username", user.getUsername());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));

        }

        model.addAttribute("shoes", paginatedShoes.getContent());
        model.addAttribute("hasMoreShoes", more);
        return "partials/loadMoreShoe";
    }

    @PostMapping("/submit/{id}")
    public String publishReview(
            @PathVariable Long id, // ID of the shoe
            @RequestParam("rating") int rating,
            @RequestParam String description, Model model, HttpServletRequest request
    ) {
        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // search the shoe by id
        Shoe shoe = shoeService.getShoeById(id).orElseThrow(() -> new RuntimeException("Shoe not found"));
        LocalDate date;
        date = LocalDate.now();
        // Create new review
        Review review = new Review(rating, description, shoe, user, date);

        // Saving the review
        reviewService.save(review);

        return "redirect:/shop/single-product/" + id + "?page=0";

    }

    @GetMapping("/{productId}/deleteReview/{id}")
    public String deleteReview(@PathVariable Long productId, @PathVariable Long id, Model model,
            HttpServletRequest request) {

        boolean isAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if (isAuthenticated) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("admin", request.isUserInRole("ROLE_ADMIN"));
        }

        reviewService.deleteReview(id);

        List<Review> review = reviewService.getReviewsByShoe(productId);
        if (review != null) {
            model.addAttribute("review", review);
            return "partials/singleProduct-reviewList";
        }
        return "error";

    }

    @PostMapping("/single-product/loadMoreReviews")
    public String loadMoreReviews(@RequestParam int page, @RequestParam Long shoeId, Model model) {
        int limit = 2;
        List<Review> reviews = reviewService.getPagedReviewsByShoeId(shoeId, page, limit);
        model.addAttribute("review", reviews);

        return "partials/singleProduct-reviewList";
    }

}
